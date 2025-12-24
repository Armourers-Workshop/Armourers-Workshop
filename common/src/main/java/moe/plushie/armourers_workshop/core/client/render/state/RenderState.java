package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;

public abstract class RenderState extends DataContainer {

    protected float partialTick = 0.0f;
    protected double animationTick = 0.0d;

    protected AnimationManager animationManager = AnimationManager.NONE;

    public void setPartialTick(float partialTick) {
        this.partialTick = partialTick;
    }

    public float partialTick() {
        return partialTick;
    }

    public void setAnimationTick(double animationTick) {
        this.animationTick = animationTick;
    }

    public double animationTick() {
        return animationTick;
    }

    public AnimationManager animationManager() {
        return animationManager;
    }
}
