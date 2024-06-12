package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;

@Available("[1.21, )")
public class AbstractVertexConsumer implements IVertexConsumer {

    protected VertexConsumer parent;

    protected AbstractVertexConsumer(VertexConsumer parent) {
        this.parent = parent;
    }

    public static AbstractVertexConsumer of(VertexConsumer parent) {
        return new AbstractVertexConsumer(parent);
    }

    @Override
    public IVertexConsumer vertex(float x, float y, float z) {
        this.parent = parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public IVertexConsumer color(int i, int j, int k, int l) {
        this.parent = parent.setColor(i, j, k, l);
        return this;
    }

    @Override
    public IVertexConsumer uv(float f, float g) {
        this.parent = parent.setUv(f, g);
        return this;
    }

    @Override
    public IVertexConsumer overlayCoords(int i, int j) {
        this.parent = parent.setUv1(i, j);
        return this;
    }

    @Override
    public IVertexConsumer uv2(int i, int j) {
        this.parent = parent.setUv2(i, j);
        return this;
    }

    @Override
    public IVertexConsumer normal(float f, float g, float h) {
        this.parent = parent.setNormal(f, g, h);
        return this;
    }

    @Override
    public void endVertex() {
        //builder.endVertex();
    }

    @Override
    public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
        parent.addVertex(x, y, z, color, u, v, overlay, light, nx, ny, nz);
    }
}
