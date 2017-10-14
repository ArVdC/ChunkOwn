/**********************
**** TAB COMPLETER ****
**********************/

package net.vdcraft.arvdc.terrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Detects the first characters of the entered argument to complete it
 *
 * @author ArVdC
 */
public class TrTabCompleter implements TabCompleter {
	
	/****************
	*** VARIABLES ***
	****************/
	
	List<String> trCmdAdminList = Arrays.asList("reload");
	
	List<String> trCmdResidentList = Arrays.asList("info", "acheter");
	
	List<String> trCmdOwnerList = Arrays.asList("vendre", "toutVendre", "liste", "partagePlus", "partageMoins", "partageListe", "statutPublic", "statutNormal", "statutPrive", "alarm", "domicile", "domicileSet");
			
	List<String> trCmdArgsList(CommandSender sender) {
		
		List<String> trCmdArgsList = Arrays.asList("help");
		
		if (!(sender instanceof Player)) {
			return trCmdAdminList;
		}
		if (Terrains.hasPermission(sender.getName(), "admin")) {
			trCmdArgsList = Stream.concat(trCmdArgsList.stream(), trCmdAdminList.stream()).collect(Collectors.toList());
		}
		if (Terrains.hasPermission(sender.getName(), "resident")) {
			trCmdArgsList = Stream.concat(trCmdArgsList.stream(), trCmdResidentList.stream()).collect(Collectors.toList());
		}
		if (Terrains.isAnOwner(sender.getName())) {	
			trCmdArgsList = Stream.concat(trCmdArgsList.stream(), trCmdOwnerList.stream()).collect(Collectors.toList());
		}
		return trCmdArgsList;
	}

	/****************
	***** EVENT *****
	****************/
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		// List of player's sub-commands
		
		List<String> outputArgsList = new ArrayList<String>();
		
		if(command.getName().toLowerCase().equalsIgnoreCase(TrCommand.command)) {
			
			if (args.length == 1) {
				for(String verif : trCmdArgsList(sender)) {
					if(verif.toLowerCase().startsWith(args[0].toLowerCase())) outputArgsList.add(verif);
				}
			}
			
			if (args.length == 2) {
				if (args[0].toLowerCase().equalsIgnoreCase("partagePlus")) {
					for(Player verif : Terrains.server.getOnlinePlayers()) {
						if(verif.getName().toLowerCase().startsWith(args[1].toLowerCase()) && !verif.getName().toLowerCase().equals(sender.getName().toLowerCase())) outputArgsList.add(verif.getName());
					}
				}
				else if (args[0].toLowerCase().equalsIgnoreCase("partageMoins") || args[0].toLowerCase().equalsIgnoreCase("domicile")) {
					Player player = ((Player)sender).getPlayer();
					String playerName = player.getName();
					for (String verif : Terrains.getOwner(playerName).coOwners) {
						if(verif.toLowerCase().startsWith(args[1].toLowerCase())) outputArgsList.add(verif);
					}
				}
				else if (args[0].toLowerCase().equalsIgnoreCase("toutVendre") && Terrains.hasPermission(sender.getName(), "admin")) {
					for(Player verif : Terrains.server.getOnlinePlayers()) {
						if(verif.getName().toLowerCase().startsWith(args[1].toLowerCase()) && !verif.getName().toLowerCase().equals(sender.getName().toLowerCase())) outputArgsList.add(verif.getName());
					}
				}
			}
		return outputArgsList;
		} else {
			return null;
		}
	}
}
