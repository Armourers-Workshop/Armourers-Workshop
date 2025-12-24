package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public abstract class ArmaturePlugin {

    public void prepare(EntityRenderState renderState, Entity entity, float partialTick) {
    }

    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
    }

    public void deactivate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
    }

    public boolean freeze() {
        return true;
    }

    public interface Context {

        int overlay();

        int lightmap();

        float partialTick();

        double animationTick();
    }
}
