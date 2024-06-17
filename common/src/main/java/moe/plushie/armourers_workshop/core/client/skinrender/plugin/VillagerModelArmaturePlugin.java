package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.model.TransformModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class VillagerModelArmaturePlugin extends ArmaturePlugin {

    private final IModelPart sourcePart;
    private final IModelPart destinationPart;
    private final TransformModel<LivingEntity> transformModelRef = new TransformModel<>(0.0f);

    public VillagerModelArmaturePlugin(ArmatureTransformerContext context) {
        var model = context.getEntityModel();
        var transformModel = ModelHolder.of(transformModelRef);
        sourcePart = model.getPart("head");
        destinationPart = transformModel.getPart("head");
        context.setEntityModel0(transformModel);
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        transformModelRef.transformFrom(livingEntity, context.getPartialTicks());
        if (sourcePart != null && destinationPart != null) {
            var src = sourcePart.pose();
            var dest = destinationPart.pose();
            dest.setRotation(src.getXRot(), src.getYRot(), src.getZRot());
        }
    }
}
