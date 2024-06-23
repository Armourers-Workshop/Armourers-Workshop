package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.io.IOException;

public interface SkinAnimationFunction {

    SkinAnimationFunction STEP = (a, b, t) -> b;
    SkinAnimationFunction LINEAR = (a, b, t) -> a + (b - a) * t;
    SkinAnimationFunction SMOOTH = (a, b, t) -> a + (b - a) * t;

    float interpolating(float a, float b, float t);

    static SkinAnimationFunction bezier(float[] parameters) {
        return new SkinAnimationFunction() {
            @Override
            public float interpolating(float a, float b, float t) {
                // TODO: no impl @SAGESSE
                return a;
            }

            @Override
            public void writeToStream(IOutputStream stream) throws IOException {
                stream.writeVarInt(3);
                stream.writeFloatArray(parameters);
            }

            @Override
            public CompoundTag serializeNBT() {
                var tag = new CompoundTag();
                var list = new ListTag();
                for (var value : parameters) {
                    list.add(FloatTag.valueOf(value));
                }
                tag.put("Bezier", list);
                return tag;
            }
        };
    }

    static SkinAnimationFunction readFromTag(Tag tag) {
        // a simple value.
        if (tag instanceof IntTag intTag) {
            return switch (intTag.getAsInt()) {
                case 2 -> SMOOTH;
                case 1 -> STEP;
                case 0 -> LINEAR;
                default -> LINEAR; // can't parse, downcast.
            };
        }
        if (tag instanceof CompoundTag compoundTag) {
            var list = compoundTag.getList("Bezier", Constants.TagFlags.FLOAT);
            var parameters = new float[12];
            for (int i = 0; i < list.size(); ++i) {
                parameters[i] = list.getFloat(i);
            }
            return bezier(parameters);
        }
        return LINEAR; // can't parse, downcast.
    }

    static SkinAnimationFunction readFromStream(IInputStream stream) throws IOException {
        return switch (stream.readVarInt()) {
            case 3 -> bezier(stream.readFloatArray(12));
            case 2 -> SMOOTH;
            case 1 -> STEP;
            case 0 -> LINEAR;
            default -> LINEAR; // can't parse, downcast.
        };
    }

    default void writeToStream(IOutputStream stream) throws IOException {
        if (this == STEP) {
            stream.writeVarInt(1);
        } else if (this == SMOOTH) {
            stream.writeVarInt(2);
        } else {
            stream.writeVarInt(0);
        }
    }

    default Tag serializeNBT() {
        if (this == STEP) {
            return IntTag.valueOf(1);
        } else if (this == SMOOTH) {
            return IntTag.valueOf(2);
        } else {
            return IntTag.valueOf(0);
        }
    }
}
