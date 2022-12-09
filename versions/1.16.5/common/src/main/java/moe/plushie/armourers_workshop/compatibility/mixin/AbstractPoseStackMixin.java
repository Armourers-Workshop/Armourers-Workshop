package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.FloatBuffer;

@Mixin(PoseStack.class)
@Implements(@Interface(iface = IPoseStack.class, prefix = "aw$"))
public abstract class AbstractPoseStackMixin {

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
    public void aw$rotate(IQuaternionf q) {
        _aw$self().mulPose(new Quaternion(q.i(), q.j(), q.k(), q.r()));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IMatrix4f matrix) {
        aw$lastPose().multiply(matrix);
    }

    @Intrinsic(displace = true)
    public IMatrix4f aw$lastPose() {
        return ObjectUtils.unsafeCast(_aw$self().last().pose());
    }

    @Intrinsic(displace = true)
    public IMatrix3f aw$lastNormal() {
        return ObjectUtils.unsafeCast(_aw$self().last().normal());
    }

    private PoseStack _aw$self() {
        return ObjectUtils.unsafeCast(this);
    }
}
