package moe.plushie.armourers_workshop.api.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public interface IHumanoidModelHolder<T extends EntityModel<?>> extends IModelHolder<T> {

    default ModelPart getHatPart() {
        return getPart("hat");
    }

    default ModelPart getHeadPart() {
        return getPart("head");
    }

    default ModelPart getBodyPart() {
        return getPart("body");
    }

    default ModelPart getLeftArmPart() {
        return getPart("left_arm");
    }

    default ModelPart getRightArmPart() {
        return getPart("right_arm");
    }

    default ModelPart getLeftLegPart() {
        return getPart("left_leg");
    }

    default ModelPart getRightLegPart() {
        return getPart("right_leg");
    }

}
