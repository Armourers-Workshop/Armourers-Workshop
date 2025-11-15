package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

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
        var red = (color >> 16) & 0xff;
        var green = (color >> 8) & 0xff;
        var blue = color & 0xff;
        var alpha = (color >> 24) & 0xff;
        return color(red, green, blue, alpha);
    }

    default IVertexConsumer uv2(int i) {
        return this.uv2(i & 0xffff, i >> 16 & 0xffff);
    }

    default IVertexConsumer overlayCoords(int i) {
        return this.overlayCoords(i & 0xffff, i >> 16 & 0xffff);
    }

    default IVertexConsumer vertex(IPoseStack.Pose pose, float x, float y, float z) {
        float[] values = {x, y, z, 1};
        pose.transformPose(values);
        return vertex(values[0], values[1], values[2]);
    }

    default IVertexConsumer normal(IPoseStack.Pose pose, float x, float y, float z) {
        float[] values = {x, y, z};
        pose.transformNormal(values);
        return normal(values[0], values[1], values[2]);
    }
}
