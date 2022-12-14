package moe.plushie.armourers_workshop.api.math;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IPoseStack {

    void pushPose();

    void popPose();

    void translate(float x, float y, float z);

    void scale(float x, float y, float z);

    void rotate(IQuaternionf quaternion);

    void multiply(IMatrix4f matrix);

    void multiply(IPoseStack poseStack);

    IMatrix4f lastPose();

    IMatrix3f lastNormal();

    IPoseStack copy();

    @Environment(value = EnvType.CLIENT)
    PoseStack cast();
}
