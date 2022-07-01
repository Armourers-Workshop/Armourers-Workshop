package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.other.SkinModelManager;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModDebugger;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.extened.AWMatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nullable;

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

        transformer.registerItem(ItemCameraTransforms.TransformType.NONE, Transformer::withModel);
        transformer.registerItem(ItemCameraTransforms.TransformType.GUI, Transformer::withModel);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIXED, Transformer::withModel);
        transformer.registerItem(ItemCameraTransforms.TransformType.GROUND, Transformer::withModel);
        transformer.registerItem(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, Transformer::withModel);
        transformer.registerItem(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, Transformer::withModel);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, Transformer::withModel);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, Transformer::withModel);
    }

    @Override
    public void willRender(T entity, M model, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        // Limit the players limbs if they have a skirt equipped.
        // A proper lady should not swing her legs around!
        if (renderData.isLimitLimbs()) {
            if (entity.animationSpeed > 0.25F) {
                entity.animationSpeed = 0.25F;
                entity.animationSpeedOld = 0.25F;
            }
        }
        if (ModConfig.Client.enableModelOverridden) {
            applyOverriders(entity, model, renderData);
        }
    }

    protected void applyOverriders(T entity, M model, SkinRenderData renderData) {
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            addOverrider(accessor.getLeftArm(model));
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addOverrider(accessor.getRightArm(model));
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
            addOverrider(accessor.getHead(model));
            addOverrider(accessor.getHat(model)); // when override the head, the hat needs to override too
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
            addOverrider(accessor.getBody(model));
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addOverrider(accessor.getLeftLeg(model));
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addOverrider(accessor.getRightLeg(model));
        }
    }

    @Override
    public void apply(T entity, M model, EquipmentSlotType slotType, float partialTicks, MatrixStack matrixStack) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        switch (slotType) {
            case HEAD:
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_HEAD)) {
                    setHidden(accessor.getHead(model));
                    setHidden(accessor.getHat(model)); // when override the head, the hat needs to override too
                }
                break;
            case CHEST:
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_CHEST)) {
                    setHidden(accessor.getBody(model));
                }
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_LEFT_ARM)) {
                    setHidden(accessor.getLeftArm(model));
                }
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
                    setHidden(accessor.getRightArm(model));
                }
                break;
            case LEGS:
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_LEFT_LEG)) {
                    setHidden(accessor.getLeftLeg(model));
                    setHidden(accessor.getBody(model));
                }
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_RIGHT_LEG)) {
                    setHidden(accessor.getRightLeg(model));
                    setHidden(accessor.getBody(model));
                }
                break;
            case FEET:
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
                    setHidden(accessor.getLeftLeg(model));
                }
                if (renderData.hasOverriddenEquipmentPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
                    setHidden(accessor.getRightLeg(model));
                }
                break;
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

