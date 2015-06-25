package riskyken.minecraftWrapper.common.entity;

import net.minecraft.entity.EntityLivingBase;

public class EntityLivingBasePointer extends EntityPointer {

    public EntityLivingBasePointer(EntityLivingBase entityLivingBase) {
        super(entityLivingBase);
    }
    
    public EntityLivingBase getEntityLivingBase() {
        return (EntityLivingBase) entity;
    }
}
