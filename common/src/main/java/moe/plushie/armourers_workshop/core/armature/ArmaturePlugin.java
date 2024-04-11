package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

public abstract class ArmaturePlugin {

    public void prepare(Entity entity, SkinRenderContext context) {
    }

    public void activate(Entity entity, SkinRenderContext context) {
    }

    public void deactivate(Entity entity, SkinRenderContext context) {
    }

    public boolean freeze() {
        return true;
    }
}
