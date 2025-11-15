package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;

public abstract class RenderState extends DataContainer {

    protected float partialTicks = 0.0f;
    protected double animationTicks = 0.0d;

    protected AnimationManager animationManager = AnimationManager.NONE;

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float partialTicks() {
        return partialTicks;
    }

    public void setAnimationTicks(double animationTicks) {
        this.animationTicks = animationTicks;
    }

    public double animationTicks() {
        return animationTicks;
    }

    public AnimationManager animationManager() {
        return animationManager;
    }
}
