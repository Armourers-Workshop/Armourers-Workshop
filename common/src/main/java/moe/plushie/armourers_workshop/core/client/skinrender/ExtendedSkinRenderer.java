package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(value = EnvType.CLIENT)
public abstract class ExtendedSkinRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LivingSkinRenderer<T, M> {

    protected final IPartAccessor<M> accessor;

    public ExtendedSkinRenderer(EntityProfile profile) {
        super(profile);
        this.accessor = getAccessor();
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
            addModelOverride(accessor.getHead(model));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_CHEST)) {
            addModelOverride(accessor.getBody(model));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(accessor.getLeftArm(model));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(accessor.getRightArm(model));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_LEG)) {
            addModelOverride(accessor.getLeftLeg(model));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_LEG)) {
            addModelOverride(accessor.getRightLeg(model));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addModelOverride(accessor.getLeftLeg(model));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addModelOverride(accessor.getRightLeg(model));
        }
        // overlay
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(accessor.getHat(model));
        }
    }

    public abstract IPartAccessor<M> getAccessor();

    protected void setHatPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getHat(model));
    }

    protected void setHeadPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getHead(model));
    }

    protected void setBodyPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getBody(model));
    }

    protected void setLeftArmPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getLeftArm(model));
    }

    protected void setRightArmPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getRightArm(model));
    }

    protected void setLeftLegPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getLeftLeg(model));
    }

    protected void setRightLegPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getRightLeg(model));
    }

    protected void setLeftFootPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getLeftLeg(model));
    }

    protected void setRightFootPart(PoseStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getRightLeg(model));
    }

    protected void setSkirtPart(PoseStack matrixStack, M model) {
        ModelPart body = accessor.getBody(model);
        ModelPart leg = accessor.getRightLeg(model);
        matrixStack.translate(body.x, leg.y, leg.z);
        if (body.yRot != 0) {
            matrixStack.mulPose(Vector3f.YP.rotation(body.yRot));
        }
        // skirt does not wobble during normal walking.
        if (!model.riding) {
            return;
        }
        if (leg.xRot != 0) {
            matrixStack.mulPose(Vector3f.XP.rotation(leg.xRot));
        }
    }

    protected void setWings(PoseStack matrixStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        if (bakedPart.getProperties().get(SkinProperty.WINGS_MATCHING_POSE)) {
            transformer.apply(matrixStack, accessor.getBody(model));
        }
        matrixStack.translate(0, 0, 2);
    }

    public interface IPartAccessor<M extends Model> {

        ModelPart getHat(M model);

        ModelPart getHead(M model);

        ModelPart getBody(M model);

        ModelPart getLeftArm(M model);

        ModelPart getRightArm(M model);

        ModelPart getLeftLeg(M model);

        ModelPart getRightLeg(M model);
    }
}

