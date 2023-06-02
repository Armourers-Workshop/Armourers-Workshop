package moe.plushie.armourers_workshop.utils;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractShaderTesselator;
import net.minecraft.client.renderer.RenderType;

public class RectangleTesselator {

    final AbstractShaderTesselator tesselator;
    final PoseStack.Pose pose;

    float uScale;
    float vScale;
    BufferBuilder builder;

    public RectangleTesselator(PoseStack poseStack) {
        this.pose = poseStack.last();
        this.tesselator = new AbstractShaderTesselator();
    }

    public void begin(RenderType renderType, int texWidth, int texHeight) {
        this.builder = tesselator.begin(renderType);
        this.uScale = 1f / texWidth;
        this.vScale = 1f / texHeight;
    }

    public void add(float x, float y, float width, float height, float u, float v, float zLevel) {
        add(x, y, width, height, u, v, width, height, zLevel);
    }

    public void add(float x, float y, float width, float height, float u, float v, float texWidth, float texHeight, float zLevel) {
        builder.vertex(pose.pose(), x, y + height, zLevel).uv(u * uScale, (v + texHeight) * vScale).endVertex();
        builder.vertex(pose.pose(), x + width, y + height, zLevel).uv((u + texWidth) * uScale, (v + texHeight) * vScale).endVertex();
        builder.vertex(pose.pose(), x + width, y, zLevel).uv((u + texWidth) * uScale, v * vScale).endVertex();
        builder.vertex(pose.pose(), x, y, zLevel).uv(u * uScale, v * vScale).endVertex();
    }

    public void end() {
        tesselator.end();
        builder = null;
    }
}
