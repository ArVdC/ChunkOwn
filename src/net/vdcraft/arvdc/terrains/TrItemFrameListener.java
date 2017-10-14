package net.vdcraft.arvdc.terrains;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Listens for ItemFrame related events
 *
 * @author ArVdC
 */
public class TrItemFrameListener implements Listener {
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can rotate items in Item Frames
     *
     * @param event The PlayerInteractEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onItemFrameRotate(PlayerInteractEntityEvent event) {
    	Entity entity = event.getRightClicked();
       	if (entity instanceof ItemFrame) {
	        Player player = event.getPlayer();
	    	Block blockP = player.getLocation().getBlock();
	    	Block blockE = entity.getLocation().getBlock();
	        // Only permit for 'coowner' or more authorized
	    	if (Terrains.canInterractHere(player, blockP, blockE) > 6) {
	    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from rotating an item frame."); // Debug
	    		event.setCancelled(true);
	    	}
        }

    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can damage an Item Frame
     *
     * @param event The EntityDamageByEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onItemFrameDamage(EntityDamageByEntityEvent event) {
        Entity wounded = event.getEntity();
        Entity attacker = event.getDamager();
        if (attacker instanceof Player && wounded instanceof ItemFrame) {
    	    Player player = (Player) attacker;
        	Block blockP = player.getLocation().getBlock();
        	Block blockE = wounded.getLocation().getBlock();
    	    // Only permit for 'coowner' or more authorized
    	    if (Terrains.canInterractHere(player, blockP, blockE) > 6) {
    	    	if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from damaging an item frame."); // Debug
        		event.setCancelled(true);
        	}
        }
    }
}
