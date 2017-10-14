package net.vdcraft.arvdc.terrains;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

/**
 * Activates Alarm System & Notify When in OwnedChunks
 *
 * @author Codisimus
 */
public class TrMvtListener implements Listener {
    private static HashMap<Player, TerrainOwner> byOwner = new HashMap<Player, TerrainOwner>();
    private static HashMap<Player, OwnedTerrain> inChunk = new HashMap<Player, OwnedTerrain>();

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
    	
        Player player = event.getPlayer();
        OwnedTerrain currentOwnedTerrain = Terrains.findOwnedTerrainByBlock(event.getTo().getBlock());
        OwnedTerrain previousOwnedTerrain = inChunk.get(player);
        TerrainOwner previousOwner = byOwner.get(player);
        
        if (currentOwnedTerrain == null) {
            if (byOwner.containsKey(player)) {
                playerLeftTerrain(player, previousOwnedTerrain, previousOwner, currentOwnedTerrain);
            }
        } else {
            if (currentOwnedTerrain.owner != previousOwner) {
                byOwner.remove(player);
                playerLeftTerrain(player, previousOwnedTerrain, previousOwner, currentOwnedTerrain);
                playerEnteredTerrain(player, currentOwnedTerrain);
            }
        }
    }

    protected static void playerLeftTerrain(Player player, OwnedTerrain previousOwnedTerrain, TerrainOwner previousOwner, OwnedTerrain currentOwnedTerrain) {
    	
        String walkerName = player.getName();
        Plugin barPlugin = Terrains.pm.getPlugin("ActionBarAPI");
        
        if (previousOwner != null) {

    		// ALARM
        	if (previousOwner.alarm && !previousOwnedTerrain.isCoOwner(player) && !previousOwner.name.equals(walkerName) && !Terrains.hasPermission(player, "ninja")) {
        		previousOwner.sendMessage(TrMsg.alarmLeftOwned.replace("<player>", walkerName) + "\n" + previousOwnedTerrain.toString());
        	}

			// ALERT
        	if (currentOwnedTerrain == null) {
		        if (previousOwner.name.equals(walkerName)) {
		            if (barPlugin != null) ActionBarAPI.sendActionBar(player, TrMsg.alertLeftSelf);
		            else player.sendMessage(TrMsg.alertLeftSelf);
		        } else {
		            if (barPlugin != null) ActionBarAPI.sendActionBar(player, TrMsg.alertLeftOwned.replace("<owner>", previousOwner.name));
			        else player.sendMessage(TrMsg.alertLeftOwned.replace("<owner>", previousOwner.name));
		        }
        	}
        }
        
        byOwner.remove(player);
        inChunk.remove(player);
    }

    protected static void playerEnteredTerrain(Player player, OwnedTerrain currentOwnedTerrain) {    	
        
        String walkerName = player.getName();
        TerrainOwner currentOwner = currentOwnedTerrain.owner;
        Plugin barPlugin = Terrains.pm.getPlugin("ActionBarAPI");
        
		if (currentOwner != null) {
			
			// ALARM
			if (currentOwner.alarm && !currentOwnedTerrain.isCoOwner(player) && !currentOwner.name.equals(walkerName) && !Terrains.hasPermission(player, "ninja")) {
					currentOwner.sendMessage(TrMsg.alarmEnterOwned.replace("<player>", walkerName) + "\n" + currentOwnedTerrain.toString());
			}
			
			// ALERT
            if (currentOwner.name.equals(walkerName)) {
                if (barPlugin != null) ActionBarAPI.sendActionBar(player, TrMsg.alertEnterSelf);
    	        else player.sendMessage(TrMsg.alertEnterSelf);
            } else {
                if (barPlugin != null) ActionBarAPI.sendActionBar(player, TrMsg.alertEnterOwned.replace("<owner>", currentOwner.name));
            	else player.sendMessage(TrMsg.alertEnterOwned.replace("<owner>", currentOwner.name));
        	}
            
        }

        byOwner.put(player, currentOwner);
        inChunk.put(player, currentOwnedTerrain);
    }

}
