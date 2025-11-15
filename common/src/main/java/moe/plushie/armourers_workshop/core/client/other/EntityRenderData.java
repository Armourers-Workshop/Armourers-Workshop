package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.entity.Entity;

public class EntityRenderData extends EntitySlotsHandler<Entity> {

    private static final DataContainer.Key<EntityRenderData> KEY = DataContainer.key("RenderData", EntityRenderData::new);

    public EntityRenderData(Entity entity) {
        super(entity, new EntityProvider(), new WardrobeProvider());
    }

    public static EntityRenderData of(Entity entity) {
        if (entity != null) {
            return DataContainer.of(entity, KEY);
        }
        return null;
    }

    public void tick(Entity entity) {
        tick(entity, SkinWardrobe.of(entity));
    }
}

