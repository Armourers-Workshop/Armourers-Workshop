package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;

public class DefaultArmaturePluginContext implements ArmaturePlugin.Context {

    protected int overlay = 0x0a0000;
    protected int lightmap = 0xf000f0;
    protected float partialTick;
    protected double animationTick;

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

    public void setPartialTick(float partialTick) {
        this.partialTick = partialTick;
    }

    public void setAnimationTick(double animationTick) {
        this.animationTick = animationTick;
    }

    @Override
    public double animationTick() {
        return animationTick;
    }

    @Override
    public float partialTick() {
        return partialTick;
    }
}
