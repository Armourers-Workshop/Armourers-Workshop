package moe.plushie.armourers_workshop.core.client.skinrender.plugin;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.model.TransformModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.auto;

public class VillagerModelArmaturePlugin extends ArmaturePlugin {

    private final IModelPart sourcePart;
    private final IModelPart destinationPart;
    private final TransformModel<LivingEntity> transformModelRef = new TransformModel<>(0.0f);

    public VillagerModelArmaturePlugin(ArmatureTransformerContext context) {
        IModel model = context.getEntityModel();
        IModel transformModel = ModelHolder.of(transformModelRef);
        sourcePart = model.getPart("head");
        destinationPart = transformModel.getPart("head");
        context.setEntityModel(transformModel);
    }

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        LivingEntity livingEntity = ObjectUtils.safeCast(entity, LivingEntity.class);
        if (livingEntity == null) {
            return;
        }
        transformModelRef.transformFrom(livingEntity, context.getPartialTicks());
        if (sourcePart != null && destinationPart != null) {
            auto src = sourcePart.pose();
            auto dest = destinationPart.pose();
            dest.setPos(src.getXRot(), src.getYRot(), src.getZRot());
        }
    }
}
