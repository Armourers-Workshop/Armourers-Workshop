package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;

public class LinkedModelPart implements IModelPart {

    private final IModelPart parent;
    private IModelPart target;

    public LinkedModelPart(IModelPart parent) {
        this.parent = parent;
    }

    public void linkTo(IModelPart child) {
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
        if (target != null) {
            return target.pose();
        }
        if (parent != null) {
            return parent.pose();
        }
        return null;
    }
}
