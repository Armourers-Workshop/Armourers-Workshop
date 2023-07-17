package moe.plushie.armourers_workshop.api.client.model;

public interface IModelPart {

    boolean isVisible();

    void setVisible(boolean visible);

    IModelPartPose pose();
}
