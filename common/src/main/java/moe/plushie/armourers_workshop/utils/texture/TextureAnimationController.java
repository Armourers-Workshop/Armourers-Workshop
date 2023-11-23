package moe.plushie.armourers_workshop.utils.texture;

import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;

public class TextureAnimationController {

    private static final OpenMatrix4f IDENTITY = OpenMatrix4f.identity();

    public static final TextureAnimationController NONE = new TextureAnimationController(0, 0, TextureAnimation.Mode.LOOP);
    public static final TextureAnimationController DEFAULT = new TextureAnimationController(25, 256, TextureAnimation.Mode.LOOP);

    private final int frameCount;
    private final int frameTime;

    private final OpenMatrix4f[] frames;

    public TextureAnimationController(int frameTime, int frameCount, TextureAnimation.Mode frameMode) {
        this.frames = _genTextureMatrices(frameCount, frameMode);
        this.frameTime = frameTime;
        this.frameCount = frames.length;
    }

    public OpenMatrix4f getTextureMatrix(int partialTicks) {
        if (frameCount != 0) {
            int idx = partialTicks / frameTime;
            return frames[idx % frameCount];
        }
        return IDENTITY;
    }

    private OpenMatrix4f[] _genTextureMatrices(int total, TextureAnimation.Mode mode) {
        if (mode.equals(TextureAnimation.Mode.LOOP)) {
            OpenMatrix4f[] frames = new OpenMatrix4f[total];
            for (int i = 0; i < total; ++i) {
                frames[i] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.equals(TextureAnimation.Mode.BACKWARDS)) {
            OpenMatrix4f[] frames = new OpenMatrix4f[total];
            for (int i = 0; i < total; ++i) {
                frames[total - i - 1] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.equals(TextureAnimation.Mode.BACK_AND_FORTH)) {
            OpenMatrix4f[] frames = new OpenMatrix4f[total + total - 2];
            for (int i = 0; i < total; ++i) {
                frames[i] = _genTextureMatrix(i / (float) total);
            }
            for (int i = 1; i < total; ++i) {
                frames[total - i - 1] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.getFrames() != null) {
            int[] indexes = mode.getFrames();
            OpenMatrix4f[] frames = new OpenMatrix4f[indexes.length];
            for (int i = 0; i < indexes.length; ++i) {
                frames[i] = _genTextureMatrix(MathUtils.clamp(indexes[i], 0, total - 1) / (float) total);
            }
            return frames;
        }
        return new OpenMatrix4f[0];
    }

    private OpenMatrix4f _genTextureMatrix(float offset) {
        if (offset != 0) {
            return OpenMatrix4f.createTranslateMatrix(0, offset, 0);
        }
        return IDENTITY;
    }
}
