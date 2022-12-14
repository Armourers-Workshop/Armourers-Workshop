package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.api.client.model.IOverrideModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

@Environment(value = EnvType.CLIENT)
public abstract class ExtendedSkinRenderer<T extends LivingEntity, V extends EntityModel<T>, M extends IHumanoidModelHolder<V>> extends LivingSkinRenderer<T, V, M> {

    public ExtendedSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_HAT, this::setHatPart);
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, this::setHeadPart);
        transformer.registerArmor(SkinPartTypes.BIPED_CHEST, this::setBodyPart);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM, this::setLeftArmPart);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM, this::setRightArmPart);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_FOOT, this::setLeftFootPart);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_FOOT, this::setRightFootPart);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_LEG, this::setLeftLegPart);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_LEG, this::setRightLegPart);
        transformer.registerArmor(SkinPartTypes.BIPED_SKIRT, this::setSkirtPart);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_WING, this::setWings);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_WING, this::setWings);

        transformer.registerArmor(SkinPartTypes.BIPED_CHEST2, this::setBodyPart2);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM2, this::setLeftArmPart2);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM2, this::setRightArmPart2);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_LEG2, this::setLeftLegPart2);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_LEG2, this::setRightLegPart2);

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
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(model.getHeadPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_CHEST)) {
            addModelOverride(model.getBodyPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(model.getLeftArmPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(model.getRightArmPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_LEG)) {
            addModelOverride(model.getLeftLegPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_LEG)) {
            addModelOverride(model.getRightLegPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addModelOverride(model.getLeftLegPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addModelOverride(model.getRightLegPart());
        }
        // overlay
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(model.getHatPart());
        }
    }

    protected boolean ov(IPoseStack poseStack, M model, String name) {
        IOverrideModelHolder mo = ObjectUtils.safeCast(model, IOverrideModelHolder.class);
        if (mo != null) {
            HashMap<String, IPoseStack> dq = mo.getOverrides();
            if (dq != null && dq.get(name) != null) {
                IPoseStack value = dq.get(name);
                poseStack.scale(16f, 16f, 16f);
                poseStack.multiply(value);
                poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
                ModDebugger.translate(poseStack);
                ModDebugger.scale(poseStack);
                ModDebugger.rotate(poseStack);
                poseStack.scale(-1, -1, 1);
                return true;
            }
        }
        return false;
    }

    protected void setHatPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Head")) {
            return;
        }
        transformer.apply(poseStack, model.getHatPart());
    }

    protected void setHeadPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Head")) {
            return;
        }
        transformer.apply(poseStack, model.getHeadPart());
    }

    protected void setBodyPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Chest")) {
            return;
        }
        transformer.apply(poseStack, model.getBodyPart());
    }

    protected void setLeftArmPart(IPoseStack poseStack, M model) {
         if (ov(poseStack, model, "Arm_L")) {
             return;
        }
        transformer.apply(poseStack, model.getLeftArmPart());
    }

    protected void setRightArmPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Arm_R")) {
            return;
        }
        transformer.apply(poseStack, model.getRightArmPart());
    }

    protected void setLeftLegPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Thigh_L")) {
            return;
        }
        transformer.apply(poseStack, model.getLeftLegPart());
    }

    protected void setRightLegPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Thigh_R")) {
            return;
        }
        transformer.apply(poseStack, model.getRightLegPart());
    }

    protected void setLeftFootPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Foot_L")) {
            return;
        }
        transformer.apply(poseStack, model.getLeftLegPart());
    }

    protected void setRightFootPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Foot_R")) {
            return;
        }
        transformer.apply(poseStack, model.getRightLegPart());
    }

    protected void setSkirtPart(IPoseStack poseStack, M model) {
        if (ov(poseStack, model, "Skirt")) {
            return;
        }
        ModelPart body = model.getBodyPart();
        ModelPart leg = model.getRightLegPart();
        poseStack.translate(body.x, leg.y, leg.z);
        if (body.yRot != 0) {
            poseStack.rotate(Vector3f.YP.rotation(body.yRot));
        }
        // skirt does not wobble during normal walking.
        if (!model.isRiding()) {
            return;
        }
        if (leg.xRot != 0) {
            poseStack.rotate(Vector3f.XP.rotation(leg.xRot));
        }
    }

    protected void setWings(IPoseStack poseStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        if (bakedPart.getProperties().get(SkinProperty.WINGS_MATCHING_POSE)) {
            transformer.apply(poseStack, model.getBodyPart());
        }
        poseStack.translate(0, 0, 2);
    }

    protected void setBodyPart2(IPoseStack poseStack, M model) {
        ov(poseStack, model, "Torso");
    }

    protected void setLeftArmPart2(IPoseStack poseStack, M model) {
        ov(poseStack, model, "Hand_L");
    }

    protected void setRightArmPart2(IPoseStack poseStack, M model) {
        ov(poseStack, model, "Hand_R");
    }

    protected void setLeftLegPart2(IPoseStack poseStack, M model) {
        ov(poseStack, model, "Leg_L");
    }

    protected void setRightLegPart2(IPoseStack poseStack, M model) {
        ov(poseStack, model, "Leg_R");
    }
}

