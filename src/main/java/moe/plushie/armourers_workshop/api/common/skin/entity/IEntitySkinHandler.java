package moe.plushie.armourers_workshop.api.common.skin.entity;

import net.minecraft.entity.EntityLivingBase;

public interface IEntitySkinHandler {
    
    public void registerEntity(ISkinnableEntity skinnableEntity);
    
    public boolean isValidEntity(EntityLivingBase entity);
}
