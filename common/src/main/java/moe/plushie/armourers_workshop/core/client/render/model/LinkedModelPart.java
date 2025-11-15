package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;

@OnlyIn(Dist.CLIENT)
public class LinkedModelPart implements IModelPart {

    private final String name;
    private final LinkedModelPartPose pose;
    private final IModelPart parent;
    private IModelPart target;

    public LinkedModelPart(String name, IModelPart parent) {
        this.name = name;
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
