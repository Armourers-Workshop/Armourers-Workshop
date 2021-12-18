package moe.plushie.armourers_workshop.core.api.common.capability;

import moe.plushie.armourers_workshop.core.api.common.IExtraColours;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import moe.plushie.armourers_workshop.core.api.common.skin.entity.ISkinnableEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IWardrobeCap {
    
    public IExtraColours getExtraColours();
    
    public ISkinDye getDye();
    
    public ISkinnableEntity getSkinnableEntity();
    
    /**
     * Syncs capability data to a player.
     * 
     * @param entityPlayer Player to sync to.
     */
    public void syncToPlayer(ServerPlayerEntity entityPlayer);

    /**
     * Syncs capability data to all players tracking the entity.
     */
    public void syncToAllTracking();
    
    public void sendUpdateToServer();
}
