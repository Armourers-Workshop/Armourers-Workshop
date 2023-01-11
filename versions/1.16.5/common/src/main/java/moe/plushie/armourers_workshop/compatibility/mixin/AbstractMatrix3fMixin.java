package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.math.Matrix3f;
import com.mojang.math.Vector3f;
import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.spongepowered.asm.mixin.*;

import java.nio.FloatBuffer;

@Mixin(Matrix3f.class)
@Implements(@Interface(iface = IMatrix3f.class, prefix = "aw$"))
public abstract class AbstractMatrix3fMixin {

    @Shadow protected float m00;
    @Shadow protected float m01;
    @Shadow protected float m02;
    @Shadow protected float m10;
    @Shadow protected float m11;
    @Shadow protected float m12;
    @Shadow protected float m20;
    @Shadow protected float m21;
    @Shadow protected float m22;

    @Intrinsic(displace = true)
    public void aw$load(FloatBuffer buffer) {
        m00 = buffer.get(bufferIndex(0, 0));
        m01 = buffer.get(bufferIndex(0, 1));
        m02 = buffer.get(bufferIndex(0, 2));
        m10 = buffer.get(bufferIndex(1, 0));
        m11 = buffer.get(bufferIndex(1, 1));
        m12 = buffer.get(bufferIndex(1, 2));
        m20 = buffer.get(bufferIndex(2, 0));
        m21 = buffer.get(bufferIndex(2, 1));
        m22 = buffer.get(bufferIndex(2, 2));
    }

    @Intrinsic(displace = true)
    public void aw$store(FloatBuffer buffer) {
        buffer.put(bufferIndex(0, 0), m00);
        buffer.put(bufferIndex(0, 1), m01);
        buffer.put(bufferIndex(0, 2), m02);
        buffer.put(bufferIndex(1, 0), m10);
        buffer.put(bufferIndex(1, 1), m11);
        buffer.put(bufferIndex(1, 2), m12);
        buffer.put(bufferIndex(2, 0), m20);
        buffer.put(bufferIndex(2, 1), m21);
        buffer.put(bufferIndex(2, 2), m22);
    }

    @Intrinsic(displace = true)
    public void aw$scale(float x, float y, float z) {
        _aw$self().mul(Matrix3f.createScaleMatrix(x, y, z));
    }

    @Intrinsic(displace = true)
    public void aw$rotate(IQuaternionf q) {
        _aw$self().mul(MatrixUtils.of(q));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IMatrix3f matrix) {
        _aw$self().mul(MatrixUtils.of(matrix));
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

    private static int bufferIndex(int i, int j) {
        return j * 3 + i;
    }
}
