package net.vdcraft.arvdc.terrains;

import org.bukkit.Bukkit;

public class McVersionHandler extends Terrains {
	
	/** 
	 *  Get the version of the server and return only the MC decimal part
	 */
	public static Double KeepDecimalOfMcVersion() {
		Double minRequiredMcVersion = 13.0;
		String splitMarker = "oAoroVodoCo";
		String[] split1;
		String split2;
		String[] split3;
		String[] split4;
		String mcVersionString;
		Double mcVersion;
		String completeServerVersion = Bukkit.getVersion().toLowerCase();
		if(completeServerVersion.contains("(mc: 1.")) { // For usual version syntax
			completeServerVersion = completeServerVersion.replace("(mc: 1.", splitMarker);
			split1 = completeServerVersion.split(splitMarker);
			split2 = split1[1]; // Keep only what is after the "(mc: 1."
			split2 = split2.replace(")", splitMarker); // Tag the character ")" after the version value 
			
		} else if(completeServerVersion.contains("1.")) {  // For other type of syntax (less specific format, so it could crash sometimes)
			completeServerVersion = completeServerVersion.replace("1.", splitMarker);
			split1 = completeServerVersion.split(splitMarker);
			split2 = split1[1];
			split2 = split2.replace(")", splitMarker).replace("]", splitMarker).replace("-", splitMarker).replace("_", splitMarker).replace(" ", splitMarker);
		} else { // Use the latest version of MC
			mcVersion = minRequiredMcVersion;
			return mcVersion;
		}
		// Then, for the 2 first cases
		split3 = split2.split(splitMarker); // Keep only what was before a ")", "]", "-", "_" or " " character
		mcVersionString = split3[0]; // If version is in a " 1.x" format, keep it
		if(mcVersionString.contains(".")) { // But if version is in a " 1.x.x" format, check if a "0" needs to be add before the last number
			String mcVersionSplit = mcVersionString.replace(".", splitMarker);
			split4 = mcVersionSplit.split(splitMarker);
			String firstPart = split4[0];
			String secondPart = split4[1];
			if(secondPart.length() == 1) {
				secondPart = "0" + secondPart;
			}
			mcVersionString = firstPart + "." + secondPart;
		} 	
		try { // Check if value could be parsed as a double
    		mcVersion = Double.parseDouble(mcVersionString);
		} catch (NumberFormatException nfe) { // If not possible, use the latest version of MC
			mcVersion = minRequiredMcVersion;
		}
		return mcVersion;
	}

};