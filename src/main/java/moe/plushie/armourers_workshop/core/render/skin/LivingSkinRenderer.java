package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.action.ICanHeld;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeState;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LivingSkinRenderer<T extends LivingEntity, M extends BipedModel<T>> extends SkinRenderer<T, M> {

    public LivingSkinRenderer(EntityType<T> entityType) {
        super(entityType);
        this.registerTransformers();
    }

    @Override
    public void override(T entity, M model, SkinWardrobe wardrobe) {
        model.setAllVisible(true);
        SkinWardrobeState snapshot = wardrobe.snapshot();
        PlayerModel<?> playerModel = null;
        if (model instanceof PlayerModel) {
            playerModel = (PlayerModel<?>) model;
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            model.leftArm.visible = false;
            if (playerModel != null) {
                playerModel.leftSleeve.visible = false;
            }
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            model.rightArm.visible = false;
            if (playerModel != null) {
                playerModel.rightSleeve.visible = false;
            }
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
            model.head.visible = false;
            model.hat.visible = false; // when override the head, the hat needs to override too
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
            model.body.visible = false;
            if (playerModel != null) {
                playerModel.jacket.visible = false;
            }
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
            model.leftLeg.visible = false;
            if (playerModel != null) {
                playerModel.leftPants.visible = false;
            }
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            model.rightLeg.visible = false;
            if (playerModel != null) {
                playerModel.rightPants.visible = false;
            }
        }
    }

    private void setHead(MatrixStack matrixStack, M model) {
        transformer.apply(matrixStack, model.head);
        if (model.young) {
            float scale = model.babyBodyScale;
            if (model.scaleHead) {
                scale = 1.5f;
            }
            matrixStack.scale(scale, scale, scale);
        }
    }

    private void setSkirt(MatrixStack matrixStack, M model) {
        ModelRenderer body = model.body;
        ModelRenderer leg = model.rightLeg;
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
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, this::setHead);
        transformer.registerArmor(SkinPartTypes.BIPED_CHEST, m -> m.body);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM, m -> m.leftArm);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM, m -> m.rightArm);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_FOOT, m -> m.leftLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_FOOT, m -> m.rightLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_LEG, m -> m.leftLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_LEG, m -> m.rightLeg);
        transformer.registerArmor(SkinPartTypes.BIPED_SKIRT, this::setSkirt);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_WING, this::setWings);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_WING, this::setWings);

        transformer.registerItem(ItemCameraTransforms.TransformType.NONE, this::setNoHand);
        transformer.registerItem(ItemCameraTransforms.TransformType.GUI, this::setNoHand);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIXED, this::setNoHand);
        transformer.registerItem(ItemCameraTransforms.TransformType.GROUND, this::setNoHand);
        transformer.registerItem(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, this::setLeftHand);
        transformer.registerItem(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, this::setRightHand);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, this::setLeftHand);
        transformer.registerItem(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, this::setRightHand);
    }
}

