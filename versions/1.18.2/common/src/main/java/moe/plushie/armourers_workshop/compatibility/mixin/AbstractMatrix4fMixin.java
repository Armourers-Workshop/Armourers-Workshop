package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.FloatBuffer;

@Mixin(Matrix4f.class)
@Implements(@Interface(iface = IMatrix4f.class, prefix = "aw$"))
public abstract class AbstractMatrix4fMixin {

    @Intrinsic(displace = true)
    public void aw$set(FloatBuffer buffer) {
        _aw$self().load(buffer);
    }

    @Intrinsic(displace = true)
    public void aw$get(FloatBuffer buffer) {
        _aw$self().store(buffer);
    }

    @Intrinsic(displace = true)
    public void aw$scale(float x, float y, float z) {
        _aw$self().multiply(Matrix4f.createScaleMatrix(x, y, z));
    }

    @Intrinsic(displace = true)
    public void aw$translate(float x, float y, float z) {
        _aw$self().multiply(Matrix4f.createTranslateMatrix(x, y, z));
    }

    @Intrinsic(displace = true)
    public void aw$rotate(IQuaternionf quaternion) {
        _aw$self().multiply(AbstractPoseStack.of(quaternion));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IMatrix4f matrix) {
        _aw$self().multiply(AbstractPoseStack.of(matrix));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(float[] values) {
        Vector4f vec = new Vector4f(values[0], values[1], values[2], values[3]);
        vec.transform(_aw$self());
        values[0] = vec.x();
        values[1] = vec.y();
        values[2] = vec.z();
        values[3] = vec.w();
    }

    @Intrinsic(displace = true)
    public void aw$invert() {
        _aw$self().invert();
    }

    @Intrinsic(displace = true)
    public IMatrix4f aw$copy() {
        return ObjectUtils.unsafeCast(_aw$self().copy());
    }

    private Matrix4f _aw$self() {
        return ObjectUtils.unsafeCast(this);
    }
}
