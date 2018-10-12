package moe.plushie.armourers_workshop.common.capability.wardrobe;

import java.util.BitSet;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IWardrobeCapability {
    
    public int getExtraColour(ExtraColourType type);
    
    public byte[] getExtraColourByte(ExtraColourType type);
    
    public void setExtraColour(ExtraColourType type, int colour);
    
    public void setExtraColourByte(ExtraColourType type, byte[] colour);
    
    public byte[] getAllExtraColours();
    
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
    
    public enum ExtraColourType {
        SKIN,
        HAIR,
        EYE,
        ACC
    }
}
