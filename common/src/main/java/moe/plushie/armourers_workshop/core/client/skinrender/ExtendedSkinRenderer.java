package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.armature.Joints;
import moe.plushie.armourers_workshop.core.client.other.SkinModelTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinVisibilityTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public abstract class ExtendedSkinRenderer<T extends LivingEntity, M extends IHumanoidModel> extends LivingSkinRenderer<T, M> {

    public ExtendedSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void init(SkinModelTransformer<T, M> transformer) {
        transformer.registerArmor(SkinPartTypes.BIPPED_HAT, Joints.BIPPED_HEAD);
        transformer.registerArmor(SkinPartTypes.BIPPED_HEAD, Joints.BIPPED_HEAD);
        transformer.registerArmor(SkinPartTypes.BIPPED_CHEST, Joints.BIPPED_CHEST);
        transformer.registerArmor(SkinPartTypes.BIPPED_LEFT_ARM, Joints.BIPPED_LEFT_ARM);
        transformer.registerArmor(SkinPartTypes.BIPPED_RIGHT_ARM, Joints.BIPPED_RIGHT_ARM);
        transformer.registerArmor(SkinPartTypes.BIPPED_LEFT_FOOT, Joints.BIPPED_LEFT_FOOT);
        transformer.registerArmor(SkinPartTypes.BIPPED_RIGHT_FOOT, Joints.BIPPED_RIGHT_FOOT);
        transformer.registerArmor(SkinPartTypes.BIPPED_LEFT_LEG, Joints.BIPPED_LEFT_THIGH);
        transformer.registerArmor(SkinPartTypes.BIPPED_RIGHT_LEG, Joints.BIPPED_RIGHT_THIGH);
        transformer.registerArmor(SkinPartTypes.BIPPED_SKIRT, Joints.BIPPED_SKIRT);
        transformer.registerArmor(SkinPartTypes.BIPPED_RIGHT_WING, sel(Joints.BIPPED_LEFT_WING, Joints.BIPPED_LEFT_PHALANX));
        transformer.registerArmor(SkinPartTypes.BIPPED_LEFT_WING, sel(Joints.BIPPED_RIGHT_WING, Joints.BIPPED_RIGHT_PHALANX));

        transformer.registerArmor(SkinPartTypes.BIPPED_CHEST2, Joints.BIPPED_TORSO);
        transformer.registerArmor(SkinPartTypes.BIPPED_LEFT_ARM2, Joints.BIPPED_LEFT_HAND);
        transformer.registerArmor(SkinPartTypes.BIPPED_RIGHT_ARM2, Joints.BIPPED_RIGHT_HAND);
        transformer.registerArmor(SkinPartTypes.BIPPED_LEFT_LEG2, Joints.BIPPED_LEFT_LEG);
        transformer.registerArmor(SkinPartTypes.BIPPED_RIGHT_LEG2, Joints.BIPPED_RIGHT_LEG);

        transformer.registerItem(AbstractItemTransformType.NONE, SkinModelTransformer::fromModel);
        transformer.registerItem(AbstractItemTransformType.GUI, SkinModelTransformer::fromModel);
        transformer.registerItem(AbstractItemTransformType.FIXED, SkinModelTransformer::fromModel);
        transformer.registerItem(AbstractItemTransformType.GROUND, SkinModelTransformer::fromModel);
        transformer.registerItem(AbstractItemTransformType.THIRD_PERSON_LEFT_HAND, SkinModelTransformer::fromModel);
        transformer.registerItem(AbstractItemTransformType.THIRD_PERSON_RIGHT_HAND, SkinModelTransformer::fromModel);
        transformer.registerItem(AbstractItemTransformType.FIRST_PERSON_LEFT_HAND, SkinModelTransformer::fromModel);
        transformer.registerItem(AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND, SkinModelTransformer::fromModel);
    }

    @Override
    protected void init(SkinVisibilityTransformer<M> transformer) {
        SkinVisibilityTransformer.setupHumanoidModel(transformer);
    }

    @Override
    public void willRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRender(entity, model, renderData, context);
        renderData.getOverriddenManager().willRender(entity);

        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        if (renderData.isLimitLimbs()) {
            entity.applyLimitLimbs();
        }
    }

    @Override
    public void didRender(T entity, M model, SkinRenderData renderData, SkinRenderContext renderContext) {
        super.didRender(entity, model, renderData, renderContext);
        renderData.getOverriddenManager().didRender(entity);
    }

    protected SkinModelTransformer.Entry<T, M> sel(IJoint joint1, IJoint joint2) {
        return (poseStack, entity, model, bakedPart, bakedSkin, context) -> {
            if (bakedPart.getProperties().get(SkinProperty.WINGS_MATCHING_POSE)) {
                transformer.apply(poseStack, joint2, context);
            } else {
                transformer.apply(poseStack, joint1, context);
            }
        };
    }
}

