package net.vdcraft.arvdc.terrains;

import java.util.Properties;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * An OwnedTerrain is a Chunk that a Player has bought
 * An OwnedTerrain has an Owner and a sharing status (=shareState)
 * The x-coordinate and z-coordinate together create a unique identifier
 *
 * @author ArVdC
 */
public class OwnedTerrain {
    public String world;
    public int x;
    public int z;
    public TerrainOwner owner;
    public String shareState = new String();

    /**
     * Constructs a new OwnedChunk
     *
     * @param chunk The chunk that the OwnedChunks represents
     */
    public OwnedTerrain(Chunk chunk, String owner) {
        this.world = chunk.getWorld().getName();
        this.x = chunk.getX();
        this.z = chunk.getZ();
        setOwner(owner);
    }

    /**
     * Constructs a new OwnedChunk
     *
     * @param world The name of the world of the Chunk
     * @param x The x-coordinate of the Chunk
     * @param z The z-coordinate of the Chunk
     * @param owner The name of the owner of the Chunk
     */
    public OwnedTerrain(String world, int x, int z, String owner) {
        this.world = world;
        this.x = x;
        this.z = z;
        setOwner(owner);
    }

    /**
     * Sets the ChunkOwner of the OwnedChunk and increments their chunkCounter by 1
     *
     * @param player The name of the owner of the Chunk
     */
    private void setOwner(String player) {
        owner = Terrains.findOwnerByPlayer(player);
        if (owner == null) {
            owner = new TerrainOwner(player);
        }
        owner.terrainsCounter++;
    }

    /**
     * Returns whether the given player is a Co-owner
     *
     * @param player The Player to be check for Co-ownership
     * @return true if the given player is a Co-owner
     */
    public boolean isCoOwner(Player player) {
        //Check to see if the Player is a Co-owner
        for (String coOwner: owner.coOwners) {
            if (coOwner.equalsIgnoreCase(player.getName())) {
                return true;
            }
        }
        //Return false because the Player is not a coowner
        return false;
    }
    
    public void save() {
    	
    	shareState = shareState.isEmpty() ? "normal" : shareState;
    	
        if (!Terrains.savedData.containsKey(world)) {
            Terrains.savedData.put(world, new Properties());
        }
        Properties p = Terrains.savedData.get(world);
        p.setProperty(x + "'" + z, owner.name + "," + shareState);

        Terrains.save(world);
    }

    @Override
    public String toString() {
    	Double xPos = (x * 16.0 + 8.0);
    	Double zPos = (z * 16.0 + 8.0);
        return TrMsg.terrainDetail.replace("<x>", xPos.toString()).replace("<z>", zPos.toString()).replace("<world>", world);
    }
}
