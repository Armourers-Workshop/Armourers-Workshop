package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.capability.Wardrobe;
import moe.plushie.armourers_workshop.core.capability.WardrobeState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public abstract class ExtendedSkinRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LivingSkinRenderer<T, M> {

    protected final IPartAccessor<M> accessor;

    public ExtendedSkinRenderer(EntityProfile profile) {
        super(profile);
        this.accessor = getAccessor();
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_HAT, accessor::getHat);
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, this::setHeadPart);
        transformer.registerArmor(SkinPartTypes.BIPED_CHEST, accessor::getBody);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM, accessor::getLeftArm);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM, accessor::getRightArm);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_FOOT, accessor::getLeftLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_FOOT, accessor::getRightLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_LEG, accessor::getLeftLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_LEG, accessor::getRightLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_SKIRT, this::setSkirtPart);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_WING, this::setWings);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_WING, this::setWings);

        transformer.registerItem(ItemCameraTransforms.TransformType.NONE, Transformer::none);
        transformer.registerItem(ItemCameraTransforms.TransformType.GUI, Transformer::none);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIXED, Transformer::none);
        transformer.registerItem(ItemCameraTransforms.TransformType.GROUND, Transformer::none);
        transformer.registerItem(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, this::setLeftHandPart);
        transformer.registerItem(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, this::setRightHandPart);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, this::setLeftHandPart);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, this::setRightHandPart);
    }

    @Override
    public void willRender(T entity, M model, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        Wardrobe wardrobe = Wardrobe.of(entity);
        if (wardrobe == null) {
            return;
        }
        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        WardrobeState snapshot = wardrobe.snapshot();
        if (snapshot.isLimitLimbs()) {
            if (entity.animationSpeed > 0.25F) {
                entity.animationSpeed = 0.25F;
                entity.animationSpeedOld = 0.25F;
            }
        }
        if (AWConfig.enableModelOverridden) {
            applyOverriders(entity, model, wardrobe, snapshot);
        }
    }

    protected void applyOverriders(T entity, M model, Wardrobe wardrobe, WardrobeState snapshot) {
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            addOverrider(accessor.getLeftArm(model));
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addOverrider(accessor.getRightArm(model));
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
            addOverrider(accessor.getHead(model));
            addOverrider(accessor.getHat(model)); // when override the head, the hat needs to override too
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
            addOverrider(accessor.getBody(model));
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addOverrider(accessor.getLeftLeg(model));
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addOverrider(accessor.getRightLeg(model));
        }
    }

    @Override
    public void apply(T entity, M model, EquipmentSlotType slotType, float partialTicks, MatrixStack matrixStack) {
        Wardrobe wardrobe = Wardrobe.of(entity);
        if (wardrobe == null) {
            return;
        }
        WardrobeState snapshot = wardrobe.snapshot();
        Function<ISkinPartType, Boolean> shouldRenderEquipment = partType -> snapshot.hasOverriddenEquipmentPart(partType, wardrobe::shouldRenderEquipment);

        if (shouldRenderEquipment.apply(SkinPartTypes.BIPED_HEAD)) {
            setHidden(accessor.getHead(model));
            setHidden(accessor.getHat(model)); // when override the head, the hat needs to override too
        }
        if (shouldRenderEquipment.apply(SkinPartTypes.BIPED_CHEST)) {
            setHidden(accessor.getBody(model));
        }
        if (shouldRenderEquipment.apply(SkinPartTypes.BIPED_LEFT_ARM)) {
            setHidden(accessor.getLeftArm(model));
        }
        if (shouldRenderEquipment.apply(SkinPartTypes.BIPED_RIGHT_ARM)) {
            setHidden(accessor.getRightArm(model));
        }
        if (shouldRenderEquipment.apply(SkinPartTypes.BIPED_LEFT_FOOT) || shouldRenderEquipment.apply(SkinPartTypes.BIPED_LEFT_LEG)) {
            setHidden(accessor.getLeftLeg(model));
        }
        if (shouldRenderEquipment.apply(SkinPartTypes.BIPED_RIGHT_FOOT) || shouldRenderEquipment.apply(SkinPartTypes.BIPED_RIGHT_LEG)) {
            setHidden(accessor.getRightLeg(model));
        }
    }

    private void setHidden(ModelRenderer modelRenderer) {
        if (modelRenderer != null) {
            modelRenderer.visible = false;
        }
    }

    public abstract IPartAccessor<M> getAccessor();


    protected void setHeadPart(MatrixStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getHead(model));
    }

    protected void setSkirtPart(MatrixStack matrixStack, M model) {
        ModelRenderer body = accessor.getBody(model);
        ModelRenderer leg = accessor.getRightLeg(model);
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

    private void setWings(MatrixStack matrixStack, M model) {
        transformer.apply(matrixStack, accessor.getBody(model));
        matrixStack.translate(0, 0, 2);
    }

    private void setLeftHandPart(MatrixStack matrixStack, M model) {
        // vanilla: xRot=-90º yRot=180º x=-1/1 y=2 z=-10 xRot=180º
        matrixStack.translate(0, 2, 2);
        matrixStack.scale(-1, 1, 1);
    }

    private void setRightHandPart(MatrixStack matrixStack, M model) {
        // vanilla: xRot=-90º yRot=180º x=-1/1 y=2 z=-10 xRot=180º
        matrixStack.translate(0, 2, 2);
    }


    public interface IPartAccessor<M extends Model> {

        ModelRenderer getHat(M model);

        ModelRenderer getHead(M model);

        ModelRenderer getBody(M model);

        ModelRenderer getLeftArm(M model);

        ModelRenderer getRightArm(M model);

        ModelRenderer getLeftLeg(M model);

        ModelRenderer getRightLeg(M model);
    }
}

