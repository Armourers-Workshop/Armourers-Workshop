package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public void willRender(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        super.willRender(entity, model, renderData, light, partialRenderTick, matrixStack, buffers);
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
    public void didRender(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        super.didRender(entity, model, renderData, light, partialRenderTick, matrixStack, buffers);
        renderData.getOverriddenManager().didRender(entity);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        // model
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(accessor.getHead(model));
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_CHEST)) {
            addModelOverride(accessor.getBody(model));
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(accessor.getLeftArm(model));
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(accessor.getRightArm(model));
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_LEFT_LEG)) {
            addModelOverride(accessor.getLeftLeg(model));
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_RIGHT_LEG)) {
            addModelOverride(accessor.getRightLeg(model));
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addModelOverride(accessor.getLeftLeg(model));
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addModelOverride(accessor.getRightLeg(model));
        }
        // overlay
        if (overriddenManager.hasOverlay(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(accessor.getHat(model));
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

    private void setWings(MatrixStack matrixStack, T entity, M model, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        if (bakedPart.getProperties().get(SkinProperty.WINGS_MATCHING_POSE)) {
            transformer.apply(matrixStack, accessor.getBody(model));
        }
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

