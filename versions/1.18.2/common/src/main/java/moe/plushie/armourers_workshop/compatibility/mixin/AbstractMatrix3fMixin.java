package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.math.Matrix3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.compatibility.AbstractMatrix3fWrapper;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.FloatBuffer;

@Mixin(Matrix3f.class)
@Implements(@Interface(iface = IMatrix3f.class, prefix = "aw$"))
public abstract class AbstractMatrix3fMixin {

    @Intrinsic(displace = true)
    public void aw$set(FloatBuffer buffer) {
        _aw$self().load(buffer);
    }

    @Intrinsic(displace = true)
    public void aw$get(FloatBuffer buffer) {
        _aw$self().store(buffer);
    }

    @Intrinsic(displace = true)
    public void aw$rotate(IQuaternionf q) {
        _aw$self().mul(new Quaternion(q.i(), q.j(), q.k(), q.r()));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IMatrix3f matrix) {
        _aw$self().mul(AbstractMatrix3fWrapper.of(matrix));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(float[] values) {
        Vector3f vec = new Vector3f(values[0], values[1], values[2]);
        vec.transform(_aw$self());
        values[0] = vec.x();
        values[1] = vec.y();
        values[2] = vec.z();
    }

    @Intrinsic(displace = true)
    public void aw$invert() {
        _aw$self().invert();
    }

    @Intrinsic(displace = true)
    public IMatrix3f aw$copy() {
        return ObjectUtils.unsafeCast(_aw$self().copy());
    }

    private Matrix3f _aw$self() {
        return ObjectUtils.unsafeCast(this);
    }
}
