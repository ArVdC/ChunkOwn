package net.vdcraft.arvdc.terrains;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

/**
 * Executes Player Commands
 *
 * @author ArVdC
 */
public class TrCommand implements CommandExecutor {
	
	/****************
	*** VARIABLES ***
	****************/
	
    public static String command;
    public static boolean wgSupport;
    public static boolean dmSupport;
    public static String dmTrack;
    public static String dmLabel;
    public static String dmMarkerName;
    public static String dmMarkerIcon;
    public static int dmLayer;
    public static long effectDuration;
    public static String domicileSetCommand;
    public static String domicileClearCommand;
    private static enum Action { RL, RELOAD, HELP, INFO, ACHETER, VENDRE, TOUTVENDRE, LISTE, PARTAGEPLUS, PARTAGEMOINS, PARTAGELISTE, STATUTPUBLIC, STATUTNORMAL, STATUTPRIVE, ALARM, DOMICILE, DOMICILESET };
    private static HashMap<Player, LinkedList<Location>> terrainIsFree = new HashMap<Player, LinkedList<Location>>();
    private static HashMap<Player, LinkedList<Location>> terrainIsOwned = new HashMap<Player, LinkedList<Location>>();
    private static HashMap<Player, LinkedList<Location>> terrainIsMine = new HashMap<Player, LinkedList<Location>>();
    public static HashMap<UUID, PermissionAttachment> tempAttachmentList = new HashMap<UUID, PermissionAttachment>();
    public static HashMap<UUID, PermissionAttachment> domicileAttachmentList = new HashMap<UUID, PermissionAttachment>();

