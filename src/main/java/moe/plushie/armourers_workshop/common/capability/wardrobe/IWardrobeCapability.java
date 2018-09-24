package moe.plushie.armourers_workshop.common.capability.wardrobe;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IWardrobeCapability {
    
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
}
