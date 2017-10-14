package net.vdcraft.arvdc.terrains;

import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.*;

/**
 * Listens for animals related events
 *
 * @author ArVdC
 */
public class TrAnimalsListener implements Listener {
    
    /**
     * Within a non-public OwnedChunk only the Owner, a Co-Owner, or an Admin can leash an entity
     *
     * @param event The PlayerLeashEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLeash(PlayerLeashEntityEvent event) {
	    Player player = event.getPlayer();
    	Block blockP = player.getLocation().getBlock();
    	Block blockE = event.getEntity().getLocation().getBlock();
        // Only permit for 'public' or more authorized
    	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from leashing a " + event.getEntity().getName() + "."); // Debug
            event.setCancelled(true);
    	}
    }
	
    /**
     * Within a non-public OwnedChunk only the Owner, a Co-Owner, or an Admin can unleash an entity
     *
     * @param event The PlayerUnleashEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onUnleash(PlayerUnleashEntityEvent event) {
	    Player player = event.getPlayer();
    	Block blockP = player.getLocation().getBlock();
    	Block blockE = event.getEntity().getLocation().getBlock();
        // Only permit for 'public' or more authorized
    	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from unleashing a " + event.getEntity().getName() + "."); // Debug
            event.setCancelled(true);
    	}
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can shear a sheep
     *
     * @param event The PlayerShearEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onShear(PlayerShearEntityEvent event) {
	    Player player = event.getPlayer();
    	Block blockP = player.getLocation().getBlock();
    	Block blockE = event.getEntity().getLocation().getBlock();
        // Only permit for 'coowner' or more authorized
    	if (Terrains.canInterractHere(player, blockP, blockE) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from shearing a sheep."); // Debug
            event.setCancelled(true);
    	}
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can interact with some animals
     *
     * @param event The PlayerInteractEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void PlayerInteractWithAnimal(PlayerInteractEntityEvent event) {
	    Player player = event.getPlayer();
    	Block blockP = player.getLocation().getBlock();
    	if (event.getRightClicked() instanceof Animals) {
    		Entity entity = event.getRightClicked();
        	Block blockE = entity.getLocation().getBlock();
        	switch (entity.getType()) {
            case HORSE:
            case PIG:
            case LLAMA:
	            // Only permit for 'public' or more authorized
            	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
            		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from interact with an animal " + entity.getType() + "."); // Debug
    	            event.setCancelled(true);
            	}
	        	break;
			default:
	            return;
	    	}
    	}
    }
	
    /**
     * Within a non-public OwnedChunk only the Owner, a Co-Owner, or an Admin can fish
     *
     * @param event The PlayerFishEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onFish(PlayerFishEvent event) {
	    Player player = event.getPlayer();
    	Block blockP = player.getLocation().getBlock();
		Block blockE = event.getHook().getLocation().getBlock();
        // Only permit for 'public' or more authorized
    	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from fishing."); // Debug
            event.setCancelled(true);
    	}
    }
	
    /**
     * Animals within an OwnedChunk can only be damaged by the Owner, a Co-Owner, or an Admin
     *
     * @param event The EntityDamageByEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity wounded = event.getEntity();
        Entity attacker = event.getDamager();
        if (attacker instanceof Player && wounded instanceof Animals) {
    	    Player player = (Player) attacker;
        	Block blockP= player.getLocation().getBlock();
        	Block blockE = wounded.getLocation().getBlock();
            // Only permit for 'coowner' or more authorized
        	if (Terrains.canInterractHere(player, blockP, blockE) > 6) {
	    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from damaging a " + wounded.getType() + "."); // Debug
	            event.setCancelled(true);
        	}
        }
    }
}
