package extensions.com.mojang.blaze3d.vertex.VertexConsumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@Extension
public class ABI {

    public static @Self VertexConsumer vertex(@This VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z) {
        return consumer.vertex(pose.pose(), x, y, z);
    }

    public static @Self VertexConsumer normal(@This VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z) {
        return consumer.normal(pose.normal(), x, y, z);
    }
}
