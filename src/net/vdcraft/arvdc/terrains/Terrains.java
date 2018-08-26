package net.vdcraft.arvdc.terrains;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.vdcraft.arvdc.terrains.TrLang;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Loads Plugin and manages Data/Permissions
 *
 * @author ArVdC
 */
public class Terrains extends JavaPlugin {
	public static Boolean debug = false; /* Activate the debug mode */
	static Server server;
	static Logger logger;
	static Permission permission;
	static PluginManager pm;
	static int lowerLimit;
	static HashMap<String, Properties> savedData = new HashMap<String, Properties>();
	static int groupSize;
	static Properties lastDaySeen;
	static Plugin plugin;
	public static Terrains instanceMainClass;
	public static FileConfiguration langConf;
	static BukkitScheduler scheduler;
	static int disownTime;
	static LinkedList<World> worlds = new LinkedList<World>();
	static LinkedList<World> tpWorlds = new LinkedList<World>();
	static String dataFolder;
	public static HashMap<String, OwnedTerrain> ownedTerrains = new HashMap<String, OwnedTerrain>();
	public static HashMap<String, TerrainOwner> terrainOwners = new HashMap<String, TerrainOwner>();

	/**
	 * Console admin messages
	 *
	 */
	public static String missValueMsg = "Missing value for";
	public static String noTicketMsg = "DO NOT POST A TICKET FOR THIS MESSAGE, IT WILL JUST BE IGNORED";
	
	/**
	 * Calls methods to load this Plugin when it is enabled
	 *
	 */
	@Override
	public void onEnable () {
	    server = getServer();
	    logger = getLogger();
	    pm = server.getPluginManager();
	    plugin = this;
	    scheduler = server.getScheduler();
	    
	    /* Version testing */
	    if (McVersionHandler.KeepDecimalOfMcVersion() >= 13.0) {
	    	logger.info("Veuillez télécharger la version 1.2.0 du plugin, conçue pour MC 1.13+");
	    	pm.disablePlugin(this);
	    }
	    
	    /* Rebooted version Msg */
	    logger.info("[Terrains] is the Codisimus [ChunkOwn] plugin forked by ArVdC for MC 1.12.2 in a french version.");
	     
	
	    /* Disable this plugin if Vault is not present */
	    if (!pm.isPluginEnabled("Vault")) {
	        logger.severe("Please install Vault in order to use this plugin!");
	        pm.disablePlugin(this);
	        return;
	    }
	
	    /* Create data folders */
	    File dir = this.getDataFolder();
	    if (!dir.isDirectory()) {
	        dir.mkdir();
	    }
	
	    dataFolder = dir.getPath();
	
	    dir = new File(dataFolder+"/Terrains");
	    if (!dir.isDirectory()) {
	        dir.mkdir();
	    }
	
	    dir = new File(dataFolder+"/Owners");
	    if (!dir.isDirectory()) {
	        dir.mkdir();
	    }
	
	    /* Link Permissions/Economy */
	    RegisteredServiceProvider<Permission> permissionProvider =
	            getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	    if (permissionProvider != null) {
	        permission = permissionProvider.getProvider();
	    }
	
	    RegisteredServiceProvider<Economy> economyProvider =
	            getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	    if (economyProvider != null) {
	        Econ.economy = economyProvider.getProvider();
	    }
	
	    loadAll();
	
	    /* Register Events */
	    pm.registerEvents(new TrGlobalListener(), this);
	    pm.registerEvents(new TrItemFrameListener(), this);
	    pm.registerEvents(new TrArmorStandListener(), this);
	    pm.registerEvents(new TrVehicleListener(), this);
	    pm.registerEvents(new TrAnimalsListener(), this);
	    pm.registerEvents(new TrDamageListener(), this);
	    pm.registerEvents(new TrExplosionListener(), this);
    	pm.registerEvents(new TrInteractionListener(), this);
	    pm.registerEvents(new TrMvtListener(), this);
	    pm.registerEvents(new TrChatListener(), this);
	    pm.registerEvents(new TrOnJoin(), this);
	    /* Register the command found in the plugin.yml */
	    String commands = this.getDescription().getCommands().toString();
	    TrCommand.command = commands.substring(1, commands.indexOf("="));
	    getCommand(TrCommand.command).setExecutor(new TrCommand());
	    /* Register the tab completion list */
		getCommand(TrCommand.command).setTabCompleter(new TrTabCompleter());
	
	    /* Schedule repeating tasks */
	    TrCommand.animateIsFree();
	    TrCommand.animateIsOwned();
	    TrCommand.animateIsMine();
	
	}

