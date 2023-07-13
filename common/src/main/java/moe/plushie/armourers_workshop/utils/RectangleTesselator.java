package moe.plushie.armourers_workshop.utils;

import com.apple.library.coregraphics.CGGraphicsState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class RectangleTesselator {

    private final CGGraphicsState state;

    private final PoseStack.Pose pose;

    private float uScale;
    private float vScale;
    private ResourceLocation texture;
    private VertexConsumer builder;

    public RectangleTesselator(CGGraphicsState state) {
        this.state = state;
        this.pose = state.ctm().last();
    }

    public void begin(RenderType renderType, ResourceLocation texture, float texWidth, float texHeight) {
        this.builder = state.buffers().getBuffer(renderType);
        this.texture = texture;
        this.uScale = 1f / texWidth;
        this.vScale = 1f / texHeight;
    }

    public void blit(float x, float y, float width, float height, float u, float v, float zLevel) {
        blit(x, y, width, height, u, v, width, height, zLevel);
    }

    public void blit(float x, float y, float width, float height, float u, float v, float texWidth, float texHeight, float zLevel) {
        builder.vertex(pose.pose(), x, y + height, zLevel).uv(u * uScale, (v + texHeight) * vScale).endVertex();
        builder.vertex(pose.pose(), x + width, y + height, zLevel).uv((u + texWidth) * uScale, (v + texHeight) * vScale).endVertex();
        builder.vertex(pose.pose(), x + width, y, zLevel).uv((u + texWidth) * uScale, v * vScale).endVertex();
        builder.vertex(pose.pose(), x, y, zLevel).uv(u * uScale, v * vScale).endVertex();
    }

    public void tile(float x, float y, float width, float height, float u, float v, float texWidth, float texHeight, float zLevel) {
        float x1 = x + width;
        float y1 = y + height;
        for (float y0 = y; y0 < y1; y0 += texHeight) {
            for (float x0 = x; x0 < x1; x0 += texWidth) {
                float w0 = Math.min(x1 - x0, texWidth);
                float h0 = Math.min(y1 - y0, texHeight);
                blit(x0, y0, w0, h0, u, v, w0, h0, zLevel);
            }
        }
    }

    public void end() {
        RenderSystem.setShaderTexture(0, texture);
        state.flush();
        builder = null;
    }
}
