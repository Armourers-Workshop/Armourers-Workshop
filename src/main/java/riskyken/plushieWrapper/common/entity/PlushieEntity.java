package riskyken.plushieWrapper.common.entity;

import net.minecraft.entity.Entity;

public class PlushieEntity {
    
    protected Entity entity;
    
    public PlushieEntity(Entity entity) {
        this.entity = entity;
    }
    
    public boolean isSneaking() {
        return entity.isSneaking();
    }
    
    public Entity getEntity() {
        return entity;
    }
}
