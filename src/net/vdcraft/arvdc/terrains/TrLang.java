package net.vdcraft.arvdc.terrains;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.YamlConfiguration;

import net.vdcraft.arvdc.terrains.Terrains;

/**
 * Loads Plugin and manages Data/Permissions
 *
 * @author Codisimus
 */
public class TrLang {

    public static void load() {

        File lgFileYaml = new File(Terrains.dataFolder, "/lang.yml");
    	
        if (!lgFileYaml.exists()) {
        	copy(Terrains.plugin.getResource("lang.yml"), lgFileYaml);
        }
        Terrains.langConf = YamlConfiguration.loadConfiguration(lgFileYaml); 
    	Terrains.logger.info("The language file was loaded."); // Console loaded file msg

        /* Messages */
        String string = "PLUGIN LANG.YML MUST BE REGENERATED!";
        TrMsg.prefix = loadString("HeaderPrefix", string);
        TrMsg.terrSing = loadString("HeaderTrSingular", string);
        TrMsg.terrPlur = loadString("HeaderTrPlural", string);
        TrMsg.domicile = loadString("HeaderDomicile", string);
        TrMsg.permission = loadString("PermissionMessage", string);
        TrMsg.doNotOwn = loadString("DoNotOwnMessage", string);
        TrMsg.claimed = loadString("AlreadyClaimedMessage", string);
        TrMsg.limit = loadString("LimitReachedMessage", string);
        TrMsg.ownLimit = loadString("OwnLimit", string);
        TrMsg.underLimit = loadString("UnderTheLimit", string);
        TrMsg.unclaimed = loadString("UnclaimedMessage", string);
        TrMsg.buyFree = loadString("BuyFreeMessage", string);
        TrMsg.insufficientFunds = loadString("InsufficientFundsMessage", string);
        TrMsg.insufficientCredits = loadString("InsufficientCreditsMessage", string);
        TrMsg.refundMessage = loadString("RefundMessage", string);
        TrMsg.buy = loadString("BuyMessage", string);
        TrMsg.sell = loadString("SellMessage", string);
        TrMsg.charge = loadString("ChargeMessage", string);
        TrMsg.clearConfirm = loadString("ClearConfirm", string);
        TrMsg.adminSell = loadString("AdminSellMessage", string);
        TrMsg.adminSold = loadString("SoldByAdminMessage", string);
        TrMsg.groupLand = loadString("MustGroupLandMessage", string);
        TrMsg.worldGuard = loadString("WorldGuardMessage", string);
        TrMsg.disabledWorld = loadString("DisabledWorldMessage", string);
        TrMsg.enabledAlarm = loadString("EnabledAlarmMessage", string);
        TrMsg.disabledAlarm = loadString("DisabledAlarmMessage", string);
        TrMsg.coownersList = loadString("CoownersList", string);
        TrMsg.noCoownersList = loadString("NoCoownersList", string);
        TrMsg.notAnOwner = loadString("NotAnOwner", string);
        TrMsg.noOwnedTerrain = loadString("NoOwnedTerrain", string);
        TrMsg.noMoreTerrain = loadString("NoMoreTerrain", string);
        TrMsg.noPlayerOwnedTerrain = loadString("NoPlayerOwnedTerrain", string);
        TrMsg.numberOfOwnedTerrain = loadString("NumberOfOwnedTerrain", string);
        TrMsg.infoSelf = loadString("InfoSelf", string);
        TrMsg.infoOther = loadString("InfoOther", string);
        TrMsg.infoShared = loadString("InfoShared", string);
        TrMsg.infoNonShared = loadString("InfoNonShared", string);
        TrMsg.shareStatutAlready = loadString("ShareStatutAlready", string);
        TrMsg.shareOn = loadString("ShareOn", string);
        TrMsg.shareOnConfirm = loadString("ShareOnConfirm", string);
        TrMsg.shareOff = loadString("ShareOff", string);
        TrMsg.shareOffConfirm = loadString("ShareOffConfirm", string);
        TrMsg.shareNot = loadString("ShareNot", string);
        TrMsg.terrainDetail = loadString("TerrainDetail", string);
        TrMsg.shareStateNormal = loadString("ShareStateNormal", string);
        TrMsg.shareStatePublic = loadString("ShareStatePublic", string);
        TrMsg.shareStatePrivate = loadString("ShareStatePrivate", string);
        TrMsg.alertEnterSelf = loadString("AlertEnterSelf", string);
        TrMsg.alertEnterOwned = loadString("AlertEnterOwned", string);
        TrMsg.alertLeftSelf = loadString("AlertLeftSelf", string);
        TrMsg.alertLeftOwned = loadString("AlertLeftOwned", string);
        TrMsg.alarmEnterOwned = loadString("AlarmEnterOwned", string);
        TrMsg.alarmLeftOwned = loadString("AlarmLeftOwned", string);
        TrMsg.domicileSet = loadString("DomicileSet", string);
        TrMsg.noMoreDomicile = loadString("NoMoreDomicile", string);
        TrMsg.noDomicileSelf = loadString("NoDomicileSelf", string);
        TrMsg.noDomicileOther = loadString("NoDomicileOther", string);
        TrMsg.privateDomicile = loadString("PrivateDomicile", string);
        TrMsg.tpDomicileSelf = loadString("TpDomicileSelf", string);
        TrMsg.tpDomicileOther = loadString("TpDomicileOther", string);
    }

    /**
     * Loads the given key and prints an error if the key is missing
     *
     * @param key The key to be loaded
     * @return The String value of the loaded key
     */
    private static String loadString(String key, String defaultString) {
    	try {
    		if (Terrains.langConf.getKeys(false).contains(key)) {
    			if (key.toLowerCase().contains("header")) {
    				return TrMsg.formatHeaders(Terrains.langConf.getString(key));
    			}
    			return TrMsg.format(Terrains.langConf.getString(key));
	        }
    	} catch (Exception e) {
            Terrains.logger.severe(Terrains.missValueMsg + " " + key);
            Terrains.logger.severe("Please regenerate the lang.yml file (delete the old file to allow a new one to be created)");
            Terrains.logger.severe(Terrains.noTicketMsg);
    	}
    	return defaultString;
    }
    
	/** 
	 * Export files from the .jar
	 */
	public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0) {
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
