package net.vdcraft.arvdc.terrains;

/**
 * Holds messages that are displayed to users of this plugin
 *
 * @author Codisimus @author ArVdC
 */
public class TrMsg {
    public static String prefix;
    public static String terrSing;
    public static String terrPlur;
    public static String domicile;
    public static String permission;
    public static String doNotOwn;
    public static String claimed;
    public static String limit;
    public static String ownLimit;
    public static String underLimit;
    public static String unclaimed;
    public static String buyFree;
    public static String insufficientFunds;
    public static String insufficientCredits;
    public static String refundMessage;
    public static String buy;
    public static String sell;
    public static String charge;
    public static String clearConfirm;
    public static String adminSell;
    public static String adminSold;
    public static String groupLand;
    public static String worldGuard;
    public static String disabledWorld;
    public static String enabledAlarm;
    public static String disabledAlarm;
    public static String coownersList;
    public static String noCoownersList;
    public static String notAnOwner;
    public static String noOwnedTerrain;
    public static String noMoreTerrain;
    public static String noPlayerOwnedTerrain;
    public static String numberOfOwnedTerrain;
    public static String infoSelf;
    public static String infoOther;
    public static String infoShared;
    public static String infoNonShared;
    public static String shareStatutAlready;
    public static String shareOn;
    public static String shareOnConfirm;
    public static String shareOff;
    public static String shareOffConfirm;
    public static String shareNot;
    public static String terrainDetail;
    public static String shareStateNormal;
    public static String shareStatePublic;
    public static String shareStatePrivate;
    public static String alertEnterSelf;
    public static String alertEnterOwned;
    public static String alertLeftSelf;
    public static String alertLeftOwned;
    public static String alarmEnterOwned;
    public static String alarmLeftOwned;
    public static String domicileSet;
    public static String noMoreDomicile;
    public static String noDomicileSelf;
    public static String noDomicileOther;
    public static String privateDomicile;
    public static String tpDomicileSelf;
    public static String tpDomicileOther;
    public static String shortHelpMsg;
    /**
     * Adds various Unicode characters and colors to a string
     *
     * @param string The string being formated
     * @return The formatted String
     */
    public static String formatHeaders(String string) {
    	return string.replace("&", "§");
    }

    /**
     * Adds various Unicode characters and colors to a string
     *
     * @param string The string being formated
     * @return The formatted String
     */
    public static String format(String string) {
    	return string.replace("&", "§").replace("<prefix>", prefix).replace("<terrain>", terrSing).replace("<terrains>", terrPlur).replace("<domicile>", domicile);
    }
}