    /**
     * Listens for Terrains commands to execute them
     *
     * @param sender The CommandSender who may not be a Player
     * @param command The command that was executed
     * @param alias The alias that the sender used
     * @param args The arguments for the command
     * @return true always
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        
        // Cancel if the command is not from a Player, except for reloading
        if (!(sender instanceof Player)) {        	
        	//Let the console only can use the reload command
            if (args.length == 1 && (args[0].toLowerCase().equals("rl") || (args[0].toLowerCase().equals("reload")))) {
                Terrains.rl();
            }
            return true;
        }

        Player player = (Player) sender;
        
        //Display help page if the Player did not add any arguments
        if (args.length == 0) {
            player.sendMessage(TrMsg.shortHelpMsg);
            return true;
        }
        
        //Cancel if the Player is in a disabled World
        if ((args[0].toLowerCase().equals("domicile") && !Terrains.tpEnableInWorld(player.getWorld())) || (!args[0].toLowerCase().equals("domicile") && !args[0].toLowerCase().equals("reload") && !args[0].toLowerCase().equals("help") && !Terrains.enabledInWorld(player.getWorld()))) {
            player.sendMessage(TrMsg.disabledWorld);
            return true;
        }
        
        Action action;

        try {
            action = Action.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException notEnum) {
            sendHelp(player);
            return true;
        }

        //Execute the correct command
        switch (action) {        
	    case HELP:
	        sendHelp(player);
	        return true;
	
	    case RELOAD:
	    case RL:
	        //Cancel if the Player does not have permission to use the command
	        if (!Terrains.hasPermission(player, "admin")) {
	            player.sendMessage(TrMsg.permission);
	        } else {
	            Terrains.rl(player);
	        }
	        return true;
	        
        case INFO:
            info(player);
            return true;
            
        case ACHETER:
        	acheter(player);
        	return true;

        case VENDRE:
        	vendre(player);
        	return true;

        case TOUTVENDRE:
            switch (args.length) {
            case 1:
            	if (Terrains.hasPermission(player, "clearConfirm")) {
            		PermissionAttachment attachment = tempAttachmentList.get(player.getUniqueId());
            		if (attachment != null) player.removeAttachment(attachment);
            		tempAttachmentList.remove(player.getUniqueId());
                	toutVendre(player);
                    return true;
            	} else if (!Terrains.isAnOwner(player.getName())) {
                    player.sendMessage(TrMsg.noOwnedTerrain);
                    return true;
            	} else {
                    player.sendMessage(TrMsg.clearConfirm);
                    PermissionAttachment attachment = player.addAttachment(Terrains.plugin,200);
            		attachment.setPermission("terrains."+"clearConfirm", true);
            		tempAttachmentList.put(player.getUniqueId(), attachment);
                    return true;
            	}
            case 2:
                if (!Terrains.hasPermission(player, "admin")) {
                    player.sendMessage(TrMsg.permission);
                    return true;
                } else {
                    toutVendre(player, args[1]);
                    return true;
                }
            default: return false;
            }

        case LISTE:
        	terrainsListe(player);
            return true;

        case PARTAGEPLUS:
        	if (args.length == 2) {
	            String coOwnerPlus = args[args.length - 1];
		        coowner(player, true, coOwnerPlus);
        	} else {
        		player.sendMessage(TrMsg.shareOnConfirm);
                PermissionAttachment attachment = player.addAttachment(Terrains.plugin,200);
        		attachment.setPermission("terrains."+"PlayerNamePlus", true);
        		tempAttachmentList.put(player.getUniqueId(), attachment);
        	}
	        return true;

        case PARTAGEMOINS:
        	if (args.length == 2) {
	            String coOwnerMoins = args[args.length - 1];
		        coowner(player, false, coOwnerMoins);
        	} else {
        		player.sendMessage(TrMsg.shareOffConfirm);
                PermissionAttachment attachment = player.addAttachment(Terrains.plugin,200);
        		attachment.setPermission("terrains."+"PlayerNameMoins", true);
        		tempAttachmentList.put(player.getUniqueId(), attachment);
        	}
	        return true;

        case PARTAGELISTE:
        	listCoOwners(player);
            return true;

        case STATUTNORMAL:
        	terrainStatus(player, 0);
            return true;
            
        case STATUTPUBLIC:
        	terrainStatus(player, 1);
            return true;

        case STATUTPRIVE:
        	terrainStatus(player, 2);
            return true;

        case ALARM:
        	alarmToggle(player);
        	return true;

        case DOMICILE:
        	if (args.length == 2) {
	            String coOwner = args[args.length - 1];
	            domicilePartageTp(player, coOwner);
        	} else {
	        	domicileTp(player);
        	}
            return true;

        case DOMICILESET:
        	domicileSet(player); 
            return true;

        default: break;
        }        
        return true;
    }

    /**
     * Gives ownership of the current Terrain to the Player
     *
     * @param player The Player buying the Terrain
     */
    public static void acheter(Player player) {
        
        //Cancel if the Player does not have permission to use the command
        if (!Terrains.hasPermission(player, "resident")) {
            player.sendMessage(TrMsg.permission);
            return;
        } 
        
        //Cancel if the Player under the protection limit
        if (player.getLocation().getBlock().getY() < Terrains.lowerLimit) {
            player.sendMessage(TrMsg.underLimit.replace("<limit>", ""+Terrains.lowerLimit));
            return;
        }

        //Retrieve the ownedTerrain that the Player is in
        Chunk chunk = player.getLocation().getBlock().getChunk();
        OwnedTerrain ownedTerrain = Terrains.findOwnedTerrain(chunk);

        //If the owner of the ownedTerrain is not blank then the land is already claimed
        if (ownedTerrain != null) {
        	String owner = ownedTerrain.owner.name;
        	if (player.getName().equalsIgnoreCase(owner)) {
                player.sendMessage(TrMsg.infoSelf);
        		return;
        	} else {
	            player.sendMessage(TrMsg.claimed.replace("<player>", owner));
	            // Previews the boundaries of the current Chunk
	            waitTime(10);
	            previewTerrain(player, chunk);
	            return;
        	}
        }

        if (wgSupport) {
            Plugin plugin = Terrains.pm.getPlugin("WorldGuard");
            if (player != null && plugin != null) {
                WorldGuardPlugin wg = (WorldGuardPlugin) plugin;
                for (Block block: Terrains.getBlocks(chunk)) {
                    if (!wg.canBuild(player, block)) {
                        player.sendMessage(TrMsg.worldGuard);
                        return;
                    }
                }
            }
        }

        String name = player.getName();

        //Check if the Player is limited
        int limit = Terrains.getOwnLimit(player);
        if (limit != -1) {
            //Cancel if the Player owns their maximum limit
            if (Terrains.getTerrainCounter(name) >= limit) {
                player.sendMessage(TrMsg.limit.replace("<number>", ""+limit));
                return;
            }
        }

        //Charge the Player only if they don't have the 'chunkown.free' node
        if (Terrains.hasPermission(player, "free")) {
            player.sendMessage(TrMsg.buyFree);
        } else if(!Econ.achat(player)) {
            return;
        }
        
        Terrains.addOwnedTerrain(new OwnedTerrain(chunk, name));

        // Previews the boundaries of the current Chunk
        waitTime(10);
        previewTerrain(player, chunk);
    }

