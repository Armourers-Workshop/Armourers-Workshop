package moe.plushie.armourers_workshop.core.data.transform;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

import java.util.LinkedHashMap;

public class SkinItemTransforms extends LinkedHashMap<String, ITransformf> {

    public SkinItemTransforms() {
    }

    public SkinItemTransforms(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            put(key, deserializeTransform(nbt.getList(key, Constants.TagFlags.FLOAT)));
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        forEach((key, value) -> nbt.put(key, serializeTransform(value)));
        return nbt;
    }

    private ListTag serializeTransform(ITransformf transform) {
        ListTag tag = new ListTag();
        if (transform.isIdentity()) {
            return tag;
        }
        IVector3f translate = transform.getTranslate();
        tag.add(FloatTag.valueOf(translate.getX()));
        tag.add(FloatTag.valueOf(translate.getY()));
        tag.add(FloatTag.valueOf(translate.getZ()));

        IVector3f rotation = transform.getRotation();
        tag.add(FloatTag.valueOf(rotation.getX()));
        tag.add(FloatTag.valueOf(rotation.getY()));
        tag.add(FloatTag.valueOf(rotation.getZ()));

        IVector3f scale = transform.getScale();
        tag.add(FloatTag.valueOf(scale.getX()));
        tag.add(FloatTag.valueOf(scale.getY()));
        tag.add(FloatTag.valueOf(scale.getZ()));

        return tag;
    }

    private ITransformf deserializeTransform(ListTag tag) {
        if (tag.isEmpty() || tag.size() < 9) {
            return SkinTransform.IDENTITY;
        }
        float tx = tag.getFloat(0);
        float ty = tag.getFloat(1);
        float tz = tag.getFloat(2);
        Vector3f translate = new Vector3f(tx, ty, tz);

        float rx = tag.getFloat(3);
        float ry = tag.getFloat(4);
        float rz = tag.getFloat(5);
        Vector3f rotation = new Vector3f(rx, ry, rz);

        float sx = tag.getFloat(6);
        float sy = tag.getFloat(7);
        float sz = tag.getFloat(8);
        Vector3f scale = new Vector3f(sx, sy, sz);

        return SkinTransform.create(translate, rotation, scale);
    }
}
