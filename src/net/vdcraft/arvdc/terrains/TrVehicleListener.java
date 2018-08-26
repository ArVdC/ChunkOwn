package net.vdcraft.arvdc.terrains;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

/**
 * Listens for vehicles related events
 *
 * @author Codisimus @author ArVdC
 */
public class TrVehicleListener implements Listener {

    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can enter a vehicle
     *
     * @param event The VehicleEnterEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onMount(VehicleEnterEvent event) {
    	if (event.getEntered() instanceof Player) {
    	    Player player = (Player)event.getEntered();
        	Block blockP = player.getLocation().getBlock();
        	Block blockE = event.getVehicle().getLocation().getBlock();
            // Only permit for 'public' or more authorized 
        	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
        		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from entering a " + event.getVehicle().getName()); // Debug
	            event.setCancelled(true);
	        }
    	}
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can place a vehicle on tracks
     *
     * @param event The VehicleCreateEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onVehiclePlace(VehicleCreateEvent event) {
	    Vehicle vehicle = event.getVehicle();
	    Block blockE = vehicle.getLocation().getBlock();
		Player player;
	    for (int i = 0; i < 6; i++) {
        	List<Entity> entitiesList = vehicle.getNearbyEntities(i, i, i);
	    	for (Entity nearestEntity : entitiesList) {
	    		if (nearestEntity instanceof Player) {
	    			player = (Player) nearestEntity;
	    		    Block blockP = player.getLocation().getBlock();
	    			String item = player.getInventory().getItemInMainHand().getType().toString();
	        		if (item.contains(vehicle.getName())) {
	    	            // Only permit for 'public' or more authorized
	                	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
	                		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from placing a " + vehicle.getName()); // Debug
		                    event.setCancelled(true);
				            return;
		            	}
	    			}
	    		}
	    	}
    	}
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can interact with vehicle
     *
     * @param event The PlayerInteractEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void PlayerInteractWithVehicle(PlayerInteractEntityEvent event) {
	    Player player = event.getPlayer();
    	Block blockP = player.getLocation().getBlock();
    	Block blockE = event.getRightClicked().getLocation().getBlock();
    	Integer limit;
    	switch (event.getRightClicked().getType()) {
        case BOAT:
        case MINECART:
            // Only permit for 'public' or more authorized 
        	limit = 7;
        	break;
        case MINECART_HOPPER:
        case MINECART_CHEST:
        case MINECART_FURNACE:
        case MINECART_TNT:
        case MINECART_COMMAND:
        case MINECART_MOB_SPAWNER:
            // Only permit for 'coowner' or more authorized 
        	limit = 6;
        	break;
		default:
            return;
    	}
    	if (Terrains.canInterractHere(player, blockP, blockE) > limit) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from interact with a vehicle " + event.getRightClicked().getType()); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Vehicles within an OwnedChunk can only be damaged by the Owner, a Co-Owner, or an Admin
     *
     * @param event The VehicleDamageEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onVehicleDamage(VehicleDamageEvent event) {
        Player player = null;
        Entity entity = event.getAttacker();
        Block blockE = event.getVehicle().getLocation().getBlock();
        if (entity instanceof Player) {
            player = (Player)entity;
	        Block blockP = player.getLocation().getBlock();
	    	Integer limit;
	    	switch (event.getVehicle().getType()) {
	        case MINECART_HOPPER:
	        case MINECART_CHEST:
	        case MINECART_FURNACE:
	        case MINECART_TNT:
	        case MINECART_COMMAND:
	        case MINECART_MOB_SPAWNER:
	            // Only permit for 'coowner' or more authorized 
	        	limit = 6;
	        	break;
			default:
		        // Only permit for 'public' or more authorized 
	        	limit = 7;
	    	}
	    	if (Terrains.canInterractHere(player, blockP, blockE) > limit) {
	    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from damaging a " + event.getVehicle().getName()); // Debug
	            event.setCancelled(true);
	    	}
        }
    }

    /**
     * Vehicles within an OwnedChunk can only be destroyed by the Owner, a Co-Owner, or an Admin
     *
     * @param event The VehicleDestroyEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Player player = null;
        Entity entity = event.getAttacker();
        Block blockE = event.getVehicle().getLocation().getBlock();
        if (entity instanceof Player) {
            player = (Player)entity;
	        Block blockP = player.getLocation().getBlock();
	        // Only permit for 'public' or more authorized 
	    	if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
	    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from destroying a " + event.getVehicle().getName()); // Debug
	            event.setCancelled(true);
	        }
        }
    }
}
