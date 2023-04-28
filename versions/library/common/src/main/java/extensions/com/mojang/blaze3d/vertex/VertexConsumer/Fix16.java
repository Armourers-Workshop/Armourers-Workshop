package extensions.com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;

@Extension
public class Fix16 {

    public static @Self VertexConsumer vertex(@This VertexConsumer consumer, IMatrix4f matrix, float x, float y, float z) {
        float[] floats = {x, y, z, 1f};
        matrix.multiply(floats);
        return consumer.vertex(floats[0], floats[1], floats[2]);
    }

    public static @Self VertexConsumer normal(@This VertexConsumer consumer, IMatrix3f matrix, float x, float y, float z) {
        float[] floats = {x, y, z};
        matrix.multiply(floats);
        return consumer.normal(floats[0], floats[1], floats[2]);
    }
}
