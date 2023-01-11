package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.spongepowered.asm.mixin.*;

import java.nio.FloatBuffer;

@Mixin(Matrix4f.class)
@Implements(@Interface(iface = IMatrix4f.class, prefix = "aw$"))
public abstract class AbstractMatrix4fMixin {

    @Shadow protected float m00;
    @Shadow protected float m01;
    @Shadow protected float m02;
    @Shadow protected float m03;
    @Shadow protected float m10;
    @Shadow protected float m11;
    @Shadow protected float m12;
    @Shadow protected float m13;
    @Shadow protected float m20;
    @Shadow protected float m21;
    @Shadow protected float m22;
    @Shadow protected float m23;
    @Shadow protected float m30;
    @Shadow protected float m31;
    @Shadow protected float m32;
    @Shadow protected float m33;

    @Intrinsic(displace = true)
    public void aw$load(FloatBuffer buffer) {
        m00 = buffer.get(bufferIndex(0, 0));
        m01 = buffer.get(bufferIndex(0, 1));
        m02 = buffer.get(bufferIndex(0, 2));
        m03 = buffer.get(bufferIndex(0, 3));
        m10 = buffer.get(bufferIndex(1, 0));
        m11 = buffer.get(bufferIndex(1, 1));
        m12 = buffer.get(bufferIndex(1, 2));
        m13 = buffer.get(bufferIndex(1, 3));
        m20 = buffer.get(bufferIndex(2, 0));
        m21 = buffer.get(bufferIndex(2, 1));
        m22 = buffer.get(bufferIndex(2, 2));
        m23 = buffer.get(bufferIndex(2, 3));
        m30 = buffer.get(bufferIndex(3, 0));
        m31 = buffer.get(bufferIndex(3, 1));
        m32 = buffer.get(bufferIndex(3, 2));
        m33 = buffer.get(bufferIndex(3, 3));
    }

    @Intrinsic(displace = true)
    public void aw$store(FloatBuffer buffer) {
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
    public void aw$rotate(IQuaternionf q) {
        _aw$self().multiply(MatrixUtils.of(q));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IMatrix4f matrix) {
        _aw$self().multiply(MatrixUtils.of(matrix));
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

    private static int bufferIndex(int i, int j) {
        return j * 4 + i;
    }
}
