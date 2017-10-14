package net.vdcraft.arvdc.terrains;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

/**
 * Listens for ArmorStand related events
 *
 * @author ArVdC
 */
public class TrArmorStandListener implements Listener {
	
    /**
     * Within a non-public OwnedChunk only the Owner, a Co-Owner, or an Admin can pick up items on an Armor Stand
     *
     * @param event The PlayerArmorStandManipulateEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onArmorPickup(PlayerArmorStandManipulateEvent event) {
	    Player player = event.getPlayer();
    	Block blockP = player.getLocation().getBlock();
    	Block blockE = event.getRightClicked().getLocation().getBlock();
        // Only permit for 'public' or more authorized
    	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from picking up a(n) " + event.getArmorStandItem().getType() + " on an Armor Stand."); // Debug
            event.setCancelled(true);
    	}
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can destroy an Armor Stand
     *
     * @param event The VehicleCreateEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onArmorStandPlace(EntitySpawnEvent event) {
	    Entity entity = event.getEntity();
    	Block blockE = entity.getLocation().getBlock();
		Player player;
	    for (int i = 0; i < 6; i++) {
        	List<Entity> entitiesList = entity.getNearbyEntities(i, i, i);
			Boolean found = false;
	    	for (Entity nearestEntity : entitiesList) {
	    		if (nearestEntity instanceof Player) {
	    			player = (Player) nearestEntity;
	    	    	Block blockP = player.getLocation().getBlock();
	    			String item = player.getInventory().getItemInMainHand().getType().toString();
	            	if (item.contains("ARMOR_STAND")) {
	        			found = true;
	        	        // Only permit for 'coowner' or more authorized
	        	    	if (Terrains.canInterractHere(player, blockP, blockE) > 6) {
	        	    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from destroying an Armor Stand."); // Debug
	        	            event.setCancelled(true);
	        	            player.updateInventory();
	            			// TODO The armor stand is actually not restored in player's inventory, but why ? (and the same code works with a minecart) TODO
		                    
		            	}
	    			}
	            if (found) break;
	    		}
	    	}
    	if (found) break;
    	}
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can damage an Armor Stand
     *
     * @param event The onEntityDamage that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onArmorStandDamage(EntityDamageByEntityEvent event) {
        Entity wounded = event.getEntity();
        Entity attacker = event.getDamager();
        if (attacker instanceof Player && wounded instanceof ArmorStand) {
    	    Player player = (Player) attacker;
        	Block blockP = player.getLocation().getBlock();
        	Block blockE = wounded.getLocation().getBlock();
            // Only permit for 'coowner' or more authorized
        	if (Terrains.canInterractHere(player, blockP, blockE) > 6) {
        		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from damaging an Armor Stand."); // Debug
                event.setCancelled(true);
        	}
        }
    }
}
