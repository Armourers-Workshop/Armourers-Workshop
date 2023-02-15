package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.VillagerModel;

public abstract class AbstractModelPartRegistries {

    public static void init() {

        ModelHolder.register(EntityModel.class, ModelHolder.EntityStub::new, (model, it) -> {
            // noop
        });

        ModelHolder.register(ListModel.class, (model, it) -> {
            it.unnamed(model.parts());
        });

        ModelHolder.register(HumanoidModel.class, ModelHolder.HumanoidStub::new, (model, it) -> {
            it.put("hat", model.hat);
            it.put("head", model.head);
            it.put("body", model.body);
            it.put("left_arm", model.leftArm);
            it.put("right_arm", model.rightArm);
            it.put("left_leg", model.leftLeg);
            it.put("right_leg", model.rightLeg);
        });
        ModelHolder.register(PlayerModel.class, ModelHolder.PlayerStub::new, (model, it) -> {
            it.put("left_sleeve", model.leftSleeve);
            it.put("right_sleeve", model.rightSleeve);
            it.put("left_pants", model.leftPants);
            it.put("right_pants", model.rightPants);
            it.put("jacket", model.jacket);
        });


        ModelHolder.register(VillagerModel.class, ModelHolder.HumanoidStub::new, (model, it) -> {
            it.put("hat", model.hat);
            it.put("hat_rim", model.hatRim);
            it.put("head", model.head);
            it.put("nose", model.nose);
            it.put("body", model.body);
            it.put("left_arm", model.arms);
            it.put("right_arm", model.arms);
            it.put("left_leg", model.leg1);
            it.put("right_leg", model.leg0);
            it.put("jacket", model.jacket);
        });

        ModelHolder.register(IllagerModel.class, ModelHolder.HumanoidStub::new, (model, it) -> {
            it.put("hat", model.hat);
            it.put("head", model.head);
            it.put("body", model.body);
            it.put("left_arm", model.leftArm);
            it.put("right_arm", model.rightArm);
            it.put("left_leg", model.leftLeg);
            it.put("right_leg", model.rightLeg);
            it.put("arms", model.arms);
        });

        ModelHolder.register(IronGolemModel.class, ModelHolder.HumanoidStub::new, (model, it) -> {
            it.put("head", model.head);
            it.put("body", model.body);
            it.put("left_arm", model.arm1);
            it.put("right_arm", model.arm0);
            it.put("left_leg", model.leg1);
            it.put("right_leg", model.leg0);
        });

        ModelHolder.register(ChickenModel.class, (model, it) -> {
            it.put("head", model.head);
//            it.put("body", model.body);
            it.put("beak", model.beak);
            it.put("red_thing", model.redThing);
//            it.put("left_leg", model.leg1);
//            it.put("right_leg", model.leg0);
//            it.put("left_wing", model.wing1);
//            it.put("right_wing", model.wing0);
        });

        ModelHolder.register(CreeperModel.class, (model, it) -> {
            it.put("head", model.head);
            it.put("hair", model.hair);
        });
    }

    public static ModelHolder.Transform transform(Model model) {
        AgeableListModel<?> model1 = ObjectUtils.safeCast(model, AgeableListModel.class);
        if (model1 != null) {
            float scale = model1.babyHeadScale;
            if (model1.scaleHead) {
                scale = 1.5f;
            }
            return new ModelHolder.Transform(scale, new Vector3f(0, model1.yHeadOffset, model1.zHeadOffset));
        }
        return new ModelHolder.Transform(1, Vector3f.ZERO);
    }
}
