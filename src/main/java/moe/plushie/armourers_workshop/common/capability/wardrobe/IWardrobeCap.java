package moe.plushie.armourers_workshop.common.capability.wardrobe;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import net.minecraft.entity.player.EntityPlayerMP;

public interface IWardrobeCap {
    
    public ExtraColours getExtraColours();
    
    public ISkinDye getDye();
    
    /**
     * Syncs capability data to a player.
     * 
     * @param entityPlayer Player to sync to.
     */
    public void syncToPlayer(EntityPlayerMP entityPlayer);

    /**
     * Syncs capability data to all players tracking the entity.
     */
    public void syncToAllTracking();
    
    public void sendUpdateToServer();
}
