package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.core.utils.OpenModelPartPose;

@OnlyIn(Dist.CLIENT)
public class PlaceholderModelPart implements IModelPart {

    private boolean isVisible = true;
    private OpenModelPartPose pose;
    private final String name;

    public PlaceholderModelPart(String name) {
        this.name = name;
        this.pose = new OpenModelPartPose(0, 0, 0, 0, 0, 0);
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public OpenModelPartPose pose() {
        return pose;
    }
}
