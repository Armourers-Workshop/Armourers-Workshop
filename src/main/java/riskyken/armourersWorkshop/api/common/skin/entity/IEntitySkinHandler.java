package riskyken.armourersWorkshop.api.common.skin.entity;

import net.minecraft.entity.Entity;

public interface IEntitySkinHandler {
    
    public void registerEntity(ISkinnableEntity skinnableEntity);
    
    public boolean isValidEntity(Entity entity);
}
