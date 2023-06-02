package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector4f;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.math.IQuaternionf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

import java.nio.FloatBuffer;

@Available("[1.18, 1.20)")
@Mixin(Matrix4f.class)
@Implements(@Interface(iface = IMatrix4f.class, prefix = "aw$"))
public abstract class Matrix4fMixin {

    @Intrinsic(displace = true)
    public void aw$load(FloatBuffer buffer) {
        _aw$self().load(buffer);
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
        _aw$self().multiply(new Quaternion(q.x(), q.y(), q.z(), q.w()));
    }

    @Intrinsic(displace = true)
    public void aw$multiply(IMatrix4f matrix) {
        _aw$self().multiply(PoseStack.convertMatrix(matrix));
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

    private Matrix4f _aw$self() {
        return ObjectUtils.unsafeCast(this);
    }
}
