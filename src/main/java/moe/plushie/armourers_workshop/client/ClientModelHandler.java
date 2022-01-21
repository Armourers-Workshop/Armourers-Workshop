package moe.plushie.armourers_workshop.client;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.render.model.HeldItemModel;
import moe.plushie.armourers_workshop.core.render.model.ModelTransformer;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.part.unknown.UnknownPartType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;

public class ClientModelHandler extends ModelTransformer {

    public static void init() {
        registerBipedModel();
        registerItemModel();
        registerPlayerModel();
    }

    private static void registerItemModel() {
        // vanilla: xRot=-90º yRot=180º x=-1/1 y=2 z=-10 xRot=180º
        OffsetModelRenderer thirdPersonLeftItem = new OffsetModelRenderer();
        thirdPersonLeftItem.y = 2;
        thirdPersonLeftItem.z = 2;
        thirdPersonLeftItem.scale = new Vector3f(-1, 1, 1);
        ModelRenderer thirdPersonRightItem = new ModelRenderer(0, 0, 0, 0);
        thirdPersonRightItem.y = 2;
        thirdPersonRightItem.z = 2;
        registerItem(HeldItemModel.class, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, m -> thirdPersonLeftItem);
        registerItem(HeldItemModel.class, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, m -> thirdPersonRightItem);

        // vanilla:
        ModelRenderer firstPersonLeftItem = new ModelRenderer(0, 0, 0, 0);
        firstPersonLeftItem.z = 1;
        firstPersonLeftItem.yRot = (float) Math.PI / -2;
        ModelRenderer firstPersonRightItem = new ModelRenderer(0, 0, 0, 0);
        firstPersonRightItem.z = 1;
        registerItem(HeldItemModel.class, ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, m -> firstPersonLeftItem);
        registerItem(HeldItemModel.class, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, m -> firstPersonRightItem);
    }

    private static void registerBipedModel() {
        // simple
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_HAT, m -> m.hat);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_HEAD, m -> m.head);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_CHEST, m -> m.body);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_LEFT_ARM, m -> m.leftArm);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_RIGHT_ARM, m -> m.rightArm);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_LEFT_FOOT, m -> m.leftLeg);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_RIGHT_FOOT, m -> m.rightLeg);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_LEFT_LEG, m -> m.leftLeg);
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_RIGHT_LEG, m -> m.rightLeg);

        // skirt does not wobble during normal walking.
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_SKIRT, m -> {
            ModelRenderer skirt = m.rightLeg.createShallowCopy();
            skirt.x = m.body.x;
            skirt.xRot = m.riding ? skirt.xRot : 0;
            skirt.yRot = m.body.yRot;
            return skirt;
        });

        // wings
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_RIGHT_WING, m -> {
            OffsetModelRenderer wing = new OffsetModelRenderer(m.body);
            wing.z = 2;
            return wing;
        });
        registerArmor(BipedModel.class, SkinPartTypes.BIPED_LEFT_WING, m -> {
            OffsetModelRenderer wing = new OffsetModelRenderer(m.body);
            wing.z = 2;
            wing.mirror = true;
            return wing;
        });

        //
        Vector3f flip = new Vector3f(-1, 1, 1);
        registerItem(BipedModel.class, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, m -> {
            OffsetModelRenderer item = new OffsetModelRenderer(m.leftArm);
            item.x = 1;
            item.y = 8;
            item.xRot = (float) Math.PI / 2;
            item.scale = flip;
            return item;
        });
        registerItem(BipedModel.class, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, m -> {
            OffsetModelRenderer item = new OffsetModelRenderer(m.rightArm);
            item.x = -1;
            item.y = 8;
            item.xRot = (float) Math.PI / 2;
            return item;
        });
    }

    private static void registerPlayerModel() {
        // simple
//        registerArmor(PlayerModel.class, SkinPartTypes.PLAYER_JACKET, m -> m.jacket);
//        registerArmor(PlayerModel.class, SkinPartTypes.PLAYER_LEFT_PANTS, m -> m.leftPants);
//        registerArmor(PlayerModel.class, SkinPartTypes.PLAYER_RIGHT_PANTS, m -> m.rightPants);
//        registerArmor(PlayerModel.class, SkinPartTypes.PLAYER_LEFT_SLEEVE, m -> m.leftSleeve);
//        registerArmor(PlayerModel.class, SkinPartTypes.PLAYER_RIGHT_SLEEVE, m -> m.rightSleeve);
    }
}