    /**
     * Removes ownership of the current Chunk from the Player
     *
     * @param player The Player selling the Terrain
     */
    public static void vendre(Player player) {
    	
        //Cancel if the Player is not an owner    	
    	if (!Terrains.isAnOwner(player.getName())) {
            player.sendMessage(TrMsg.notAnOwner);
    		return;
    	}
    	
    	//Cancel if the Player under the protection limit
        if (player.getLocation().getBlock().getY() < Terrains.lowerLimit) {
            player.sendMessage(TrMsg.underLimit.replace("<limit>", ""+Terrains.lowerLimit));
            return;
        }
        
    	if (terrainIsFree.containsKey(player)) terrainIsFree.remove(player);
    	if (terrainIsMine.containsKey(player)) terrainIsMine.remove(player);
        if (terrainIsOwned.containsKey(player)) terrainIsOwned.remove(player);
        
        //Retrieve the ownedTerrain that the Player is in
        Chunk chunk = player.getLocation().getBlock().getChunk();
        OwnedTerrain ownedTerrain = Terrains.findOwnedTerrain(chunk);
        
        //Cancel if the Terrain is not owned
        if (ownedTerrain == null) {
            player.sendMessage(TrMsg.doNotOwn);
            return;
        }

        //Cancel if the ownedTerrain is owned by someone else
        if (!ownedTerrain.owner.name.equals(player.getName())) {
            if (Terrains.hasPermission(player, "admin")) {
                Econ.vente(player, ownedTerrain.owner.name);
            }
            else {
                player.sendMessage(TrMsg.doNotOwn);
                // Previews the boundaries of the current Chunk
                waitTime(10);
                previewTerrain(player, chunk);
                return;
            }
        }
        else {
            Econ.vente(null, player.getName());
        }

        Terrains.removeOwnedTerrain(ownedTerrain);
        
        // If Player have a Domicile in this Terrain, remove it
        if (!String.valueOf(ownedTerrain.owner.domicile).equals("null")) {
        	World w = ownedTerrain.owner.domicile.getWorld();
            if (!Terrains.enabledInWorld(w)) {
                return;
            }
        	Chunk domicileChunk = w.getChunkAt(ownedTerrain.owner.domicile);
		    if (chunk == domicileChunk) {
		    	domicileRemove(player);
	    	}
        }
        // Previews the boundaries of the current Chunk
        waitTime(10);
        previewTerrain(player, chunk);
        
    }

    /**
     * Removes all the Terrains that are owned by the given Player
     *
     * @param player The given Player
     */
    public static void toutVendre(Player player) {
        toutVendre(null, player.getName());
    }

    /**
     * Removes all the Terrains that are owned by the given Player
     *
     * @param player The given Player
     */
    private static void toutVendre(Player admin, String ownerName) {
        Iterator <OwnedTerrain> itr = Terrains.getOwnedTerrains().iterator();
        OwnedTerrain ownedTerrain;
    	TerrainOwner TerrainOwner = Terrains.findOwnerByPlayer(ownerName);
    	Player ownerPlayer = Bukkit.getServer().getPlayer(ownerName);
        
        while (itr.hasNext()) {
            ownedTerrain = itr.next();

            if (ownedTerrain.owner.name.equals(ownerName)) {
                if (admin == null) {
                    //Player sell all his Terrains
                    Econ.vente(null, ownerName);                    
                } else {
                    //Admin sell all the Terrains owned by the given Player
                    Econ.vente(admin, ownerName);
                }

                itr.remove();
                Terrains.removeOwnedTerrain(ownedTerrain);
            }
        }
    	if (admin != null) admin.sendMessage(TrMsg.noPlayerOwnedTerrain.replace("<player>", ownerName));
    	ownerPlayer.sendMessage(TrMsg.noMoreTerrain);
    	
    	// Delete Player's Domicile, if he has one
    	if (!String.valueOf(TerrainOwner.domicile).equals("null")) {    		
	    	domicileRemove(Bukkit.getPlayer(ownerName));
    	}
    }

    /**
     * Display to the Player all of the Terrains that they own
     *
     * @param player The Player requesting the list
     */
    public static void terrainsListe(Player player) {
        String name = player.getName();
        String msg;
        Integer count = Terrains.getTerrainCounter(name);
        switch (count) {
	        case 0 :        
	        	msg = TrMsg.noOwnedTerrain;
	        	break;
	        case 1:
		        msg = TrMsg.numberOfOwnedTerrain.replace("<number>", count.toString()).replace("<terrain(s)>", TrMsg.terrSing);
	        	break;
	        default:
		        msg = TrMsg.numberOfOwnedTerrain.replace("<number>", count.toString()).replace("<terrain(s)>", TrMsg.terrPlur);
	        	break;
        }
        player.sendMessage(msg);

        //Retrieve the ownLimit to display to the Player
        Integer ownLimit = Terrains.getOwnLimit(player);
        if (ownLimit > -1) {
            player.sendMessage(TrMsg.ownLimit.replace("<number>", ownLimit.toString()));
        }
        count = 0;
        for (OwnedTerrain ownedTerrain: Terrains.getOwnedTerrains()) {
            if (ownedTerrain.owner.name.equals(name)) {
            	++ count;
                player.sendMessage(ownedTerrain.toString().replace("<number>", count.toString()));
            }
        }
    }

