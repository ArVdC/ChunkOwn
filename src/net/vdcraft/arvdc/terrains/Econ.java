package net.vdcraft.arvdc.terrains;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;

/**
 * Manages payments
 *
 * @author ArVdC
 */
public class Econ {
    static Economy economy;
    static double buyPrice;
    static double sellPrice;
    static double buyMultiplier;
    static double sellMultiplier;
    static double domicileSetPrice;
    static double domicileTpPrice;
    static double domicileFriendPrice;

    /**
     * Charges a Player a given amount of money, which goes to a Player/Bank
     *
     * @param player The name of the Player to be charged
     * @param source The Player/Bank that will receive the money
     * @param amount The amount that will be charged
     * @return True if the transaction was successful
     */
    public static boolean achat(Player player) {
        String name = player.getName();
        double price = getBuyPrice(name);

        if (economy != null) {
            //Cancel if the Player cannot afford the transaction
            if (!economy.has(player, price)) {
                player.sendMessage(TrMsg.insufficientFunds.replace("<price>", format(price)));
                return false;
            }

            economy.withdrawPlayer(player, price);
        }

        player.sendMessage(TrMsg.buy.replace("<price>", format(price)));
        return true;
    }

    /**
     * Adds the sellPrice to the Player's total balance. Admin can force a Player to sell all his lands
     *
     * @param admin The Player who is forcing the sale
     * @param seller The Player who is being forced to sell
     */
    public static void vente(Player admin, String ownerName) {
        String price = format(getSellPrice(ownerName));
        Player seller = Terrains.server.getPlayer(ownerName);

        if (economy != null) {
            economy.depositPlayer(seller, getSellPrice(ownerName));
        }

        if (admin != null) {
        	//Notify the Admin
        	admin.sendMessage(TrMsg.adminSell.replace("<price>", price).replace("<owner>", ownerName));
	        //Notify the Seller
	        if (seller != null) {
	            seller.sendMessage(TrMsg.adminSold.replace("<price>", price));
	        }
        } else {
        	seller.sendMessage(TrMsg.sell.replace("<price>", price));
        }
    }

    /**
     * Charges the Player the given amount of money
     *
     * @param player The Player being charged
     * @param amount The amount being charged
     * @return true if the transaction was successful;
     */
    public static boolean charge(Player player, double amount) {

        if (economy != null) {
            //Cancel if the Player cannot afford the transaction
            if (!economy.has(player, amount)) {
            	Double dif = amount - economy.getBalance(player);
                player.sendMessage(TrMsg.insufficientCredits.replace("<price>", format(dif))); 
                return false;
            }

            economy.withdrawPlayer(player, amount);
        }

        if (amount > 0) {
            player.sendMessage(TrMsg.charge.replace("<price>", format(amount)));
        }
        return true;
    }

    /**
     * Refunds the Player the given amount of money
     *
     * @param player The Player being refunded
     * @param amount The amount being refunded
     */
    public static void refund(Player player, double amount) {

        if (economy != null) {
            economy.depositPlayer(player, amount);
        }

        player.sendMessage(TrMsg.refundMessage.replace("<price>", format(amount)));
    }

    /**
     * Formats the money amount by adding the unit
     *
     * @param amount The amount of money to be formatted
     * @return The String of the amount + currency name
     */
    public static String format(double amount) {
        return economy == null ? "free" : economy.format(amount).replace(".00", "");
    }

    /**
     * Returns the BuyPrice for the given Player
     *
     * @param player The given Player
     * @return The calculated BuyPrice
     */
    public static double getBuyPrice(String player) {
        return Terrains.hasPermission(player, "free")
                ? 0
                : buyPrice * Math.pow(buyMultiplier, Terrains.getTerrainCounter(player));
    }

    /**
     * Returns the SellPrice for the given Player
     *
     * @param player The given Player
     * @return The calculated SellPrice
     */
    public static double getSellPrice(String player) {
        return Terrains.hasPermission(player, "free")
                ? 0
                : sellPrice * Math.pow(sellMultiplier, Terrains.getTerrainCounter(player) - 1);
    }
}
