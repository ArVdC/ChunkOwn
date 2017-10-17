package net.vdcraft.arvdc.terrains;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.PermissionAttachment;

/**
 * Detects Player Names in chat to share terrains
 *
 * @author ArVdC
 */
public class TrChatListener implements Listener {

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChatPlayerName(AsyncPlayerChatEvent event) {
    	
		String coOwnerName = event.getMessage();
    	Player player = event.getPlayer();
    
    	if (Terrains.hasPermission(player, "PlayerNamePlus") || Terrains.hasPermission(player, "PlayerNameMoins")) {
    		event.setCancelled(true);
    		Boolean addRemove = player.hasPermission("terrains.PlayerNamePlus") ? true : false;
        	TrCommand.coowner(player, addRemove, coOwnerName);
        	// Remove Permission 
    		PermissionAttachment attachment = TrCommand.tempAttachmentList.get(player.getUniqueId());
    		TrCommand.tempAttachmentList.remove(player.getUniqueId());
    		player.removeAttachment(attachment);
    	}
    }

}
