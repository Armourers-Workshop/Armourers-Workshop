package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractMatrix3fWrapper;
import moe.plushie.armourers_workshop.compatibility.AbstractMatrix4fWrapper;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseWrapper;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PoseStack.Pose.class)
public abstract class AbstractPoseStateWrapperMixin implements AbstractPoseWrapper {

    private AbstractMatrix3fWrapper aw$normal;
    private AbstractMatrix4fWrapper aw$pose;

    @Shadow public abstract Matrix3f normal();

    @Shadow public abstract Matrix4f pose();

    @Override
    public AbstractMatrix3fWrapper aw$normal() {
        if (aw$normal == null) {
            aw$normal = new AbstractMatrix3fWrapper(normal());
        }
        return aw$normal;
    }

    @Override
    public AbstractMatrix4fWrapper aw$pose() {
        if (aw$pose == null) {
            aw$pose = new AbstractMatrix4fWrapper(pose());
        }
        return aw$pose;
    }

}
