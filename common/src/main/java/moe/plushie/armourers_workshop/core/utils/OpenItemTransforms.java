package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class OpenItemTransforms extends LinkedHashMap<String, OpenTransform3f> {

    public static IDataCodec<OpenItemTransforms> CODEC = IDataCodec.COMPOUND_TAG.xmap(OpenItemTransforms::new, OpenItemTransforms::serializeNBT);

    public OpenItemTransforms() {
    }

    public OpenItemTransforms(CompoundTag nbt) {
        for (var key : nbt.keySet()) {
            nbt.getOptionalList(key, Constants.TagFlags.FLOAT).ifPresent(values -> put(key, deserializeTransform(values)));
        }
    }

    public void put(OpenItemDisplayContext key, OpenTransform3f value) {
        put(key.serializedName(), value);
    }

    public OpenTransform3f get(OpenItemDisplayContext key) {
        return get(key.serializedName());
    }


    public void setOffset(OpenTransform3f offset) {
        put("offset", offset);
    }

    @Nullable
    public OpenTransform3f offset() {
        return get("offset");
    }


    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        forEach((key, value) -> nbt.put(key, serializeTransform(value)));
        return nbt;
    }

    private ListTag serializeTransform(OpenTransform3f transform) {
        var tag = new ListTag();
        if (transform.isIdentity()) {
            return tag;
        }
        var translate = transform.translate();
        tag.add(FloatTag.valueOf(translate.x()));
        tag.add(FloatTag.valueOf(translate.y()));
        tag.add(FloatTag.valueOf(translate.z()));

        var rotation = transform.rotation();
        tag.add(FloatTag.valueOf(rotation.x()));
        tag.add(FloatTag.valueOf(rotation.y()));
        tag.add(FloatTag.valueOf(rotation.z()));

        var scale = transform.scale();
        tag.add(FloatTag.valueOf(scale.x()));
        tag.add(FloatTag.valueOf(scale.y()));
        tag.add(FloatTag.valueOf(scale.z()));

        return tag;
    }

    private OpenTransform3f deserializeTransform(ListTag tag) {
        if (tag.isEmpty() || tag.size() < 9) {
            return OpenTransform3f.IDENTITY;
        }
        var tx = tag.getOptionalFloat(0).orElse(0f);
        var ty = tag.getOptionalFloat(1).orElse(0f);
        var tz = tag.getOptionalFloat(2).orElse(0f);
        var translate = new OpenVector3f(tx, ty, tz);

        var rx = tag.getOptionalFloat(3).orElse(0f);
        var ry = tag.getOptionalFloat(4).orElse(0f);
        var rz = tag.getOptionalFloat(5).orElse(0f);
        var rotation = new OpenVector3f(rx, ry, rz);

        var sx = tag.getOptionalFloat(6).orElse(0f);
        var sy = tag.getOptionalFloat(7).orElse(0f);
        var sz = tag.getOptionalFloat(8).orElse(0f);
        var scale = new OpenVector3f(sx, sy, sz);

        return OpenTransform3f.create(translate, rotation, scale);
    }
}
