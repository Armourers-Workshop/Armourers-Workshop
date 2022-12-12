package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PoseStack.Pose.class)
public abstract class AbstractPoseStateWrapperMixin implements AbstractPoseStack.Pose {

    private AbstractPoseStack.Mat3 aw$normal;
    private AbstractPoseStack.Mat4 aw$pose;

    @Shadow public abstract Matrix3f normal();
    @Shadow public abstract Matrix4f pose();

    @Override
    public IMatrix3f aw$getNormal() {
        if (aw$normal == null) {
            aw$normal = new AbstractPoseStack.Mat3(normal());
        }
        return aw$normal;
    }

    @Override
    public IMatrix4f aw$getPose() {
        if (aw$pose == null) {
            aw$pose = new AbstractPoseStack.Mat4(pose());
        }
        return aw$pose;
    }

}
