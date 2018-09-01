package moe.plushie.armourers_workshop.common.skin.cache;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import net.minecraft.entity.player.EntityPlayerMP;

public class SkinRequestMessage {
    
    private final ISkinIdentifier skinIdentifier;
    private final EntityPlayerMP player;
    
    public SkinRequestMessage(ISkinIdentifier skinIdentifier, EntityPlayerMP player) {
        this.skinIdentifier = skinIdentifier;
        this.player = player;
    }
    
    public ISkinIdentifier getSkinIdentifier() {
        return skinIdentifier;
    }
    
    public EntityPlayerMP getPlayer() {
        return player;
    }
}
