package riskyken.armourersWorkshop.common.skin.cache;

import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinIdentifier;

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
