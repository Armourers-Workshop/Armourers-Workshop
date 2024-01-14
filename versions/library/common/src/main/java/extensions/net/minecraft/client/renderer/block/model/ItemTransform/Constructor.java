package extensions.net.minecraft.client.renderer.block.model.ItemTransform;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import net.minecraft.client.renderer.block.model.ItemTransform;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.20, )")
public class Constructor {

    public static ItemTransform from(@ThisClass Class<?> clazz, @Nullable ITransformf transform) {
        // ignore when transform is identity.
        if (transform == null || transform.isIdentity()) {
            return ItemTransform.NO_TRANSFORM;
        }
        float tx = transform.getTranslate().getX() / 16f;
        float ty = transform.getTranslate().getY() / 16f;
        float tz = transform.getTranslate().getZ() / 16f;
        float rx = transform.getRotation().getX();
        float ry = transform.getRotation().getY();
        float rz = transform.getRotation().getZ();
        float sx = transform.getScale().getX();
        float sy = transform.getScale().getY();
        float sz = transform.getScale().getZ();
        return new ItemTransform(new Vector3f(rx, ry, rz), new Vector3f(tx, ty, tz), new Vector3f(sx, sy, sz));
    }
}
