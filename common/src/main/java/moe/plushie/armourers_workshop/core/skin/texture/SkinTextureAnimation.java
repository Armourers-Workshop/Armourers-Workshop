package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.api.skin.texture.ISkinTextureAnimation;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;
import java.util.ArrayList;

public class SkinTextureAnimation implements ISkinTextureAnimation {

    public static final SkinTextureAnimation EMPTY = new SkinTextureAnimation();

    private int frameTime = 0;
    private int frameCount = 0;
    private Mode frmaeMode = Mode.LOOP;
    private boolean interpolate = false;

    public SkinTextureAnimation() {
    }

    public SkinTextureAnimation(int frameTime, int frameCount, Mode frmaeMode, boolean interpolate) {
        this.frameTime = frameTime;
        this.frameCount = frameCount;
        this.frmaeMode = frmaeMode;
        this.interpolate = interpolate;
    }

    public void readFromStream(IInputStream stream) throws IOException {
        this.frameTime = stream.readVarInt();
        this.frameCount = stream.readVarInt();
        this.frmaeMode = Mode.readFromStream(stream);
        this.interpolate = stream.readBoolean();
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writeVarInt(this.frameTime);
        stream.writeVarInt(this.frameCount);
        frmaeMode.writeToStream(stream);
        stream.writeBoolean(interpolate);
    }

    public int frameTime() {
        return frameTime;
    }

    public int frameCount() {
        return frameCount;
    }

    public Mode frmaeMode() {
        return frmaeMode;
    }

    @Override
    public String toString() {
        if (this == EMPTY) {
            return "[]";
        }
        return String.format("[frameTime=%s, frameCount=%s, frameMode=%s]", frameTime, frameCount, frmaeMode);
    }

    public static class Mode {

        public static final Mode LOOP = new Mode(0, null);
        public static final Mode BACKWARDS = new Mode(1, null);
        public static final Mode BACK_AND_FORTH = new Mode(2, null);

        private final int type;
        private final int[] frames;

        private Mode(int type, int[] frames) {
            this.type = type;
            this.frames = frames;
        }

        public Mode(int[] frames) {
            this(3, frames);
        }

        public static Mode readFromStream(IInputStream stream) throws IOException {
            return switch (stream.readVarInt()) {
                case 0 -> LOOP;
                case 1 -> BACKWARDS;
                case 2 -> BACK_AND_FORTH;
                case 3 -> {
                    var values = new ArrayList<Integer>();
                    int len = stream.readVarInt();
                    for (int i = 0; i < len; ++i) {
                        values.add(stream.readVarInt());
                    }
                    int[] frames = new int[values.size()];
                    for (int i = 0; i < frames.length; ++i) {
                        frames[i] = values.get(i);
                    }
                    yield new Mode(frames);
                }
                default -> LOOP;
            };
        }

        public void writeToStream(IOutputStream stream) throws IOException {
            stream.writeVarInt(type);
            if (type == 3) {
                stream.writeVarInt(frames.length);
                for (int value : frames) {
                    stream.writeVarInt(value);
                }
            }
        }

        public int type() {
            return type;
        }

        public int[] frames() {
            return frames;
        }

        @Override
        public String toString() {
            if (this == LOOP) {
                return "LOOP";
            }
            if (this == BACKWARDS) {
                return "BACKWARDS";
            }
            if (this == BACK_AND_FORTH) {
                return "BACK_AND_FORTH";
            }
            return "CUSTOM";
        }
    }
}
