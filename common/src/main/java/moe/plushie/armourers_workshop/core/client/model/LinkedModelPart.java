package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;

public class LinkedModelPart implements IModelPart {

    private final LinkedModelPartPose pose;
    private final IModelPart parent;
    private IModelPart target;

    public LinkedModelPart(IModelPart parent) {
        this.parent = parent;
        this.pose = new LinkedModelPartPose(parent);
    }

    public void linkTo(IModelPart child) {
        this.pose.linkTo(child);
        this.target = child;
    }

    @Override
    public boolean isVisible() {
        if (target != null) {
            return target.isVisible();
        }
        if (parent != null) {
            return parent.isVisible();
        }
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        if (target != null) {
            target.setVisible(visible);
        }
        if (parent != null) {
            parent.setVisible(visible);
        }
    }

    @Override
    public IModelPartPose pose() {
        return pose;
    }
}
