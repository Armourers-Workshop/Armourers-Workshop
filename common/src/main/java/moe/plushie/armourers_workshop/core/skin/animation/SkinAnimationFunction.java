package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

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
        };
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
}
