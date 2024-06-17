package moe.plushie.armourers_workshop.core.data.transform;

import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
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

    public void put(AbstractItemTransformType key, ITransformf value) {
        put(key.getName(), value);
    }

    public ITransformf get(AbstractItemTransformType key) {
        return super.get(key.getName());
    }

    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        forEach((key, value) -> nbt.put(key, serializeTransform(value)));
        return nbt;
    }

    private ListTag serializeTransform(ITransformf transform) {
        var tag = new ListTag();
        if (transform.isIdentity()) {
            return tag;
        }
        var translate = transform.getTranslate();
        tag.add(FloatTag.valueOf(translate.getX()));
        tag.add(FloatTag.valueOf(translate.getY()));
        tag.add(FloatTag.valueOf(translate.getZ()));

        var rotation = transform.getRotation();
        tag.add(FloatTag.valueOf(rotation.getX()));
        tag.add(FloatTag.valueOf(rotation.getY()));
        tag.add(FloatTag.valueOf(rotation.getZ()));

        var scale = transform.getScale();
        tag.add(FloatTag.valueOf(scale.getX()));
        tag.add(FloatTag.valueOf(scale.getY()));
        tag.add(FloatTag.valueOf(scale.getZ()));

        return tag;
    }

    private ITransformf deserializeTransform(ListTag tag) {
        if (tag.isEmpty() || tag.size() < 9) {
            return SkinTransform.IDENTITY;
        }
        var tx = tag.getFloat(0);
        var ty = tag.getFloat(1);
        var tz = tag.getFloat(2);
        var translate = new Vector3f(tx, ty, tz);

        var rx = tag.getFloat(3);
        var ry = tag.getFloat(4);
        var rz = tag.getFloat(5);
        var rotation = new Vector3f(rx, ry, rz);

        var sx = tag.getFloat(6);
        var sy = tag.getFloat(7);
        var sz = tag.getFloat(8);
        var scale = new Vector3f(sx, sy, sz);

        return SkinTransform.create(translate, rotation, scale);
    }
}
