package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.ColorUtils;

public interface IVertexConsumer {

    IVertexConsumer vertex(float x, float y, float z);

    IVertexConsumer color(int r, int g, int b, int a);

    IVertexConsumer uv(float u, float v);

    IVertexConsumer overlayCoords(int u, int v);

    IVertexConsumer uv2(int u, int v);

    IVertexConsumer normal(float x, float y, float z);

    void endVertex();

    default void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
        this.vertex(x, y, z);
        this.color(color);
        this.uv(u, v);
        this.overlayCoords(overlay);
        this.uv2(light);
        this.normal(nx, ny, nz);
        this.endVertex();
    }

    default IVertexConsumer color(float r, float g, float b, float a) {
        return color((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f), (int) (a * 255.0f));
    }

    default IVertexConsumer color(int color) {
        return color(ColorUtils.ARGB32.red(color), ColorUtils.ARGB32.green(color), ColorUtils.ARGB32.blue(color), ColorUtils.ARGB32.alpha(color));
    }

    default IVertexConsumer uv2(int i) {
        return this.uv2(i & 0xffff, i >> 16 & 0xffff);
    }

    default IVertexConsumer overlayCoords(int i) {
        return this.overlayCoords(i & 0xffff, i >> 16 & 0xffff);
    }

    default IVertexConsumer vertex(IPoseStack.Pose entry, float x, float y, float z) {
        float[] values = {x, y, z, 1};
        entry.transformPose(values);
        return vertex(values[0], values[1], values[2]);
    }

    default IVertexConsumer normal(IPoseStack.Pose entry, float x, float y, float z) {
        float[] values = {x, y, z};
        entry.transformNormal(values);
        return normal(values[0], values[1], values[2]);
    }
}