    /**
     * Returns true if the given Player has the specific permission
     *
     * @param player The Player who is being checked for permission
     * @param type The String of the permission, ex. admin
     * @return True if the given Player has the specific permission
     */
    static boolean hasPermission(String player, String type) {
    	Player p = Bukkit.getServer().getPlayer(player);
    	String w = server.getWorlds().get(0).toString();
        return permission.playerHas(w, p, "terrains."+type);
    }

    /**
     * Returns true if the given Player has the specific permission
     *
     * @param player The Player who is being checked for permission
     * @param type The String of the permission, ex. admin
     * @return True if the given Player has the specific permission
     */
    public static boolean hasPermission(Player player, String type) {
        return permission.has(player, "terrains."+type);
    }

    /**
     * Returns the Integer value that is the limit of Chunks the given Player can own
     * Returns -1 if there is no limit
     *
     * @param player The Player who is being checked for a limit
     * @return The Integer value that is the limit of Chunks the given Player can own
     */
    public static int getOwnLimit(Player player) {
        //Check for the unlimited node first
        if (hasPermission(player, "limit.-1")) {
            return -1;
        }

        //Start at 100 and work down until a limit node is found
        for (int i = 100; i >= 0; i--) {
            if (hasPermission(player, "limit."+i)) {
                return i;
            }
        }

        //No limit if a limit node is not found
        return -1;
    }

    /**
     * Reads save file to load Terrains data for all Worlds
     *
     */
    private static void loadAll() {
        TrConfig.load();
        Terrains.logger.info("The configuration file is loaded.");
        
        TrLang.load();        
    	Terrains.logger.info("The language file is loaded."); // Console loaded file msg
        
        loadTerrainOwners();
        Terrains.logger.info("The owners files are loaded.");

        for (World world: worlds.isEmpty() ? server.getWorlds() : worlds) {
            loadData(world.getName());
        }   
        Terrains.logger.info("The terrains files are loaded.");
    }