    public void listCoOwners(Player player) {

        //Cancel if the Player is not an owner    	
    	if (!Terrains.isAnOwner(player.getName())) {
            player.sendMessage(TrMsg.notAnOwner);
    		return;
    	}
        
        String playerName = player.getName();
    	Integer size = Terrains.getOwner(playerName).coOwners.size();
    	if (size == 0) {
    		player.sendMessage(TrMsg.noCoownersList);
    		return;
    	}
        player.sendMessage(TrMsg.coownersList);
        
        for (String coOwner : Terrains.getOwner(playerName).coOwners) {
            player.sendMessage("§7- §e" + coOwner);
        }
    }

    /**
     * Display to the Player the info of the current Chunk
     * Info displayed is the Location of the Terrain and the current CoOwners
     *
     * @param player The Player requesting the info
     */
    public static void info(Player player) {
    	
    	//Cancel if the Player under the protection limit
        if (player.getLocation().getBlock().getY() < Terrains.lowerLimit) {
            player.sendMessage(TrMsg.underLimit.replace("<limit>", ""+Terrains.lowerLimit));
            return;
        }
        
        //Retrieve the ownedTerrain that the Player is in
        Chunk chunk = player.getLocation().getBlock().getChunk();
        OwnedTerrain ownedTerrain = Terrains.findOwnedTerrain(chunk);
        
        //Previews the boundaries of the current Chunk
    	if (terrainIsFree.containsKey(player)) terrainIsFree.remove(player);
    	if (terrainIsMine.containsKey(player)) terrainIsMine.remove(player);
        if (terrainIsOwned.containsKey(player)) terrainIsOwned.remove(player);
        // Previews the boundaries of the current Chunk
        waitTime(10);
        previewTerrain(player, chunk);
        

        //Cancel if the ownedTerrain does not exist
        if (ownedTerrain == null) {
            player.sendMessage(TrMsg.unclaimed);
            return;
        }

        //Display to the Player if he has some rights for the ownedTerrain
        if (player.getName().equals(ownedTerrain.owner.name)) { // Land is owned by the player
        	player.sendMessage(TrMsg.infoSelf);
        } else if (ownedTerrain.isCoOwner(player)) { // Land is coowned by the player without restrictions
        	player.sendMessage(TrMsg.infoOther.replace("<owner>", ownedTerrain.owner.name));
        	player.sendMessage(TrMsg.infoShared.replace("<owner>", ownedTerrain.owner.name));
        } else { // Player is not a coowner
        	player.sendMessage(TrMsg.infoOther.replace("<owner>", ownedTerrain.owner.name));
        	player.sendMessage(TrMsg.infoNonShared.replace("<owner>", ownedTerrain.owner.name));
        }
        //Display the ShareState of the ownedTerrain to the Player
        switch (ownedTerrain.shareState) {
	        case "public" :
	        	player.sendMessage(TrMsg.shareStatePublic);
	        case "private" :
	        	player.sendMessage(TrMsg.shareStatePrivate);
	        default : 
	        	player.sendMessage(TrMsg.shareStateNormal);
        }
    }

    /**
     * Manages Co-Ownership of the TerrainOwner of the Player
     *
     * @param player The given Player who may be the Owner
     * @param type true if co-owner is a Player, false if it is a group
     * @param add true if adding a co-owner, false if removing
     * @param coOwner The given Co-Owner
     */
    public static void coowner(Player player, boolean add, String coOwner) {
    	
        //Cancel if the Player is not an owner    	
    	if (!Terrains.isAnOwner(player.getName())) {
            player.sendMessage(TrMsg.notAnOwner);
    		return;
    	}
    	
        //Retrieve the TerrainOwner for the Player
    	TerrainOwner owner = Terrains.getOwner(player.getName());
        //Retrieve the coOwnerList for the Player
        LinkedList<String> coOwnerList = Terrains.getOwner(player.getName()).coOwners;

        //Determine the command to execute
        if (add) {
            //Cancel if the coOwner is the Owner
            if (owner.name.equalsIgnoreCase(coOwner) || owner.name.equalsIgnoreCase("none")) {
                return;
            }
            //Cancel if the Player is already a player.getUniqueId()
            if (coOwnerList.contains(coOwner)) {
                player.sendMessage(TrMsg.shareStatutAlready.replace("<player>", coOwner));
                return;
            }

            coOwnerList.add(coOwner);
            player.sendMessage(TrMsg.shareOn.replace("<player>", coOwner));
        } else {
             //Cancel if the Player is not a Co-owner
             if (!coOwnerList.contains(coOwner) || owner.name.equalsIgnoreCase("none")) {
                 player.sendMessage(TrMsg.shareNot.replace("<player>", coOwner));
                 return;
             }

            coOwnerList.remove(coOwner);
            player.sendMessage(TrMsg.shareOff.replace("<player>", coOwner));
        }

        owner.save();
    }

