package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractSkinnableModels;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.stream.Collectors;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractModelPartRegistries {

    public static void init() {

        ModelHolder.register(EntityModel.class, (model, it) -> {
            // noop
        });

        ModelHolder.register(HierarchicalModel.class, (model, it) -> {
            it.unnamed(model.root().getAllParts().collect(Collectors.toList()));
        });

        ModelHolder.register(AbstractSkinnableModels.HUMANOID, (model, it) -> {
            it.put("hat", model.hat);
            it.put("head", model.head);
            it.put("body", model.body);
            it.put("left_arm", model.leftArm);
            it.put("right_arm", model.rightArm);
            it.put("left_leg", model.leftLeg);
            it.put("right_leg", model.rightLeg);
        });
        ModelHolder.register(AbstractSkinnableModels.PLAYER, (model, it) -> {
            it.put("left_sleeve", model.leftSleeve);
            it.put("right_sleeve", model.rightSleeve);
            it.put("left_pants", model.leftPants);
            it.put("right_pants", model.rightPants);
            it.put("jacket", model.jacket);
            it.put("cloak", model.cloak);
            it.put("ear", model.ear);
        });

        ModelHolder.register(AbstractSkinnableModels.CREEPER, (model, it) -> {
            ModelPart root = model.root();
            it.put("head", root.getSafeChild("head"));
            it.put("hair", root.getSafeChild("head"));
        });

        ModelHolder.register(AbstractSkinnableModels.CHICKEN, (model, it) -> {
            it.put("head", model.head);
            //it.put("body", model.body);
            it.put("beak", model.beak);
            it.put("red_thing", model.redThing);
            //it.put("left_leg", model.leftLeg);
            //it.put("right_leg", model.rightLeg);
            //it.put("left_wing", model.leftWing);
            //it.put("right_wing", model.rightWing);
        });

        ModelHolder.register(AbstractSkinnableModels.HORSE, (model, it) -> {
            it.put("head", model.headParts);
            it.put("body", model.body);
            it.put("left_hind_leg", model.leftHindLeg);
            it.put("right_hind_leg", model.rightHindLeg);
            it.put("left_front_leg", model.leftFrontLeg);
            it.put("right_front_leg", model.rightFrontLeg);
            it.put("left_front_baby_leg", model.leftFrontBabyLeg);
            it.put("right_front_baby_leg", model.rightFrontBabyLeg);
            it.put("left_hind_baby_leg", model.leftHindBabyLeg);
            it.put("right_hind_baby_leg", model.rightHindBabyLeg);
            it.put("tail", model.tail);
        });


        ModelHolder.register(AbstractSkinnableModels.VILLAGER, (model, it) -> {
            ModelPart root = model.root();
            ModelPart head = root.getSafeChild("head");
            ModelPart hat = head.getSafeChild("hat");
            ModelPart body = root.getSafeChild("body");
            it.put("hat", hat);
            it.put("hat_rim", hat.getSafeChild("hat_rim"));
            it.put("head", head);
            it.put("nose", head.getSafeChild("nose"));
            it.put("body", body);
            it.put("left_arm", root.getSafeChild("arms"));
            it.put("right_arm", root.getSafeChild("arms"));
            it.put("left_leg", root.getSafeChild("left_leg"));
            it.put("right_leg", root.getSafeChild("right_leg"));
            it.put("jacket", body.getSafeChild("jacket"));
        });

        ModelHolder.register(AbstractSkinnableModels.ILLAGER, (model, it) -> {
            ModelPart root = model.root();
            ModelPart head = root.getSafeChild("head");
            ModelPart hat = head.getSafeChild("hat");
            it.put("hat", hat);
            it.put("head", head);
            it.put("body", root.getSafeChild("body"));
            it.put("arms", root.getSafeChild("arms"));
            it.put("left_arm", root.getSafeChild("left_arm"));
            it.put("right_arm", root.getSafeChild("right_arm"));
            it.put("left_leg", root.getSafeChild("left_leg"));
            it.put("right_leg", root.getSafeChild("right_leg"));
        });

        ModelHolder.register(AbstractSkinnableModels.IRON_GOLEM, (model, it) -> {
            ModelPart root = model.root();
            it.put("hat", root.getSafeChild("head"));
            it.put("head", root.getSafeChild("head"));
            it.put("body", root.getSafeChild("body"));
            it.put("left_arm", root.getSafeChild("left_arm"));
            it.put("right_arm", root.getSafeChild("right_arm"));
            it.put("left_leg", root.getSafeChild("left_leg"));
            it.put("right_leg", root.getSafeChild("right_leg"));
        });

        ModelHolder.register(AbstractSkinnableModels.BOAT, (model, it) -> {
            List<ModelPart> parts = model.parts();
            it.put("bottom", parts.get(0));
            it.put("back", parts.get(1));
            it.put("front", parts.get(2));
            it.put("right", parts.get(3));
            it.put("left", parts.get(4));
            it.put("left_paddle", parts.get(5));
            it.put("right_paddle", parts.get(6));
        });
//        ModelHolder.register(AbstractSkinnableModels.RAFT, (model, it) -> {
//            List<ModelPart> parts = model.parts();
//            it.put("bottom", parts.get(0));
//            it.put("left_paddle", parts.get(1));
//            it.put("right_paddle", parts.get(2));
//        });

        ModelHolder.registerOptional(AbstractSkinnableModels.ALLAY, (model, it) -> {
            ModelPart root = model.root();
            ModelPart body = root.getSafeChild("body");
            it.put("root", root);
            it.put("hat", root.getSafeChild("head"));
            it.put("head", root.getSafeChild("head"));
            it.put("body", root.getSafeChild("body"));
            it.put("left_arm", body.getSafeChild("left_arm"));
            it.put("right_arm", body.getSafeChild("right_arm"));
            it.put("left_leg", body.getSafeChild("left_wing"));
            it.put("right_leg", body.getSafeChild("right_wing"));
            it.put("left_wing", body.getSafeChild("left_wing"));
            it.put("right_wing", body.getSafeChild("right_wing"));
        });
    }

}
