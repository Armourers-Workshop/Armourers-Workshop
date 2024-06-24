package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkinAnimationValue {

    private final float time;

    private final String key;
    private final SkinAnimationFunction function;

    private final List<Object> points;

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
}