    /**
     * Set the share status the given Terrain
     *
     * @param player The Player who is the current TerrainOwner
     * @param status An Integer that represents the share status
     */
    public static void terrainStatus(Player player, Integer status) {
    	
        //Cancel if the Player is not the owner  of the current Terrain 	
    	if (Terrains.canInteractHere(player, player.getLocation().getBlock()) != 5) {
            player.sendMessage(TrMsg.doNotOwn);
    		return;
    	}
    	
        //Retrieve the ownedTerrain for the chunk the Player is in
    	OwnedTerrain ownedTerrain = Terrains.findOwnedTerrain(player.getLocation().getChunk());
        
        //Set the owner status property
    	String shareState;
    	switch (status) {
	    	case 1:
	    		shareState = "public";
    			player.sendMessage(TrMsg.shareStatePublic);
	    		break; 
	    	case 2:
	    		shareState = "private";
    			player.sendMessage(TrMsg.shareStatePrivate);
	    		break; 
    		default:
    			shareState = "normal";
    			player.sendMessage(TrMsg.shareStateNormal);
    			break;    	
    	}   
    	
        ownedTerrain.shareState = shareState;        
		ownedTerrain.save();
    }

    /**
     * Activates or disables the alarm system for the given Player
     *
     * @param player The Player who is an TerrainOwner
     */
    public static void alarmToggle(Player player) {
    	
        //Cancel if the Player is not an owner    	
    	if (!Terrains.isAnOwner(player.getName())) {
            player.sendMessage(TrMsg.notAnOwner);
    		return;
    	}
    	
        //Retrieve the TerrainOwner for the Player
    	TerrainOwner owner = Terrains.getOwner(player.getName());
        
        //Set the owner alarm property
		if (owner.alarm) {
            player.sendMessage(TrMsg.disabledAlarm);
			owner.setAlarm(false);
		} else {
            player.sendMessage(TrMsg.enabledAlarm);
			owner.setAlarm(true);
		}       
        
        owner.save();
    }

    /**
     * Teleport the Player to his domicile's location
     *
     * @param player The Player to teleport
     */
    public static void domicileTp(Player player) {
    	
        // Cancel if the Player is not an owner    	
    	if (!Terrains.isAnOwner(player.getName())) {
            player.sendMessage(TrMsg.notAnOwner);
    		return;
    	}
    	
		TerrainOwner owner = Terrains.getOwner(player.getName());
    	
        // Cancel if the Player doesn't have a Domicile    	
    	if (String.valueOf(owner.domicile).equals("null")) {
            player.sendMessage(TrMsg.noDomicileSelf);
    		return;
    	}
		
    	// Charge the Player
    	Econ.charge(player, Econ.domicileTpPrice);
    	
    	// Message the Player
    	owner.sendMessage(TrMsg.tpDomicileSelf);
    	
    	// Teleport the Player
    	player.teleport(owner.domicile);
    	player.spawnParticle(Particle.PORTAL, owner.domicile, 100, 0, 2.0, 0);
    }

    /**
     * Teleport the Player to a friend domicile's location
     *
     * @param player The Player to teleport
     * @param coOwner The Domicile's coOwner where teleport the Player
     */
    public static void domicilePartageTp(Player player, String coOwner) {

        // Redirect if the Player target himself
    	if (player.getName().equals(coOwner)) {
    		domicileTp(player);
    		return;
    	}

        // Cancel if the Player is not an owner    	
    	if (!Terrains.isAnOwner(coOwner)) {
            player.sendMessage(TrMsg.notAnOwner);
    		return;
    	}
    	
		TerrainOwner owner = Terrains.getOwner(coOwner);
    	
        // Cancel if the coOwner doesn't have a Domicile
    	if (String.valueOf(owner.domicile).equals("null")) {
            player.sendMessage(TrMsg.noDomicileOther.replace("<player>", coOwner));
    		return;
    	}
    	
		OwnedTerrain terrain = Terrains.findOwnedTerrainByBlock(owner.domicile.getBlock());
    	
        // Cancel if the coOwner doesn't share his Terrains with the Player
    	if (!terrain.isCoOwner(player)) {
            player.sendMessage(TrMsg.infoNonShared.replace("<owner>", owner.name));
    		return;
    	}
    	
        // Cancel if the coOwner's Domicile is in a private Terrain
    	if (terrain.shareState.equals("private")) {
            player.sendMessage(TrMsg.privateDomicile);
    		return;
    	}
    	
    	// Charge the Player
    	Econ.charge(player, Econ.domicileFriendPrice);

    	// Message the Player
    	player.sendMessage(TrMsg.tpDomicileOther.replace("<player>", coOwner));
    	
    	// Teleport the Player
    	player.teleport((owner.domicile));
    	player.spawnParticle(Particle.PORTAL, owner.domicile, 100, 0, 2.0, 0);
    }

