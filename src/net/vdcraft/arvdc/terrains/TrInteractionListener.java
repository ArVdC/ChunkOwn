package net.vdcraft.arvdc.terrains;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Lock Chests/Doors & Disable Buttons and other Items
 *
 * @author Codisimus @author ArVdC
 */
public class TrInteractionListener implements Listener {
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Return if the Event was arm failing
        Action action = event.getAction();
        switch (action) {
	        case LEFT_CLICK_AIR: return;
	        case RIGHT_CLICK_AIR: return;
	        default: break;
        }        
        Player player = event.getPlayer();
        Block blockP = player.getLocation().getBlock();
        Block blockE = event.getClickedBlock();

        switch (blockE.getType()) {

        	/*  Owner actions */

        	/* Doors, Trap Doors an Fence Gates */
	        case WOOD_DOOR: 
	        case WOODEN_DOOR: 
	        case ACACIA_DOOR: 
	        case BIRCH_DOOR: 
	        case DARK_OAK_DOOR:
	        case JUNGLE_DOOR: 
	        case SPRUCE_DOOR:
	        case IRON_DOOR:
	        case IRON_DOOR_BLOCK:
	        case TRAP_DOOR:
	        case IRON_TRAPDOOR:
	        case FENCE_GATE: 
	        case ACACIA_FENCE_GATE: 
	        case BIRCH_FENCE_GATE: 
	        case DARK_OAK_FENCE_GATE:
	        case JUNGLE_FENCE_GATE:
	        case SPRUCE_FENCE_GATE:
	        /* Redstone activation */
	        case DIODE_BLOCK_OFF:
	        case DIODE_BLOCK_ON:
	        case REDSTONE_COMPARATOR_OFF: 
	        case REDSTONE_COMPARATOR_ON:
	        case LEVER:
		    /* Containers */
	        case FLOWER_POT:
		    /* Inventories */
	        case HOPPER:
	        case DROPPER:
	        case DISPENSER:
	        case BEACON:
	        case BLACK_SHULKER_BOX:
	        case BLUE_SHULKER_BOX:
	        case BROWN_SHULKER_BOX:
	        case CYAN_SHULKER_BOX:
	        case GREEN_SHULKER_BOX:
	        case GRAY_SHULKER_BOX:
	        case LIGHT_BLUE_SHULKER_BOX:
	        case LIME_SHULKER_BOX:
	        case MAGENTA_SHULKER_BOX:
	        case ORANGE_SHULKER_BOX:
	        case PINK_SHULKER_BOX:
	        case PURPLE_SHULKER_BOX:
	        case RED_SHULKER_BOX:
	        case WHITE_SHULKER_BOX:
	        case YELLOW_SHULKER_BOX:
	        case SILVER_SHULKER_BOX:
	            // Return if the Event was not a right-click
	            if (action != Action.RIGHT_CLICK_BLOCK) {
	                return;
	            }
	            // Only permit for 'coowner' or more authorized 
	            if (Terrains.canInterractHere(player, blockP, blockE) > 6) {
	            	if (Terrains.debug) Terrains.logger.info("Cancelling the " + action + " on a " + blockE.getType()); // Debug
	                event.setCancelled(true);
	                return;
	            }
	            
	        /*  Public actions */
	          
		    /* Redstone activation */
	        case WOOD_BUTTON:
	        case STONE_BUTTON:
			/* Containers */
	        case CAULDRON:
			/* Inventories */
	        case ENCHANTMENT_TABLE:
	        case BREWING_STAND:
	        case JUKEBOX :
	        case ANVIL:
	        case FURNACE:
	        case BURNING_FURNACE:
	        case WORKBENCH:
	        case CHEST:
	        case ENDER_CHEST:
	        	// Return if the Event was not a right-click
	            if (action != Action.RIGHT_CLICK_BLOCK) {
	                return;
	            }
			/* Redstone activation */
	        case STONE_PLATE:
	        case GOLD_PLATE:
	        case IRON_PLATE:
	        case WOOD_PLATE:
	            // Only permit for 'public' or more authorized 
	            if (Terrains.canInterractHere(player, blockP, blockE) > 7) {
	            	if (Terrains.debug) Terrains.logger.info("Cancelling the " + action + " on a " + blockE.getType()); // Debug
	                event.setCancelled(true);
	                return;
	            }
	
	        default: break;
        }
    }
}
