package riskyken.plushieWrapper.common.entity;

import net.minecraft.entity.Entity;

public class EntityPointer {
    
    protected Entity entity;
    
    public EntityPointer(Entity entity) {
        this.entity = entity;
    }
    
    public boolean isSneaking() {
        return entity.isSneaking();
    }
    
    public Entity getEntity() {
        return entity;
    }
}
