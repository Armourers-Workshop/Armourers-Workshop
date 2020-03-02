package moe.plushie.armourers_workshop.api.common.skin.entity;

import net.minecraft.entity.EntityLivingBase;

public interface ISkinnableEntityRegisty {
    
    public void registerEntity(ISkinnableEntity skinnableEntity);
    
    public ISkinnableEntity getSkinnableEntity(EntityLivingBase entity);
    
    public boolean isValidEntity(EntityLivingBase entity);
}
