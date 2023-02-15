package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.IJoint;
import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.core.armature.Joints;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public abstract class ExtendedSkinRenderer<T extends LivingEntity, V extends EntityModel<T>, M extends IHumanoidModelHolder<V>> extends LivingSkinRenderer<T, V, M> {

    public ExtendedSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
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

        transformer.registerItem(ItemTransforms.TransformType.NONE, Transformer::withModel);
        transformer.registerItem(ItemTransforms.TransformType.GUI, Transformer::withModel);
        transformer.registerItem(ItemTransforms.TransformType.FIXED, Transformer::withModel);
        transformer.registerItem(ItemTransforms.TransformType.GROUND, Transformer::withModel);
        transformer.registerItem(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, Transformer::withModel);
        transformer.registerItem(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, Transformer::withModel);
        transformer.registerItem(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, Transformer::withModel);
        transformer.registerItem(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, Transformer::withModel);
    }

    @Override
    public void willRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRender(entity, model, renderData, context);
        renderData.getOverriddenManager().willRender(entity);

        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        if (renderData.isLimitLimbs()) {
            if (entity.animationSpeed > 0.25F) {
                entity.animationSpeed = 0.25F;
                entity.animationSpeedOld = 0.25F;
            }
        }
    }

    @Override
    public void didRender(T entity, M model, SkinRenderData renderData, SkinRenderContext renderContext) {
        super.didRender(entity, model, renderData, renderContext);
        renderData.getOverriddenManager().didRender(entity);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        // model
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_HEAD)) {
            addModelOverride(model.getHeadPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_CHEST)) {
            addModelOverride(model.getBodyPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_LEFT_ARM)) {
            addModelOverride(model.getLeftArmPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_RIGHT_ARM)) {
            addModelOverride(model.getRightArmPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_LEFT_LEG)) {
            addModelOverride(model.getLeftLegPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_RIGHT_LEG)) {
            addModelOverride(model.getRightLegPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_LEFT_FOOT)) {
            addModelOverride(model.getLeftLegPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_RIGHT_FOOT)) {
            addModelOverride(model.getRightLegPart());
        }
        // overlay
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_HEAD)) {
            addModelOverride(model.getHatPart());
        }
    }

    protected PartTransform<T, M> sel(IJoint joint1, IJoint joint2) {
        return (poseStack, entity, model, bakedPart, bakedSkin, context) -> {
            if (bakedPart.getProperties().get(SkinProperty.WINGS_MATCHING_POSE)) {
                transformer.apply(poseStack, joint2, context);
            } else {
                transformer.apply(poseStack, joint1, context);
            }
        };
    }
}

