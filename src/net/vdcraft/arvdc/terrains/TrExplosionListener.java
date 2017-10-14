package net.vdcraft.arvdc.terrains;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Block Explosions within Owned Chunks which have protection
 *
 * @author Codisimus
 */
public class TrExplosionListener implements Listener {
	
    /**
     * Within an OwnedChunk entities can't explode
     *
     * @param event The EntityExplodeEvent that occurred
     */
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onExplode(EntityExplodeEvent event) {
	    Player player = null;
    	Block blockE = event.getEntity().getLocation().getBlock();
    	LinkedList<Location> blocksToDestroy = new LinkedList<Location>();
		Integer interact = Terrains.canInteractHere(player, blockE);
		
        // Cancel all explosions sources in OwnedTerrains
    	if (interact > 6) { // Only permit for 'coowner' or more authorized
    		if (Terrains.debug) Terrains.logger.info("Some " + event.getEntity().getType() + " explodes in a onwed chunk, cancel it !"); // Debug
        	event.setCancelled(true);
        } else if (interact >= 2) { // Do nothing in 'np worlds' or 'WG region'
        // If explosion is permitted, if some blocks are outside any OwnedTerrain, clear them
    		for (Block b : event.blockList()) {
    			blocksToDestroy.add(b.getLocation());
    		}
    		if (Terrains.debug) Terrains.logger.info("First, preserve all the blocks concern by this event,"); // Debug
    		event.blockList().clear();
			for (Location l : blocksToDestroy) {
	    		Block b = l.getBlock();
	    		interact = Terrains.canInteractHere(player, b);
		    	if (interact <= 3) { // Only clear blocks for 'underY' or 'unclaimed'
		    		if (Terrains.debug) Terrains.logger.info("then artificially destroy this block who isn't in the onwed terrain."); // Debug
		    		b.setType(Material.AIR);
		    	}
			}
		}
    }
	
    /**
     * Within an OwnedChunk blocks can not explode // Doesn't work at all !
     *
     * @param event The BlockExplodeEvent that occurred
     */
    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockExplode(BlockExplodeEvent event) {
	    Player player = null;
    	Block blockE = event.getBlock();
        // Only permit for 'coowner' or more authorized
    	if (Terrains.canInteractHere(player, blockE) > 6) {
        		if (Terrains.debug) Terrains.logger.info("Some " + event.getBlock().getType() + " block explodes in a onwed chunk, cancel it !"); // Debug
        		event.setCancelled(true);
        }
    }

}