        /**
         * Save the Player's current location as a teleport point
         *
         * @param player The Player creating a new Domicile
         */
        public static void domicileSet(Player player) {
        	
        // Cancel if the Player is not an owner    	
    	if (!Terrains.isAnOwner(player.getName())) {
            player.sendMessage(TrMsg.notAnOwner);
    		return;
    	}
        
    	// Cancel if the Player under the protection limit
        if (player.getLocation().getBlock().getY() < Terrains.lowerLimit) {
            player.sendMessage(TrMsg.underLimit.replace("<limit>", ""+Terrains.lowerLimit));
            return;
        }
        
    	if (terrainIsFree.containsKey(player)) terrainIsFree.remove(player);
    	if (terrainIsMine.containsKey(player)) terrainIsMine.remove(player);
        if (terrainIsOwned.containsKey(player)) terrainIsOwned.remove(player);
        
        // Retrieve the ownedTerrain that the Player is in
        Chunk chunk = player.getLocation().getBlock().getChunk();
        OwnedTerrain ownedTerrain = Terrains.findOwnedTerrain(chunk);
        
        // Cancel if the Terrain is not owned
        if (ownedTerrain == null) {
            player.sendMessage(TrMsg.doNotOwn);
            return;
        }

        // Cancel if the ownedTerrain is owned by someone else
        if (!ownedTerrain.owner.name.equals(player.getName())) {
            player.sendMessage(TrMsg.doNotOwn);
            // Previews the boundaries of the current Chunk
            waitTime(10);
            previewTerrain(player, chunk);
            return;
        }       
        else if (!Terrains.hasPermission(player, "free")) {
        	Econ.charge(player, Econ.domicileSetPrice);
        }
    	
    	DecimalFormat df = new DecimalFormat("0.#");
    	df.setRoundingMode(RoundingMode.HALF_UP);
        String w = player.getLocation().getWorld().getName();
        String x = df.format(player.getLocation().getX());
        String y = df.format(player.getLocation().getY());
        String z = df.format(player.getLocation().getZ());
 
        ownedTerrain.owner.setDomicile(player.getLocation());
        ownedTerrain.owner.save();
    	player.spawnParticle(Particle.PORTAL, ownedTerrain.owner.domicile, 100, 0, 2.0, 0);
    	
        ownedTerrain.owner.sendMessage(TrMsg.domicileSet.replace("<world>", w).replace("<x>", x).replace("<y>", y).replace("<z>", z));
        
        // Add a Dynmap marker on the map
        if (dmSupport) AddOrMoveDynmapDomicileMarker(player, player.getLocation());
        
        // Add permission
        PermissionAttachment attachment = player.addAttachment(Terrains.plugin);
		attachment.setPermission("terrains."+"tpdomicile", true);
		domicileAttachmentList.put(player.getUniqueId(), attachment);
		
        // Custom command on Domicile create
        if (!domicileSetCommand.isEmpty()) Terrains.server.dispatchCommand(Terrains.server.getConsoleSender(), domicileSetCommand.replace("/","").replace("<player>", player.getName()));
        
    }

    /**
     * Remove the Player's Domicile when the correspondiong Terrain is sold
     *
     * @param player The Player removing his Domicile
     */
    public static void domicileRemove(Player player) {
    	TerrainOwner owner = Terrains.getOwner(player.getName());
    	
    	owner.sendMessage(TrMsg.noMoreDomicile);
    	
    	owner.setDomicile(null);
    	owner.save();
    	
        // Refund the Player
    	if (!Terrains.hasPermission(player, "free")) {
    		Econ.refund(player, Econ.domicileSetPrice);
    	}
        // Remove the Dynmap marker on the map
        if (dmSupport) removeDynmapDomicileMarker(player);
        
		// Remove permission
		PermissionAttachment attachment = TrCommand.domicileAttachmentList.get(player.getUniqueId());
		TrCommand.domicileAttachmentList.remove(player.getUniqueId());
		player.removeAttachment(attachment);
        
        // Custom command on Domicile remove
        if (!domicileClearCommand.isEmpty()) Terrains.server.dispatchCommand(Terrains.server.getConsoleSender(), domicileClearCommand.replace("/","").replace("<player>", player.getName()));
    }
        
