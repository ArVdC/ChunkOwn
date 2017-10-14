package net.vdcraft.arvdc.terrains;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Block PvP within Owned Chunks which have protection
 *
 * @author Codisimus @author ArVdC
 */
public class TrDamageListener implements Listener {
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Return if the Entity damaged is not a Player
        Entity wounded = event.getEntity();
        if (!(wounded instanceof Player)) {
            return;
        }
        
        Entity attacker = event.getDamager();        
        
        if (attacker instanceof Projectile) {     	
            attacker = (Entity) ((Projectile) attacker).getShooter();
        }
    
        if (attacker instanceof Player) {  	
            Player player = (Player) attacker;
            
            // Return if the Player is suicidal
            if (player.equals(wounded)) {
                return;
            }
            
            Block blockP = attacker.getLocation().getBlock();
            Block blockE = wounded.getLocation().getBlock();
       	 	// Only permit for 'under the protection limit' or more authorized
	    	if (Terrains.canInterractHere(player, blockP, blockE) > 3) {
	    		if (Terrains.debug) Terrains.logger.info("Prevent " + player.getName() + " from damaging a " + wounded.getType() + "."); // Debug
	            event.setCancelled(true);
	    	}
        }
    }
}
