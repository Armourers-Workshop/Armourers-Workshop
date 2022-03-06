package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeState;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class VillagerSkinRenderer<T extends VillagerEntity, M extends VillagerModel<T>> extends SkinRenderer<T, M> {

    public VillagerSkinRenderer(EntityType<T> entityType) {
        super(entityType);
        this.registerTransformers();
    }

    private void setSkirt(MatrixStack matrixStack, M model) {
        ModelRenderer body = model.body;
        ModelRenderer leg = model.leg1;
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

    private void setRightArms(MatrixStack matrixStack, M model) {
        matrixStack.translate(-5.0f, 2.0f, 0.0f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(10));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15));
    }

    private void setLeftArms(MatrixStack matrixStack, M model) {
        matrixStack.translate(5.0f, 2.0f, 0.0f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-10));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(-10));
    }

    private void setWings(MatrixStack matrixStack, M model) {
        transformer.apply(matrixStack, model.body);
        matrixStack.translate(0, 0, 2);
    }

    private void setNoHand(MatrixStack matrixStack, M model) {
    }

    private void setLeftHand(MatrixStack matrixStack, M model) {
        // vanilla: xRot=-90º yRot=180º x=-1/1 y=2 z=-10 xRot=180º
        matrixStack.translate(0, 2, 2);
        matrixStack.scale(-1, 1, 1);
    }

    private void setRightHand(MatrixStack matrixStack, M model) {
        // vanilla: xRot=-90º yRot=180º x=-1/1 y=2 z=-10 xRot=180º
        matrixStack.translate(0, 2, 2);
    }


    private void registerTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_HAT, m -> m.hat);
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, m -> m.head);
        transformer.registerArmor(SkinPartTypes.BIPED_CHEST, m -> m.body);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM, this::setLeftArms);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM, this::setRightArms);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_FOOT, m -> m.leg0);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_FOOT, m -> m.leg1);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_LEG, m -> m.leg0);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_LEG, m -> m.leg1);
        transformer.registerArmor(SkinPartTypes.BIPED_SKIRT, this::setSkirt);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_WING, this::setWings);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_WING, this::setWings);

//        register(ItemCameraTransforms.TransformType.NONE, this::setNoHand);
//        register(ItemCameraTransforms.TransformType.GUI, this::setNoHand);
//        register(ItemCameraTransforms.TransformType.FIXED, this::setNoHand);
//        register(ItemCameraTransforms.TransformType.GROUND, this::setNoHand);
//        register(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, this::setLeftHand);
//        register(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, this::setRightHand);
//        register(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, this::setLeftHand);
//        register(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, this::setRightHand);
    }

    @Override
    public void override(T entity, M model, SkinWardrobe wardrobe) {

        model.arms.visible = true;
        model.head.visible = true;
        model.hat.visible = true;
        model.hatRim.visible = true;
        model.nose.visible = true;
        model.body.visible = true;
        model.jacket.visible = true;
        model.leg1.visible = true;
        model.leg0.visible = true;

        SkinWardrobeState snapshot = wardrobe.snapshot();
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            model.arms.visible = false;
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            model.arms.visible = false;
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
            model.head.visible = false;
            model.hat.visible = false; // when override the head, the hat needs to override too
            model.hatRim.visible = false;
            model.nose.visible = false;
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
            model.body.visible = false;
            model.jacket.visible = false;
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
            model.leg0.visible = false;
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            model.leg1.visible = false;
        }
    }
}
