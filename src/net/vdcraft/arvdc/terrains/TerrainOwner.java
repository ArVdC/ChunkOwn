package net.vdcraft.arvdc.terrains;


import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * An ChunkOwner is a Player that can own Chunks
 *
 * @author Codisimus
 */
public class TerrainOwner {
    public String name;
    public int terrainsCounter;
    public Boolean alarm = true;
    public Location domicile = null;
    public LinkedList<String> coOwners = new LinkedList<String>();

    /**
     * Constructs a new TerrainOwner to represent the given Player
     *
     * @param player The given Player
     */
    public TerrainOwner(String player) {
        name = player;
    }

    /**
     * Send the given message to the TerrainOwner
     *
     * @param msg The message to be sent
     */
    public void sendMessage(String msg) {
        Player player = Terrains.server.getPlayer(name);
        if (player != null) {
            player.sendMessage(msg);
        }
    }

    /**
     * Sets the status of the Alarm option
     *
     * @param on The new status of the Alarm option
     */
    public void setAlarm(boolean onOff) {
        alarm = onOff;
    }

    /**
     * Sets the status of the Domicile location
     *
     * @param on The new status of the Domicile location
     */
    public void setDomicile(Location location) {
    	// TODO décomposer la location ?
        domicile = location;
    }
    

    /**
     * Write this TerrainOwner to file
     *
     */
    public void save() {
        Terrains.saveTerrainOwner(this);
    }
}
