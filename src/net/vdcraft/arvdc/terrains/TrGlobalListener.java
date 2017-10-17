package net.vdcraft.arvdc.terrains;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;

/**
 * Listens for griefing and other annoying interaction events
 *
 * @author ArVdC
 */
public class TrGlobalListener implements Listener {

    /* Drop and pickup Events */
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can pickup items
     *
     * @param event The EntityPickupItemEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPickUpItem(EntityPickupItemEvent event) {
    	if (event.getEntity() instanceof Player) {
    	    Player player = (Player)event.getEntity();
        	Block blockE = event.getItem().getLocation().getBlock();
	        // Only permit for 'public' or more authorized
	    	if (Terrains.canInteractHere(player, blockE) > 7) {
	    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from picking up an item."); // Debug
	            event.setCancelled(true);
	        }
    	}
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can drop items
     *
     * @param event The EntityPickupItemEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void PlayerDropItem(PlayerDropItemEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = player.getTargetBlock(null, 100);
        // Only permit for 'public' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from dropping some item."); // Debug
            event.setCancelled(true);
        }
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can pickup arrows
     *
     * @param event The PlayerPickupArrowEvent that occurred
     */
    @SuppressWarnings("deprecation")
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPickUpArrow(PlayerPickupArrowEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getArrow().getLocation().getBlock();
        // Only permit for 'public' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from picking up an arrow."); // Debug
            event.setCancelled(true);
        }
    }

    /* Bed Events */
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can use a bed
     *
     * @param event The PlayerBedEnterEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBedEnter(PlayerBedEnterEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getBed();
        // Only permit for 'public' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from entering a bed."); // Debug
            event.setCancelled(true);
        }
    }

    /* Building/Griefing Events */

    /**
     * Multiple blocks can only be placed within an OwnedChunk by the Owner, a Co-Owner, or an Admin
     *
     * @param event The BlockMultiPlaceEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getBlock();
        // Only permit for 'coowner' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from placing a multiple block (like a bed, ...)"); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Blocks can only be placed within an OwnedChunk by the Owner, a Co-Owner, or an Admin
     *
     * @param event The BlockPlaceEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getBlock();
        // Only permit for 'coowner' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from placing a block."); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Blocks within an OwnedChunk can only be broken by the Owner, a Co-Owner, or an Admin
     *
     * @param event The BlockBreakEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getBlock();
        // Only permit for 'coowner' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from breaking a block."); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Signs within an OwnedChunk can only be changed by the Owner, a Co-Owner, or an Admin
     *
     * @param event The SignChangeEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getBlock();
        // Only permit for 'coowner' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from changing a sign."); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Blocks within an OwnedChunk can only be ignited by the Owner/Co-Owner
     *
     * @param event The BlockIgniteEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockIgnite(BlockIgniteEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getBlock();
        // Only permit for 'coowner' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 6) {
    		if (Terrains.debug && player != null) Terrains.logger.info("Prevent " + player.getName() + " from ignite a block."); // Debug
    		else Terrains.logger.info("Prevent block from igniting in an owned chunk."); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Fire cannot spread within an OwnedChunk
     *
     * @param event The BlockSpreadEvent that occurred
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockSpread(BlockSpreadEvent event) {
    	Block blockE = event.getBlock();
    	// Only permit for 'under the protection limit' or more authorized
        if (Terrains.canInteractHere(null, blockE) > 3) {
    		if (Terrains.debug) Terrains.logger.info("Prevent fire spread in an owned chunk."); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Blocks within an OwnedChunk cannot burn
     *
     * @param event The BlockBurnEvent that occurred
     */
    @EventHandler(ignoreCancelled = true, priority=EventPriority.LOWEST)
    public void onBlockBurn(BlockBurnEvent event) {
    	Block blockE = event.getBlock();
   	 	// Only permit for 'under the protection limit' or more authorized
        if (Terrains.canInteractHere(null, blockE) > 3) {
    		if (Terrains.debug) Terrains.logger.info("Prevent block from burning in an owned chunk."); // Debug
            event.setCancelled(true);
        }
    }

    /**
     * Eggs within an OwnedChunk can only be hatched by the Owner, a Co-Owner, or an Admin
     *
     * @param event The BlockIgniteEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEggThrow(PlayerEggThrowEvent event) {
        Player player = event.getPlayer();
    	Block blockE = player.getTargetBlock(null, 10);
        // Only permit for 'coowner' or more authorized
        if (Terrains.canInteractHere(player, blockE) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from throwing an egg."); // Debug
            event.setHatching(false);
        }
    }

    /**
     * Buckets can only be emptied within an OwnedChunk by the Owner, a Co-Owner, or an Admin
     *
     * @param event The PlayerBucketEmptyEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
    	Block blockE = event.getBlockClicked().getRelative(event.getBlockFace());
        // Only permit for 'coowner' or more authorized
        if (Terrains.canInteractHere(player, blockE) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from emptying his bukket."); // Debug
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    /**
     * Buckets can only be filled within a non-public OwnedChunk by the Owner, a Co-Owner, or an Admin
     *
     * @param event The PlayerBucketFillEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
    	Block blockE = event.getBlockClicked();
        // Only permit for 'public' or more authorized
        if (Terrains.canInteractHere(player, blockE) > 7) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from filling his bukket."); // Debug
            event.setCancelled(true);
        }
    }
	
    /**
     * Within an OwnedChunk only the Owner, a Co-Owner, or an Admin can hang something on the wall
     *
     * @param event The HangingPlaceEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onHangingPlace(HangingPlaceEvent event) {
	    Player player = event.getPlayer();
    	Block blockE = event.getBlock();
    	Integer limit = 6;
    	if (event.getEntity() instanceof LeashHitch) limit = 7; // If Item is a LEAD (=leashHitch) permit for 'public' or more authorized
        // Else, permit for 'coowner' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > limit) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from placing a hanging " + event.getEntity().getType() + "."); // Debug
            event.setCancelled(true);
        }
    }

    /**
	 * Hangings within an OwnedChunk can only be broken by the Owner, a Co-Owner, or an Admin
	 *
	 * @param event The HangingBreakEvent that occurred
	 */
	//@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
	//public void onHangingBreak(HangingBreakEvent event) {
	//	if (event.getCause() != RemoveCause.ENTITY) {
	//		if (Terrains.debug) Terrains.logger.info("Prevent " + event.getCause() + ", don't exactly know why."); // Debug
	//		event.setCancelled(true);
	//	}
	//} TODO ... OR NOT TODO ?????

    /**
     * Hangings within an OwnedChunk can only be broken by the Owner, a Co-Owner, or an Admin
     *
     * @param event The HangingBreakByEntityEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        Player player = null;
        Entity entity = event.getRemover();
    	Block blockP = entity.getLocation().getBlock();
        if (entity instanceof Player) {
            player = (Player) entity;
        } else {
        	player = null;
        }
        // Only permit for 'coowner' or more authorized
        if (Terrains.canInteractHere(player, blockP) > 6) {
    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from breaking a hanging."); // Debug
            event.setCancelled(true);
        }
    }
}
