package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModelProvider;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.auto;

public class ArrowModelArmaturePlugin extends ArmaturePlugin {

    public ArrowModelArmaturePlugin(ArmatureTransformerContext context) {
        context.addEntityRendererListener(entityRenderer -> {
            // get the model by entity renderer.
            auto modelProvider = (IModelProvider<?>) entityRenderer;
            context.setEntityModel(modelProvider.getModel(null));
        });
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        auto poseStack = context.pose();
        poseStack.rotate(Vector3f.XP.rotationDegrees(-45));
        poseStack.rotate(Vector3f.YP.rotationDegrees(-90));
        poseStack.translate(0, 0, -0.0625f); // 0, 0, -1
    }
}
