package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import net.minecraft.util.FastColor;

public interface IVertexConsumer {

    IVertexConsumer vertex(double d, double e, double f);

    IVertexConsumer color(int i, int j, int k, int l);

    IVertexConsumer uv(float f, float g);

    IVertexConsumer overlayCoords(int i, int j);

    IVertexConsumer uv2(int i, int j);

    IVertexConsumer normal(float f, float g, float h);

    void endVertex();

    default void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
        this.vertex(f, g, h);
        this.color(i, j, k, l);
        this.uv(m, n);
        this.overlayCoords(o);
        this.uv2(p);
        this.normal(q, r, s);
        this.endVertex();
    }

    default IVertexConsumer color(float r, float g, float b, float a) {
        return this.color((int) (r * 255.0f), (int) (g * 255.0f), (int) (b * 255.0f), (int) (a * 255.0f));
    }

    default IVertexConsumer color(int i) {
        return this.color(FastColor.ARGB32.red(i), FastColor.ARGB32.green(i), FastColor.ARGB32.blue(i), FastColor.ARGB32.alpha(i));
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
