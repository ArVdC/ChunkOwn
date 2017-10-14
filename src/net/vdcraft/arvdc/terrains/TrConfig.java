package net.vdcraft.arvdc.terrains;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.bukkit.World;

/**
 * Loads Plugin and manages Data/Permissions
 *
 * @author Codisimus
 */
public class TrConfig {
    
    private static Properties p;
    
    public static void load() {
        //Load Config settings
        FileInputStream fis = null;
        try {
            //Copy the file from the jar if it is missing
            File file = new File(Terrains.dataFolder + "/config.properties");
            if (!file.exists()) {
                Terrains.plugin.saveResource("config.properties", true);
            }

            //Load config file
            p = new Properties();
            fis = new FileInputStream(file);
            p.load(fis);

            /* Prices */
            Econ.buyPrice = loadDouble("BuyPrice", 50);
            Econ.sellPrice = loadDouble("SellPrice", 45);
            Econ.buyMultiplier = loadDouble("BuyMultiplier", 1.1);
            Econ.sellMultiplier = loadDouble("SellMultiplier", 1.1);
            Econ.domicileSetPrice = loadDouble("DomicileSetPrice", 10);
            Econ.domicileTpPrice = loadDouble("DomicileTpPrice", 1);
            Econ.domicileFriendPrice = loadDouble("DomicileFriendPrice", 2);

            /* Other */
            Terrains.lowerLimit = loadInt("OwnLowerLimit", 1);
            TrCommand.wgSupport = loadBool("WorldGuardSupport", false);
            TrCommand.dmSupport = loadBool("DynmapSupport", false);
            TrCommand.dmTrack = loadString("DynmapTrack", "Domiciles");
            TrCommand.dmLabel = loadString("DynmapLabel", "[Domiciles]");
            TrCommand.dmMarkerName = loadString("DynmapMarkerName", "Domicile de <joueur>");
            TrCommand.dmMarkerIcon = loadString("DynmapMarkerIcon", "default");
            TrCommand.dmLayer = loadInt("DynmapLayerPrio", 0);
            TrCommand.effectDuration = loadLong("EffectDuration", 100);
            TrCommand.customCommand = loadString("CustomCommand", "");

            String data = loadString("EnabledOnlyInWorlds", "");
            if (!data.isEmpty()) {
                for (String s : data.split(", ")) {
                    World world = Terrains.server.getWorld(s);
                    if (world != null) {
                        Terrains.worlds.add(world);
                    }
                }
            }
        } catch (Exception missingProp) {
            Terrains.logger.severe("Failed to load ChunkOwn Config");
            missingProp.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Loads the given key and prints an error if the key is missing
     *
     * @param key The key to be loaded
     * @return The String value of the loaded key
     */
    private static String loadString(String key, String defaultString) {
        if (p.containsKey(key)) {
            return p.getProperty(key);
        } else {
            Terrains.logger.severe(Terrains.missValueMsg + " " + key);
            Terrains.logger.severe("Please regenerate the config.properties file (delete the old file to allow a new one to be created)");
            Terrains.logger.severe(Terrains.noTicketMsg);
            return defaultString;
        }
    }

    /**
     * Loads the given key and prints an error if the key is not an Integer
     *
     * @param key The key to be loaded
     * @return The Integer value of the loaded key
     */
    private static int loadInt(String key, int defaultValue) {
        String string = loadString(key, null);
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            Terrains.logger.severe("The setting for " + key + " must be a valid integer");
            Terrains.logger.severe(Terrains.noTicketMsg);
            return defaultValue;
        }
    }

    /**
     * Loads the given key and prints an error if the key is not a Double
     *
     * @param key The key to be loaded
     * @return The Double value of the loaded key
     */
    private static double loadDouble(String key, double defaultValue) {
        String string = loadString(key, null);
        try {
            return Double.parseDouble(string);
        } catch (Exception e) {
            Terrains.logger.severe("The setting for " + key + " must be a valid number");
            Terrains.logger.severe(Terrains.noTicketMsg);
            return defaultValue;
        }
    }

    /**
     * Loads the given key and prints an error if the key is not a Long
     *
     * @param key The key to be loaded
     * @return The Long value of the loaded key
     */
    private static long loadLong(String key, long defaultValue) {
        String string = loadString(key, null);
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            Terrains.logger.severe("The setting for " + key + " must be a valid number");
            Terrains.logger.severe(Terrains.noTicketMsg);
            return defaultValue;
        }
    }

    /**
     * Loads the given key and prints an error if the key is not a boolean
     *
     * @param key The key to be loaded
     * @return The boolean value of the loaded key
     */
    private static boolean loadBool(String key, boolean defaultValue) {
        String string = loadString(key, null);
        try {
            return Boolean.parseBoolean(string);
        } catch (Exception e) {
            Terrains.logger.severe("The setting for " + key + " must be 'true' or 'false' ");
            Terrains.logger.severe(Terrains.noTicketMsg);
            return defaultValue;
        }
    }
}
