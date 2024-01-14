package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

public class ArmaturePlugin {

    public void activate(Entity entity, SkinRenderContext context) {

    }

    public void deactivate(Entity entity, SkinRenderContext context) {

    }

    public IModel apply(IModel model) {
        return model;
    }

    public EntityRenderer<?> apply(EntityRenderer<?> entityRenderer) {
        return entityRenderer;
    }

    public boolean freeze() {
        return true;
    }
}