    /**
     * Loads TerrainOwners from file .properties
     *
     */
    public static void loadTerrainOwners() {
        for (File file: new File(dataFolder+"/Owners/").listFiles()) {
            String name = file.getName();
            if (name.endsWith(".properties")) {
                FileInputStream fis = null;
                try {
                    //Load the Properties file for reading
                    Properties p = new Properties();
                    fis = new FileInputStream(file);
                    p.load(fis);

                    //Construct a new TerrainOwners using the file name
                    TerrainOwner owner = new TerrainOwner(name.substring(0, name.length() - 11));
                    

                    //Convert the coOwners data into a LinkedList for the TerrainOwner
                    String Coownersdata = p.getProperty("CoOwners");
                    if (!Coownersdata.equals("none")) {
                        owner.coOwners = new LinkedList<String>(Arrays.asList(Coownersdata.split("'")));
                    }
                    
                    //Convert the AlarmState data into a boolean for the TerrainOwner
                    owner.alarm = Boolean.parseBoolean(p.getProperty("AlarmSystem"));
                    
                    //Convert the Domicile data into a Location for the TerrainOwner
                    //owner.domicile = p.getProperty("Domicile");
                    // (World world, double x, double y, double z, float yaw, float pitch)
                    if (!p.getProperty("Domicile").equals("none")) {
	                    String[] loc = p.getProperty("Domicile").split(",");
	                    String[] wNode = loc[0].split("\\=");
	                    World w = Bukkit.getWorld(wNode[2].replace("}", ""));
	                    String[] xNode = loc[1].split("\\=");
	                    Double x = Double.parseDouble(xNode[1]);
	                    String[] yNode = loc[2].split("\\=");
	                    Double y = Double.parseDouble(yNode[1]);
	                    String[] zNode = loc[3].split("\\=");
	                    Double z = Double.parseDouble(zNode[1]);
	                    String[] pitchNode = loc[4].split("\\=");
	                    float pitch = Float.parseFloat(pitchNode[1]);
	                    String[] yawNode = loc[5].split("\\=");
	                    float yaw = Float.parseFloat(yawNode[1].replace("}", ""));
	                    Location location = new Location(w, x, y, z, yaw, pitch);
	                    owner.domicile = location;
                    } else {
                    	owner.domicile = null;
                    }

                    terrainOwners.put(owner.name, owner);                    
                    
                } catch (Exception loadFailed) {
                    logger.severe("Failed to load "+name);
                    loadFailed.printStackTrace();
                } finally {
                    try {
                        fis.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * Reads save file to load Terrains data for given World
     *
     */
    private static void loadData(String world) {
        //Cancel if the World is disabled
        World w = server.getWorld(world);
        if (!enabledInWorld(w)) {
            return;
        }

        FileInputStream fis = null;
        try {
            File file = new File(dataFolder + "/Terrains/" + world + ".properties");
            if (!file.exists()) {
                return;
            }

            fis = new FileInputStream(file);
            Properties data = new Properties();
            data.load(fis);
            savedData.put(world, data);

            for (String key: data.stringPropertyNames()) {
                String[] keyData = key.split("'");
                String[] valueData = data.getProperty(key).split(",");

                //Construct a new OwnedTerrain using the World name and x/z coordinates
                OwnedTerrain ownedTerrain = new OwnedTerrain(world, Integer.parseInt(keyData[0]), Integer.parseInt(keyData[1]), valueData[0]);

                //Get the shareState data into a String for the OwnedChunk
                ownedTerrain.shareState = (String) valueData[1].toString();
                
                ownedTerrains.put(world + "'" + ownedTerrain.x + "'" + ownedTerrain.z, ownedTerrain);
                ownedTerrain.save();
            }
            
        } catch (Exception loadFailed) {
            logger.severe("Load Failed!");
            loadFailed.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Reloads Terrains data
     *
     */
    public static void rl() {
    	rl(null);
    }

    /**
     * Reloads Terrains data
     *
     * @param player The Player reloading the data
     */
    public static void rl(Player player) {
        savedData.clear();
        ownedTerrains.clear();
        terrainOwners.clear();
        loadAll();

        logger.info("Plugin reloaded.");
        if (player != null) {
            player.sendMessage(TrMsg.format("<prefix> reloaded."));
        }
    }

    /**
     * Writes data for specified World to save file
     * Old file is overwritten
     */
    public static void save(String world) {
        FileOutputStream fos = null;
        try {
            File file = new File(dataFolder+"/Terrains/"+world+".properties");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new Exception("File creation failed!");
                }
            }

            fos = new FileOutputStream(file);
            savedData.get(world).store(fos, null);
        } catch (Exception saveFailed) {
            logger.severe("Save Failed!");
            saveFailed.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Writes the given TerrainOwner to its save file
     * If the file already exists, it is overwritten
     *
     * @param owner The given TerrainOwner
     */
    static void saveTerrainOwner(TerrainOwner owner) {
        FileOutputStream fos = null;
        try {
            Properties p = new Properties();

            p.setProperty("AlarmSystem", String.valueOf(owner.alarm));

        	if (String.valueOf(owner.domicile).equals("null")) {
        		p.setProperty("Domicile", "none");
        	} else {
        		p.setProperty("Domicile", String.valueOf(owner.domicile));
        	}

            String coOwnersString = "";
            for (String string : owner.coOwners) {
                coOwnersString += "'" + string;
            }
            coOwnersString = coOwnersString.isEmpty() ? "none" : coOwnersString.substring(1);
            p.setProperty("CoOwners", coOwnersString);

            //Write the TerrainOwner Properties to file
            fos = new FileOutputStream(dataFolder+"/Owners/"+owner.name+".properties");
            p.store(fos, null);
        } catch (Exception saveFailed) {
            logger.severe("Save Failed!");
            saveFailed.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Returns the amount of Terrains that the given Player owns
     *
     * @return The amount of Terrains that the given Player owns
     */
    public static int getTerrainCounter(String player) {
        TerrainOwner owner = findOwnerByPlayer(player);
        return owner == null ? 0 : owner.terrainsCounter;
    }

    /**
     * Check if the Player is a ChunkOwner
     *
     * @param player The name of the owner of the Chunk
     */
    public static Boolean isAnOwner(String player) {
    	TerrainOwner owner = Terrains.getOwner(player);
        if (owner == null) {
        	return false;
        } else if (owner.terrainsCounter == 0) {
        	return false;
        } else {
        	return true;
        }
    }

    /**
     * Returns the TerrainOwner object for the Chunk which contains the given Block
     * returns null if the Chunk is not claimed or if the Block is below the Own Lower Limit
     *
     * @param block The block that may be in an OwnedTerrain
     */
    public static TerrainOwner findOwnerByBlock(Block block) {
        if (block.getY() < lowerLimit) {
            return null;
        }

        return findOwnerByChunk(block.getChunk());
    }

    /**
     * Returns the TerrainOwner object for the given Chunk
     * returns null if the Chunk is not claimed
     *
     * @param chunk The Chunk that the OwnedChunk would represent
     */
    public static TerrainOwner findOwnerByChunk(Chunk chunk) {
        OwnedTerrain ownedTerain = ownedTerrains.get(chunkToString(chunk));
        
        if (ownedTerain == null) {
            return null;
        }

        return ownedTerain.owner;
    }

    /**
     * Returns the TerrainOwner object for the given Player Name
     * returns null if the TerrainOwner does not exist
     *
     * @param player The TerrainOwner for the given Player Name
     */
    public static TerrainOwner findOwnerByPlayer(String player) {
        return terrainOwners.get(player);
    }

    /**
     * Returns the TerrainOwner object for the given Player Name
     * A new TerrainOwner is created if one does not exist
     *
     * @param player The TerrainOwner for the given Player Name
     */
    public static TerrainOwner getOwner(String player) {
        TerrainOwner owner = findOwnerByPlayer(player);
        if (owner == null) {
            owner = new TerrainOwner(player);
            terrainOwners.put(player, owner);
        }

        return owner;
    }

    /**
     * Returns the OwnedTerrain object for the given Chunk
     * returns null if the Chunk is not claimed
     *
     * @param world The name of the World the OwnedChunk is in
     * @param x The x-coordinate of the OwnedChunk
     * @param z The z-coordinate of the OwnedChunk
     * @return The OwnedTerrain object for the given Chunk
     */
    public static OwnedTerrain findOwnedTerrain(String world, int x, int z) {
        return findOwnedTerrain(server.getWorld(world).getChunkAt(x, z));
    }

    /**
     * Returns the OwnedTerrain object for the Chunk which contains the given Block
     * returns null if the Chunk is not claimed or if the Block is below the Own Lower Limit
     *
     * @param block The block that may be in an OwnedTerrain
     */
    public static OwnedTerrain findOwnedTerrainByBlock(Block block) {
    	Integer y = block.getY();
        return (y < lowerLimit) ? null : findOwnedTerrain(block.getChunk());    	
    }

    /**
     * Returns the OwnedTerrain object for the given Chunk
     * returns null if the Chunk is not claimed
     *
     * @param chunk The Chunk that the OwnedTerrain would represent
     */
    public static OwnedTerrain findOwnedTerrain(Chunk chunk) {
        return ownedTerrains.get(chunkToString(chunk));
    }

    /**
     * Adds the OwnedChunk from the saved data
     *
     * @param ownedChunk The OwnedChunk to add
     */
    public static void addOwnedTerrain(OwnedTerrain ownedChunk) {
        //Cancel if the Chunk is in a disabled World
        World world = server.getWorld(ownedChunk.world);
        if (!enabledInWorld(world)) {
            return;
        }

        ownedTerrains.put(ownedChunk.world+"'"+ownedChunk.x+"'"+ownedChunk.z, ownedChunk);
        ownedChunk.save();
        ownedChunk.owner.save();
    }

    /**
     * Removes the OwnedChunk from the saved data
     *
     * @param ownedChunk The OwnedChunk to be removed
     */
    public static void removeOwnedTerrain(OwnedTerrain ownedChunk) {
        Chunk chunk = server.getWorld(ownedChunk.world).getChunkAt(ownedChunk.x, ownedChunk.z);
        removeOwnedTerrain(chunk, ownedChunk);
    }

    /**
     * Removes the OwnedChunk from the saved data
     *
     * @param world The name of the World the OwnedChunk is in
     * @param x The x-coordinate of the OwnedChunk
     * @param z The z-coordinate of the OwnedChunk
     */
    public static void removeOwnedChunk(String world, int x, int z) {
        Chunk chunk = server.getWorld(world).getChunkAt(x, z);
        OwnedTerrain ownedChunk = ownedTerrains.get(chunkToString(chunk));
        removeOwnedTerrain(chunk, ownedChunk);
    }

    /**
     * Removes the OwnedChunk from the saved data
     *
     * @param chunk The Chunk that the OwnedChunk would represent
     */
    public static void removeOwnedChunk(Chunk chunk) {
        OwnedTerrain ownedChunk = ownedTerrains.get(chunkToString(chunk));
        removeOwnedTerrain(chunk, ownedChunk);
    }

    /**
     * Removes the OwnedChunk from the saved data
     *
     * @param chunk The Chunk that the OwnedChunk would represent
     */
    private static void removeOwnedTerrain(Chunk chunk, OwnedTerrain ownedChunk) {
        ownedChunk.owner.terrainsCounter--;
        ownedTerrains.remove(chunkToString(chunk));
        savedData.get(ownedChunk.world).remove(ownedChunk.x + "'" + ownedChunk.z);
        save(ownedChunk.world);
    }

    /**
     * Retrieves a list of Chunks that the given Player owns
     *
     * @param player The name of the given Player
     * @return The list of Chunks
     */
    public static LinkedList<Chunk> getOwnedTerrains(String player) {
        LinkedList<Chunk> chunks = new LinkedList<Chunk>();

        int owned = getTerrainCounter(player);
        if (owned == 0) {
            return chunks;
        }

        for (OwnedTerrain chunk: ownedTerrains.values()) {
            if (chunk.owner.name.equals(player)) {
                chunks.add(server.getWorld(chunk.world).getChunkAt(chunk.x, chunk.z));
                owned--;

                if (owned == 0) {
                    return chunks;
                }
            }
        }

        return chunks;
    }

    /**
     * Returns a Collection of all OwnedChunks
     *
     * @return A Collection of all OwnedChunks
     */
    public static Collection<OwnedTerrain> getOwnedTerrains() {
        return ownedTerrains.values();
    }

    /**
     * Returns true if this plugin is enabled in the given World
     *
     * @param world The given World
     * @return True if this plugin is enabled in the given World
     */
    public static boolean enabledInWorld(World world) {
        return worlds.isEmpty() || worlds.contains(world);
    }

    /**
     * Returns true if the teleport option is enabled in the given World
     *
     * @param world The given World
     * @return True if the teleport option is enabled in the given World
     */
    public static boolean tpEnableInWorld(World world) {
        return tpWorlds.isEmpty() || tpWorlds.contains(world);
    }

    static String chunkToString(Chunk chunk) {
        return chunk.getWorld().getName()+"'"+chunk.getX()+"'"+chunk.getZ();
    }

    public static LinkedList<Block> getBlocks(Chunk chunk) {
        LinkedList<Block> blockList = new LinkedList<Block>();
        World w = chunk.getWorld();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = lowerLimit; y < w.getMaxHeight(); y++) {
                    blockList.add(w.getBlockAt(x, y, z));
                }
            }
        }
        return blockList;
    }

	/**
	 * Return the permission level depending on the location of the player OR the event
	 *
	 * @param player The name of the player to test
	 * @param block The Block at the event or the player location
	 * 
	 */
	public static Integer canInteractHere(Player player, Block block) {
		Block blockP;
		if (player != null)	{
			blockP = player.getLocation().getBlock();
			if (blockP == block) {
				return canInterractHere(player, block, block);
			} else {
				return canInterractHere(player, block, blockP);
			}
		} else {
			return canInterractHere(null, block, block);
		}
	 }
	
	/**
	 * Return the permission level depending on the location of the player AND the event
	 *
	 * @param player The name of the player to test
	 * @param blockP The block where the player stands
	 * @param blockE The block where the event happens
	 * 
	 * @return #0. Event doesn't happen in a protected world
	 * @return #1. Block is in a WorldGuard protected region
	 * @return #2. One land or both are unclaimed
	 * @return #3. Block is under the protection limit
	 * @return #4. Player is an admin
	 * @return #5. Player is the owner of the event land
	 * @return #6. Player is coowner of the event land & this land isn't private
	 * @return #7. The event land is public
	 * @return #8. The event land is shared on list
	 * @return #9. The event land is private
	 * @return #10. Player can't be determined & the land is owned
	 * @return #11. If fail to match any case, result var keeps its default value
	 * 
	 */
	public static Integer canInterractHere(Player player, Block blockE, Block blockP) {
		
        Chunk chunkE = blockE.getChunk();
        Chunk chunkP = blockP.getChunk();
        Boolean verifyTwice = true;
        if (chunkP == chunkE) verifyTwice = false;
        OwnedTerrain ownedTerrainE = findOwnedTerrain(chunkE);
    	OwnedTerrain ownedTerrainP = null;
    	if (verifyTwice) ownedTerrainP = findOwnedTerrain(chunkP);
    	
	    // #11. If fails to match any case, result var keeps its default value
		Integer result = 11;        
        
        // #10. Player can't be determined & the land is owned
       if (player == null && ownedTerrainE != null) {
           if (debug) logger.info("Access test returns #10");
        	return 10;
        }       
        // #0. Event doesn't happen in a protected world
        if (!enabledInWorld(chunkE.getWorld())) {
            if (debug) logger.info("Access test returns #0");
            return 0;
        }        
        // #1. Block is in a WorldGuard protected region
      	if (player != null && TrCommand.wgSupport) {
            Plugin plugin = Terrains.pm.getPlugin("WorldGuard");
            if (plugin != null) {
                WorldGuardPlugin wg = (WorldGuardPlugin) plugin;
                if (!wg.canBuild(player, blockE)) {
                    if (debug) logger.info("Access test returns #1");
                    return 1;
                }
            }
        }        
        // #2. One land or both are unclaimed
        if (ownedTerrainE == null && ownedTerrainP == null) {
            if (debug) logger.info("Access test returns #2");
        	return 2;
        } else if (ownedTerrainP == null) { // Only one of the land where the Player stands is unclaimed
    		verifyTwice = false;
        	result = 2;
        } else if (ownedTerrainE == null) { // Only one of the land where the Event happens is unclaimed
    		ownedTerrainE = ownedTerrainP; // Test the non-null land instead the other
    		verifyTwice = false;
        	result = 2;
        }        
        // #3. Block is under the protection limit
        if (blockE.getY() < Terrains.lowerLimit) {
            if (debug) logger.info("Access test returns #3");
            return 3;
        }        
        // #4. Player is an admin
        if (hasPermission(player, "admin")) {
            if (debug) logger.info("Access test returns #4");
            return 4;
        }        
        // #5. Player is the owner of the event land
        else if (ownedTerrainE.owner.name.equals(player.getName())) result = 5;

        // #6. Player is coowner of the event land & this land isn't private
        else if (ownedTerrainE.isCoOwner(player) && !ownedTerrainE.shareState.equals("private")) result = 6;
    
        // #7. The event land is public
        else if (ownedTerrainE.shareState.equals("public")) result = 7;

        // #8. The event land is shared on list
        else if (ownedTerrainE.shareState.equals("normal")) result = 8;

        // #9. The event land is private
        else if (ownedTerrainE.shareState.equals("private")) result = 9;

	    // Check the permission level twice if the Player's land is not the same that the Event's one
    	// Only for restrictions, this will never authorize more than the first check
        if (verifyTwice) {
    		if (ownedTerrainP.owner.name.equals(player.getName())) result = 5;
        	else if (ownedTerrainP.isCoOwner(player) && !ownedTerrainP.shareState.equals("private")) result = 6;
        	else if (ownedTerrainP.shareState.equals("public")) result = 7;
        	else if (ownedTerrainP.shareState.equals("normal")) result = 8;
        	else if (ownedTerrainP.shareState.equals("private")) result = 9;
    	}
    	// Return the final result
        if (debug) logger.info("Access test returns #" + result);
    	return result;
	}
}