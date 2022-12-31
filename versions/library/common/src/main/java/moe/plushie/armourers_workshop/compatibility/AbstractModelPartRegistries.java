package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;

import java.util.stream.Collectors;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractModelPartRegistries {

    public static void init() {

        ModelHolder.register(EntityModel.class, ModelHolder.EntityStub::new, (model, it) -> {
            // noop
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

        ModelHolder.register(HierarchicalModel.class, (model, it) -> {
            it.unnamed(model.root().getAllParts().collect(Collectors.toList()));
        });

        ModelHolder.register(CreeperModel.class, (model, it) -> {
            ModelPart root = model.root();
            it.put("head", getChild(root, "head"));
            it.put("hair", getChild(root, "head"));
        });

        ModelHolder.register(ChickenModel.class, (model, it) -> {
            it.put("head", model.head);
            //it.put("body", model.body);
            it.put("beak", model.beak);
            it.put("red_thing", model.redThing);
            //it.put("left_leg", model.leftLeg);
            //it.put("right_leg", model.rightLeg);
            //it.put("left_wing", model.leftWing);
            //it.put("right_wing", model.rightWing);
        });

        ModelHolder.register(VillagerModel.class, ModelHolder.HumanoidStub::new, (model, it) -> {
            ModelPart root = model.root();
            ModelPart head = getChild(root, "head");
            ModelPart hat = getChild(head, "hat");
            ModelPart body = getChild(root, "body");
            it.put("hat", hat);
            it.put("hat_rim", getChild(hat, "hat_rim"));
            it.put("head", head);
            it.put("nose", getChild(head, "nose"));
            it.put("body", body);
            it.put("left_arm", getChild(root, "arms"));
            it.put("right_arm", getChild(root, "arms"));
            it.put("left_leg", getChild(root, "left_leg"));
            it.put("right_leg", getChild(root, "right_leg"));
            it.put("jacket", getChild(body, "jacket"));
        });

        ModelHolder.register(IllagerModel.class, ModelHolder.HumanoidStub::new, (model, it) -> {
            ModelPart root = model.root();
            ModelPart head = getChild(root, "head");
            ModelPart hat = getChild(head, "hat");
            it.put("hat", hat);
            it.put("head", head);
            it.put("body", getChild(root, "body"));
            it.put("arms", getChild(root, "arms"));
            it.put("left_arm", getChild(root, "left_arm"));
            it.put("right_arm", getChild(root, "right_arm"));
            it.put("left_leg", getChild(root, "left_leg"));
            it.put("right_leg", getChild(root, "right_leg"));
        });

        ModelHolder.register(IronGolemModel.class, ModelHolder.HumanoidStub::new, (model, it) -> {
            ModelPart root = model.root();
            it.put("hat", getChild(root, "head"));
            it.put("head", getChild(root, "head"));
            it.put("body", getChild(root, "body"));
            it.put("left_arm", getChild(root, "left_arm"));
            it.put("right_arm", getChild(root, "right_arm"));
            it.put("left_leg", getChild(root, "left_leg"));
            it.put("right_leg", getChild(root, "right_leg"));
        });
    }

    public static ModelHolder.Transform transform(Model model) {
        AgeableListModel<?> model1 = ObjectUtils.safeCast(model, AgeableListModel.class);
        if (model1 != null) {
            float scale = model1.babyHeadScale;
            if (model1.scaleHead) {
                scale = 1.5f;
            }
            return new ModelHolder.Transform(scale, new Vector3f(0, model1.babyYHeadOffset, model1.babyZHeadOffset));
        }
        return new ModelHolder.Transform(1, Vector3f.ZERO);
    }

    public static ModelPart getChild(ModelPart part, String name) {
        try {
            if (part == null) {
                return null;
            }
            return part.getChild(name);
        } catch (Exception e) {
            return null;
        }
    }
}
