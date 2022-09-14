package moe.plushie.armourers_workshop.api.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public interface IPlayerModelHolder<T extends EntityModel<?>> extends IHumanoidModelHolder<T> {

    default ModelPart getLeftSleevePart() {
        return getPart("left_sleeve");
    }

    default ModelPart getRightSleevePart() {
        return getPart("right_sleeve");
    }

    default ModelPart getLeftPantsPart() {
        return getPart("left_pants");
    }

    default ModelPart getRightPantsPart() {
        return getPart("right_pants");
    }

    default ModelPart getJacketPart() {
        return getPart("jacket");
    }
}
