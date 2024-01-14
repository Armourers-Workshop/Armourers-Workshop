package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.model.TransformModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class VillagerModelArmaturePlugin extends ArmaturePlugin {

    private IModelPart sourcePart;
    private IModelPart destinationPart;

    private final TransformModel<LivingEntity> transformModelRef = new TransformModel<>(0.0f);
    private final IModel transformModel = ModelHolder.of(transformModelRef);

    @Override
    public void activate(Entity entity, SkinRenderContext context) {
        LivingEntity livingEntity = ObjectUtils.safeCast(entity, LivingEntity.class);
        if (livingEntity == null) {
            return;
        }
        transformModelRef.transformFrom(livingEntity, context.getPartialTicks());
        if (sourcePart != null && destinationPart != null) {
            destinationPart.pose().setRotations(sourcePart.pose());
        }
    }

    @Override
    public IModel apply(IModel model) {
        sourcePart = model.getPart("head");
        destinationPart = transformModel.getPart("head");
        return transformModel;
    }
}