    /**
     * Create or move a Dynmap Marker for the given Player's Domicile
     *
     * @param Player The Player creating the Domicile
     * @param Player The Location of the new Domicile
     */
    public static void AddOrMoveDynmapDomicileMarker(Player player, Location location) {
        
        /* Create a new Track if not set yet */
        DynmapAPI dm = (DynmapAPI) Terrains.pm.getPlugin("dynmap");
    	MarkerSet markerSet = dm.getMarkerAPI().getMarkerSet(dmTrack);
        if (markerSet == null) {
        	dm.getMarkerAPI().createMarkerSet(TrCommand.dmTrack, TrCommand.dmLabel, null, true);
        	markerSet = dm.getMarkerAPI().getMarkerSet(TrCommand.dmTrack);
        	markerSet.setLayerPriority(TrCommand.dmLayer);
        }

        /* Create a new Marker or move and rename an existing one */
    	String markerId = player.getUniqueId().toString();
    	Marker marker = markerSet.findMarker(markerId);
    	String label = dmMarkerName.replace("<joueur>", player.getName());
    	if (marker != null) {
    		marker.setLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
    		marker.setLabel(label);
    	} else {
        	MarkerIcon markerIcon = dm.getMarkerAPI().getMarkerIcon(dmMarkerIcon);
    		markerSet.createMarker(markerId, label, location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), markerIcon, true);	
    	}
    }


    /**
     * Remove the Dynmap Marker for the given Player's Domicile
     *
     * @param Player The Player needing help
     */
    public static void removeDynmapDomicileMarker(Player player) {
        DynmapAPI dm = (DynmapAPI) Terrains.pm.getPlugin("dynmap");    	
        MarkerSet markerSet = dm.getMarkerAPI().getMarkerSet(dmTrack);
    	String markerId = player.getUniqueId().toString();
    	Marker marker = markerSet.findMarker(markerId);
    	if (marker != null) marker.deleteMarker();
    }

    /**
     * Displays the Terrains Help Page to the given Player
     *
     * @param Player The Player needing help
     */
    public static void sendHelp(Player player) {
        player.sendMessage("§e--------- §fHelp: §8§l[§2§lTerrains§8§l]§e ---------");
       
        if (Terrains.hasPermission(player, "resident")) {
            player.sendMessage("§6/"+command+" info§f Voir les limites du terrain actuel et ses caractéristiques (appartenance, statut, etc.)");
            player.sendMessage("§6/"+command+" acheter:§f Acheter le terrain actuel: "+Econ.format(Econ.getBuyPrice(player.getName())));
        }
        if (Terrains.isAnOwner(player.getName())) {            
            player.sendMessage("§6/"+command+": vendre:§f Vendre le §8[§2terrain§8]§f actuel: "+Econ.format(Econ.getSellPrice(player.getName())));
            player.sendMessage("§6/"+command+" toutVendre:§f Vendre l'ensemble de vos §8[§2terrains§8]§f.");
            player.sendMessage("§6/"+command+" liste:§f Lister l'ensemble de vos §8[§2terrains§8]§f.");
            player.sendMessage("§6/"+command+" partagePlus <joueur>:§f Ajouter un joueur à votre liste de partage.");
            player.sendMessage("§6/"+command+" partageMoins <joueur>:§f Supprimer un joueur de votre liste de partage.");
            player.sendMessage("§6/"+command+" partageListe:§f Lister les joueurs avec qui vous partagez vos §8[§2terrains§8]§f.");
            player.sendMessage("§6/"+command+" statutNormal:§f Seuls les joueurs figurant dans la liste de partage du propriétaire pourront accomplir toutes les actions sur ce §8[§2terrain§8]§f.");
            player.sendMessage("§6/"+command+" statutPublic:§f Tous les joueurs pourront effectuer des actions de base sur ce §8[§2terrain§8]§f, mais pas y construire.");
            player.sendMessage("§6/"+command+" statutPrive:§f En dehors du propriétaire, aucun joueur ne pourra effectuer la moindre action sur ce §8[§2terrain§8]§f.");
            player.sendMessage("§6/"+command+" alarm:§f Activer ou non le système d'alarme qui vous préviendra de toute intrusion sur vos §8[§2terrains§8]§f.");
        	player.sendMessage("§6/"+command+" domicile:§f Vous téléporter à votre §8[§2domicile§8]§f pour " + Econ.format(Econ.domicileTpPrice) + ".");
        	player.sendMessage("§6/"+command+" domicile <joueur>:§f Vous téléporter au §8[§2domicile§8]§f d'un joueur qui partage ses §8[§2terrains§8]§f avec vous, pour " + Econ.format(Econ.domicileFriendPrice) + ".");
        	player.sendMessage("§6/"+command+" domicileSet:§f Mémoriser votre position actuelle comme étant votre §8[§2domicile§8]§f. La caution coûte " + Econ.format(Econ.domicileSetPrice) + ".");
        }
        if (Terrains.hasPermission(player, "admin")) {
            player.sendMessage("§6/"+command+" toutVendre <joueur>:§f Revendre tous les 8[§2terrains§8]§f d'un joueur.");
            player.sendMessage("§6/"+command+" reload:§f Reloader le plugin, permet de restaurer les fichiers manquants.");
        }
    }

    /**
     * Sets the edges of the given Chunk to be animated to the given Player
     *
     * @param player The Player to send the smoke animations to
     * @param chunk The Chunk with the edges to be displayed
     */
    public static void previewTerrain(final Player player, Chunk chunk) {
        LinkedList<Location> outline = new LinkedList<Location>();
        Integer perm = Terrains.canInteractHere(player, player.getLocation().getBlock());
        if (perm == 2) terrainIsFree.put(player, outline);
        else if (perm >= 5 && perm <= 7) terrainIsMine.put(player, outline);
        else terrainIsOwned.put(player, outline);
        int y = player.getLocation().getBlockY();
        for (int x = 0; x <= 15; x = x + 15) {
            for (int z = 0; z <= 16; z++) {
                outline.add(chunk.getBlock(x, y, z).getLocation());
            }
        }
        for (int x = 0; x <= 16; x++) {
            for (int z = 0; z <= 15; z = z + 15) {
                outline.add(chunk.getBlock(x, y, z).getLocation());
            }
        }
        Terrains.scheduler.runTaskLater(Terrains.plugin, new Runnable() {
                @Override
                public void run() {
                	if (terrainIsFree.containsKey(player)) terrainIsFree.remove(player);
                	if (terrainIsMine.containsKey(player)) terrainIsMine.remove(player);
                    if (terrainIsOwned.containsKey(player)) terrainIsOwned.remove(player);
                }
            }, (effectDuration));
    }
    /**
     * Creates green animation for free Locations
     */
    static void animateIsFree() {
        //Repeat every 2 ticks
    	Terrains.scheduler.scheduleSyncRepeatingTask(Terrains.plugin, new Runnable() {
				@Override
                public void run() {
                    for (Player player : terrainIsFree.keySet()) {
                        for (Location location : terrainIsFree.get(player)) {
                        	location.setX(location.getX() + 0.5);
                        	location.setY(location.getY() + 0.5);
                        	location.setZ(location.getZ() + 0.5);
                            //Play some effect
                        	Particle.DustOptions dustOptions = new Particle.DustOptions(Color.GREEN, 1);
                        	player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions); // TODO vert
                        	location.setX(location.getX() - 0.5);
                        	location.setY(location.getY() - 0.5);
                        	location.setZ(location.getZ() - 0.5);
                        }
                    }
                }
            }, 0L, 1L);
    }

    /**
     * Creates red animation for owned by others Locations
     */
    static void animateIsOwned() {
        //Repeat every 2 ticks
    	Terrains.scheduler.scheduleSyncRepeatingTask(Terrains.plugin, new Runnable() {
				@Override
                public void run() {
                    for (Player player : terrainIsOwned.keySet()) {
                        for (Location location : terrainIsOwned.get(player)) {
                        	location.setX(location.getX() + 0.5);
                        	location.setY(location.getY() + 0.5);
                        	location.setZ(location.getZ() + 0.5);
                            //Play some effect
                        	Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);
                        	player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions); // TODO rouge
                        	location.setX(location.getX() - 0.5);
                        	location.setY(location.getY() - 0.5);
                        	location.setZ(location.getZ() - 0.5);
                        }
                    }
                }
            }, 0L, 1L);
    }

    /**
     * Creates white animation for self possessed Locations
     */
    static void animateIsMine() {
        //Repeat every 2 ticks
    	Terrains.scheduler.scheduleSyncRepeatingTask(Terrains.plugin, new Runnable() {
				@Override
                public void run() {
                    for (Player player : terrainIsMine.keySet()) {
                        for (Location location : terrainIsMine.get(player)) {
                        	location.setX(location.getX() + 0.5);
                        	location.setY(location.getY() + 0.5);
                        	location.setZ(location.getZ() + 0.5);
                            //Play some effect
                        	Particle.DustOptions dustOptions = new Particle.DustOptions(Color.WHITE, 1);
                        	player.spawnParticle(Particle.REDSTONE, location, 1, dustOptions); // TODO blanc
                        	location.setX(location.getX() - 0.5);
                        	location.setY(location.getY() - 0.5);
                        	location.setZ(location.getZ() - 0.5);
                        }
                    }
                }
            }, 0L, 1L);
    }

	/** 
	 * Custom wait
	 * 
	 */ 
	public static void waitTime(Integer ticksToWait) {
		try {
			Thread.sleep(ticksToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
