package moe.plushie.armourers_workshop.api.client.model;

public interface IHumanoidModel extends IModel {

    default IModelPart getHatPart() {
        return getPart("hat");
    }

    default IModelPart getHeadPart() {
        return getPart("head");
    }

    default IModelPart getBodyPart() {
        return getPart("body");
    }

    default IModelPart getLeftArmPart() {
        return getPart("left_arm");
    }

    default IModelPart getRightArmPart() {
        return getPart("right_arm");
    }

    default IModelPart getLeftLegPart() {
        return getPart("left_leg");
    }

    default IModelPart getRightLegPart() {
        return getPart("right_leg");
    }
}
