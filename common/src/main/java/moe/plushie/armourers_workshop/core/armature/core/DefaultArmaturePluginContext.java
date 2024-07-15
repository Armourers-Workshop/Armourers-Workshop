package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class DefaultArmaturePluginContext implements ArmaturePlugin.Context {

    protected int overlay = OverlayTexture.NO_OVERLAY;
    protected int lightmap = 0xf000f0;
    protected float partialTicks;
    protected float animationTicks;
    protected IPoseStack poseStack;

    protected EntityRenderData renderData;

    public void setOverlay(int overlay) {
        this.overlay = overlay;
    }

    @Override
    public int getOverlay() {
        return overlay;
    }

    public void setLightmap(int lightmap) {
        this.lightmap = lightmap;
    }

    @Override
    public int getLightmap() {
        return lightmap;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public void setAnimationTicks(float animationTicks) {
        this.animationTicks = animationTicks;
    }

    @Override
    public float getAnimationTicks() {
        return animationTicks;
    }

    @Override
    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPoseStack(IPoseStack poseStack) {
        this.poseStack = poseStack;
    }

    @Override
    public IPoseStack getPoseStack() {
        return poseStack;
    }


    public void setRenderData(EntityRenderData renderData) {
        this.renderData = renderData;
    }

    public EntityRenderData getRenderData() {
        return renderData;
    }
}
