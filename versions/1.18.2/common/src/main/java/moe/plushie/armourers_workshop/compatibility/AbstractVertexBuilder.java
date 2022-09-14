package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public abstract class AbstractVertexBuilder implements VertexConsumer {

    protected final VertexConsumer consumer;
    protected final ArrayList<Runnable> pending = new ArrayList<>();

    public AbstractVertexBuilder(VertexConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public VertexConsumer vertex(double d, double e, double f) {
        pending.add(() -> consumer.vertex(d, e, f));
        return this;
    }

    @Override
    public VertexConsumer color(int i, int j, int k, int l) {
        pending.add(() -> consumer.color(i, j, k, l));
        return this;
    }

    @Override
    public VertexConsumer uv(float f, float g) {
        pending.add(() -> consumer.uv(f, g));
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int i, int j) {
        pending.add(() -> consumer.overlayCoords(i, j));
        return this;
    }

    @Override
    public VertexConsumer uv2(int i, int j) {
        pending.add(() -> consumer.uv2(i, j));
        return this;
    }

    @Override
    public VertexConsumer normal(float f, float g, float h) {
        pending.add(() -> consumer.normal(f, g, h));
        return this;
    }

    @Override
    public void endVertex() {
        pending.add(consumer::endVertex);
    }

    @Override
    public void defaultColor(int i, int j, int k, int l) {
        pending.add(() -> consumer.defaultColor(i, j, k, l));
    }

    @Override
    public void unsetDefaultColor() {
        pending.add(consumer::unsetDefaultColor);
    }
}
