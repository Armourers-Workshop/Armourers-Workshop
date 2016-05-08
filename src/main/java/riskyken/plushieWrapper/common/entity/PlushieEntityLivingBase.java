package riskyken.plushieWrapper.common.entity;

import net.minecraft.entity.EntityLivingBase;

public class PlushieEntityLivingBase extends PlushieEntity {

    public PlushieEntityLivingBase(EntityLivingBase entityLivingBase) {
        super(entityLivingBase);
    }
    
    public EntityLivingBase getEntityLivingBase() {
        return (EntityLivingBase) entity;
    }
}
