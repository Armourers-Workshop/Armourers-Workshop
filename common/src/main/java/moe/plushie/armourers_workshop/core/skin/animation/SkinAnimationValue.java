package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkinAnimationValue {

    private final float time;

    private final String key;
    private final SkinAnimationFunction function;

    private final List<Object> points;

    public SkinAnimationValue(CompoundTag tag) {
        this.time = tag.getOptionalFloat(Keys.TIME, 0f);
        this.key = tag.getOptionalString(Keys.KEY, null);
        this.function = SkinAnimationFunction.readFromTag(tag.get(Keys.FUNCTION));
        this.points = new ArrayList<>();
        for (var point : tag.getList(Keys.POINTS, Constants.TagFlags.COMPOUND)) {
            this.points.addAll(decodeValues((CompoundTag) point));
        }
    }

    public SkinAnimationValue(float time, String key, SkinAnimationFunction function, List<Object> points) {
        this.time = time;
        this.key = key;
        this.function = function;
        this.points = points;
    }

    public static SkinAnimationValue readFromStream(String key, IInputStream stream) throws IOException {
        var time = stream.readFloat();
        var function = SkinAnimationFunction.readFromStream(stream);
        var length = stream.readVarInt();
        var objects = new ArrayList<>();
        for (int i = 0; i < length && i < 6; i++) {
            var flags = stream.readVarInt();
            if ((flags & 0x40) != 0) {
                objects.add(stream.readString());
            } else {
                objects.add(stream.readFloat());
            }
        }
        return new SkinAnimationValue(time, key, function, objects);
    }

    public void writeToStream(String key, IOutputStream stream) throws IOException {
        stream.writeFloat(time);
        function.writeToStream(stream);
        stream.writeVarInt(points.size());
        for (var obj : points) {
            if (obj instanceof String script) {
                stream.writeVarInt(0x40);
                stream.writeString(script);
            } else if (obj instanceof Number number) {
                stream.writeVarInt(0x00);
                stream.writeFloat(number.floatValue());
            } else {
                stream.writeVarInt(0x00);
                stream.writeFloat(0);
            }
        }
    }

    public CompoundTag serializeNBT() {
        var pointTags = new ListTag();
        if (points.size() >= 3) {
            pointTags.add(encodeValues(points[0], points[1], points[2]));
        }
        if (points.size() >= 6) {
            pointTags.add(encodeValues(points[3], points[4], points[5]));
        }
        var tag = new CompoundTag();
        tag.putOptionalString(Keys.KEY, key, null);
        tag.putOptionalFloat(Keys.TIME, time, 0f);
        tag.put(Keys.FUNCTION, function.serializeNBT());
        tag.put(Keys.POINTS, pointTags);
        return tag;
    }

    public float getTime() {
        return time;
    }

    public String getKey() {
        return key;
    }

    public SkinAnimationFunction getFunction() {
        return function;
    }

    public List<Object> getPoints() {
        return points;
    }

    private List<Object> decodeValues(CompoundTag tag) {
        var points = new ArrayList<>();
        points.add(decodeValue(tag.get("X")));
        points.add(decodeValue(tag.get("Y")));
        points.add(decodeValue(tag.get("Z")));
        return points;
    }

    private CompoundTag encodeValues(Object x, Object y, Object z) {
        var tag = new CompoundTag();
        tag.put("X", encodeValue(x));
        tag.put("Y", encodeValue(y));
        tag.put("Z", encodeValue(z));
        return tag;
    }

    private Object decodeValue(Tag tag) {
        if (tag instanceof StringTag stringTag) {
            return stringTag.getAsString();
        }
        if (tag instanceof NumericTag numericTag) {
            return numericTag.getAsFloat();
        }
        return 0f;
    }

    private Tag encodeValue(Object value) {
        if (value instanceof String script) {
            return StringTag.valueOf(script);
        }
        if (value instanceof Number number) {
            return FloatTag.valueOf(number.floatValue());
        }
        return FloatTag.valueOf(0f);
    }

    public static class Keys {
        public static final String KEY = "Key";
        public static final String FUNCTION = "Function";
        public static final String TIME = "Time";
        public static final String POINTS = "Points";
    }
}
