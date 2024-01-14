package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;

public class EpicFlightModelPart implements IModelPart {

    private final IModelPart parent;
    private IModelPart child;

    public EpicFlightModelPart(IModelPart parent) {
        this.parent = parent;
    }

    public void linkTo(IModelPart child) {
        this.child = child;
    }

    @Override
    public boolean isVisible() {
        if (child != null) {
            return child.isVisible();
        }
        if (parent != null) {
            return parent.isVisible();
        }
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        if (child != null) {
            child.setVisible(visible);
        }
        if (parent != null) {
            parent.setVisible(visible);
        }
    }

    @Override
    public IModelPartPose pose() {
        if (child != null) {
            return child.pose();
        }
        if (parent != null) {
            return parent.pose();
        }
        return null;
    }
}
