package moe.plushie.armourers_workshop.common.capability.wardrobe;

import java.util.BitSet;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IWardrobeCapability {
    
    public int getSkinColour();
    
    public void setSkinColour(int skinColour);
    
    public int getHairColour();
    
    public void setHairColour(int hairColour);
    
    public BitSet getArmourOverride();
    
    public void setArmourOverride(BitSet armourOverride);
    
    /**
     * Syncs capability data to a player with a delay.
     * 
     * @param entityPlayer Player to sync to.
     * @param delay Delay time in ticks.
     */
    public void syncToPlayerDelayed(EntityPlayerMP entityPlayer, int delay);
    
    /**
     * Syncs capability data to a player.
     * 
     * @param entityPlayer Player to sync to.
     */
    public void syncToPlayer(EntityPlayerMP entityPlayer);

    /**
     * Syncs capability data to all players tracking the entity.
     */
    public void syncToAllAround();
    
    public void sendUpdateToServer();
}
