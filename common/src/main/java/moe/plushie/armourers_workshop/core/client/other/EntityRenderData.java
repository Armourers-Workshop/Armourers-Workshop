package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EntityRenderPatch;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.EpicFightEntityRendererPatch;
import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EntityRenderData extends EntitySlotsHandler<Entity> {

    private EntityRenderPatch<? super Entity> renderPatch;

    public EntityRenderData(Entity entity) {
        super(new EntityProvider(), new WardrobeProvider());
    }

    @Nullable
    public static EntityRenderData of(@Nullable Entity entity) {
        if (entity != null) {
            return EntityDataStorage.of(entity).getRenderData().orElse(null);
        }
        return null;
    }

    public void tick(Entity entity) {
        tick(entity, SkinWardrobe.of(entity));
    }

    @Override
    public boolean isLimitLimbs() {
        // in EF doesn't need to limit limbs.
        if (renderPatch instanceof EpicFightEntityRendererPatch) {
            return false;
        }
        return super.isLimitLimbs();
    }

    public void setRenderPatch(EntityRenderPatch<? super Entity> renderPatch) {
        this.renderPatch = renderPatch;
    }

    public EntityRenderPatch<? super Entity> getRenderPatch() {
        return renderPatch;
    }
}

