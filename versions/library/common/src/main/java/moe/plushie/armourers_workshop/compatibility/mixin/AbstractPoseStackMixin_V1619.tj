package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Available("[1.16, 1.19.3)")
@Mixin(PoseStack.class)
@Implements(@Interface(iface = IPoseStack.class, prefix = "aw$"))
public abstract class AbstractPoseStackMixin_V1619 {

    @Intrinsic(displace = true)
    public void aw$pushPose() {
        _aw$self().pushPose();
    }

    @Intrinsic(displace = true)
    public void aw$popPose() {
        _aw$self().popPose();
    }

    @Intrinsic(displace = true)
    public void aw$translate(float x, float y, float z) {
        _aw$self().translate(x, y, z);
    }

    @Intrinsic(displace = true)
    public void aw$scale(float x, float y, float z) {
        _aw$self().scale(x, y, z);
    }

    @Intrinsic(displace = true)
    public void aw$rotate(IQuaternionf quaternion) {
        _aw$self().mulPose(MatrixUtils.of(quaternion));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IMatrix4f matrix) {
        aw$lastPose().multiply(matrix);
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IPoseStack poseStack) {
        PoseStack sourceStack = _aw$self();
        PoseStack targetStack = ObjectUtils.safeCast(poseStack, PoseStack.class);
        if (targetStack != null) {
            sourceStack.last().pose().multiply(targetStack.last().pose());
            sourceStack.last().normal().mul(targetStack.last().normal());
        } else {
            aw$lastPose().multiply(poseStack.lastPose());
            aw$lastNormal().multiply(poseStack.lastNormal());
        }
    }

    @Intrinsic(displace = true)
    public IMatrix4f aw$lastPose() {
        return ObjectUtils.unsafeCast(_aw$self().last().pose());
    }

    @Intrinsic(displace = true)
    public IMatrix3f aw$lastNormal() {
        return ObjectUtils.unsafeCast(_aw$self().last().normal());
    }

    @Intrinsic(displace = true)
    public IPoseStack aw$copy() {
        PoseStack targetStack = _aw$self();
        PoseStack sourceStack = new PoseStack();
        sourceStack.last().pose().multiply(targetStack.last().pose());
        sourceStack.last().normal().mul(targetStack.last().normal());
        return ObjectUtils.unsafeCast(sourceStack);
    }

    @Intrinsic(displace = true)
    public PoseStack aw$cast() {
        return _aw$self();
    }

    private PoseStack _aw$self() {
        return ObjectUtils.unsafeCast(this);
    }
}
