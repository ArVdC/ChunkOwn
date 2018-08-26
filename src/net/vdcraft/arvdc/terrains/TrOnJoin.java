package net.vdcraft.arvdc.terrains;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

/**
 * Detects Player Names in chat to share terrains
 *
 * @author ArVdC
 */
public class TrOnJoin implements Listener {

    @SuppressWarnings("unlikely-arg-type")
	@EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
    	
    	Player player = event.getPlayer();    	
		TerrainOwner owner = Terrains.getOwner(player.getName());
    	
        // Cancel if the Player doesn't have a Domicile    	
    	if (String.valueOf(owner.domicile).equals("null")) {
    		return;
    	}

        // Add the "terrain.tpdomicile" permission
    	if (!TrCommand.domicileAttachmentList.containsValue(player.getUniqueId())) {
	        PermissionAttachment attachment = player.addAttachment(Terrains.plugin);
			attachment.setPermission("terrains."+"tpdomicile", true);
			TrCommand.domicileAttachmentList.put(player.getUniqueId(), attachment);
    	}
    }	

}
