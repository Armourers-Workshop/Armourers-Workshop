package riskyken.armourersWorkshop.api.common.skin.npc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public interface INpcSkinDataHandler {
    
    public void registerEntity(Class<? extends EntityLivingBase> entityClass);
    
    public boolean isValidEntity(Entity entity);
}
