package moe.plushie.armourers_workshop.api.client.model;

public interface IPlayerModel extends IHumanoidModel {

    default IModelPart getEarPart() {
        return getPart("ear");
    }

    default IModelPart getCloakPart() {
        return getPart("cloak");
    }

    default IModelPart getJacketPart() {
        return getPart("jacket");
    }

    default IModelPart getLeftSleevePart() {
        return getPart("left_sleeve");
    }

    default IModelPart getRightSleevePart() {
        return getPart("right_sleeve");
    }

    default IModelPart getLeftPantsPart() {
        return getPart("left_pants");
    }

    default IModelPart getRightPantsPart() {
        return getPart("right_pants");
    }
}
