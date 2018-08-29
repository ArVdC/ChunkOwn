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
	        case ACACIA_DOOR:
	        case BIRCH_DOOR:
	        case OAK_DOOR:
	        case DARK_OAK_DOOR:
	        case JUNGLE_DOOR:
	        case SPRUCE_DOOR:
	        case IRON_DOOR:
	        case ACACIA_TRAPDOOR:
	        case BIRCH_TRAPDOOR:
	        case OAK_TRAPDOOR:
	        case DARK_OAK_TRAPDOOR:
	        case JUNGLE_TRAPDOOR:
	        case SPRUCE_TRAPDOOR:
	        case IRON_TRAPDOOR:
	        case ACACIA_FENCE_GATE: 
	        case BIRCH_FENCE_GATE:
	        case OAK_FENCE_GATE:
	        case DARK_OAK_FENCE_GATE: 
	        case JUNGLE_FENCE_GATE:
	        case SPRUCE_FENCE_GATE:
	        /* Redstone activation */
	        case REPEATER:
	        case COMPARATOR:
	        case LEVER:
		    /* FLowers and Saplings */
	        case FLOWER_POT:
	        case SUNFLOWER:
	        case DANDELION_YELLOW:
	        case CHORUS_FLOWER:
	        case POTTED_ACACIA_SAPLING:
	        case POTTED_BIRCH_SAPLING:
	        case POTTED_OAK_SAPLING:
	        case POTTED_DARK_OAK_SAPLING:
	        case POTTED_JUNGLE_SAPLING:
	        case POTTED_SPRUCE_SAPLING:
	        case POTTED_ALLIUM:
	        case POTTED_AZURE_BLUET:
	        case POTTED_BLUE_ORCHID:
	        case POTTED_BROWN_MUSHROOM:
	        case POTTED_CACTUS:
	        case POTTED_DANDELION:
	        case POTTED_DEAD_BUSH:
	        case POTTED_FERN:
	        case POTTED_OXEYE_DAISY:
	        case POTTED_POPPY:
	        case POTTED_RED_MUSHROOM:
	        case POTTED_RED_TULIP: 
	        case POTTED_WHITE_TULIP:
	        case POTTED_PINK_TULIP:
	        case POTTED_ORANGE_TULIP: 
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
	        case LIGHT_GRAY_SHULKER_BOX:
	        case WHITE_SHULKER_BOX:
	        case YELLOW_SHULKER_BOX:
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
	        case ACACIA_BUTTON:
	        case BIRCH_BUTTON:
	        case OAK_BUTTON:
	        case DARK_OAK_BUTTON:
	        case JUNGLE_BUTTON:
	        case SPRUCE_BUTTON:
	        case STONE_BUTTON:
			/* Containers */
	        case CAULDRON:
			/* Inventories */
	        case ENCHANTING_TABLE:
	        case BREWING_STAND:
	        case JUKEBOX :
	        case DAMAGED_ANVIL:
	        case CHIPPED_ANVIL:
	        case ANVIL:
	        case FURNACE:
	        case FURNACE_MINECART: // TODO
	        case CRAFTING_TABLE: // TODO
	        case CHEST:
	        case ENDER_CHEST:
	        	// Return if the Event was not a right-click
	            if (action != Action.RIGHT_CLICK_BLOCK) {
	                return;
	            }
			/* Redstone activation */
	        case ACACIA_PRESSURE_PLATE:
	        case BIRCH_PRESSURE_PLATE:
	        case OAK_PRESSURE_PLATE:
	        case DARK_OAK_PRESSURE_PLATE:
	        case JUNGLE_PRESSURE_PLATE:
	        case SPRUCE_PRESSURE_PLATE:
	        case STONE_PRESSURE_PLATE:
	        case HEAVY_WEIGHTED_PRESSURE_PLATE:
	        case LIGHT_WEIGHTED_PRESSURE_PLATE:
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
