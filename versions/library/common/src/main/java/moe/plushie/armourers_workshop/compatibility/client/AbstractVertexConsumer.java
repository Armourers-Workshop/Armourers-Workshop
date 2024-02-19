package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;

public class AbstractVertexConsumer implements IVertexConsumer {

    private VertexConsumer builder;

    public AbstractVertexConsumer(VertexConsumer builder) {
        this.builder = builder;
    }

    @Override
    public IVertexConsumer vertex(double d, double e, double f) {
        this.builder = builder.vertex(d, e, f);
        return this;
    }

    @Override
    public IVertexConsumer color(int i, int j, int k, int l) {
        this.builder = builder.color(i, j, k, l);
        return this;
    }

    @Override
    public IVertexConsumer uv(float f, float g) {
        this.builder = builder.uv(f, g);
        return this;
    }

    @Override
    public IVertexConsumer overlayCoords(int i, int j) {
        this.builder = builder.overlayCoords(i, j);
        return this;
    }

    @Override
    public IVertexConsumer uv2(int i, int j) {
        this.builder = builder.uv2(i, j);
        return this;
    }

    @Override
    public IVertexConsumer normal(float f, float g, float h) {
        this.builder = builder.normal(f, g, h);
        return this;
    }

    @Override
    public void endVertex() {
        builder.endVertex();
    }

    @Override
    public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
        builder.vertex(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
    }
}
