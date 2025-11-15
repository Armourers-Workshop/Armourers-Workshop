package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;

public class DefaultArmaturePluginContext implements ArmaturePlugin.Context {

    protected int overlay = 0x0a0000;
    protected int lightmap = 0xf000f0;
    protected float partialTicks;
    protected double animationTicks;

    public void setOverlay(int overlay) {
        this.overlay = overlay;
    }

    @Override
    public int overlay() {
        return overlay;
    }

    public void setLightmap(int lightmap) {
        this.lightmap = lightmap;
    }

    @Override
    public int lightmap() {
        return lightmap;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public void setAnimationTicks(double animationTicks) {
        this.animationTicks = animationTicks;
    }

    @Override
    public double animationTicks() {
        return animationTicks;
    }

    @Override
    public float partialTicks() {
        return partialTicks;
    }
}
