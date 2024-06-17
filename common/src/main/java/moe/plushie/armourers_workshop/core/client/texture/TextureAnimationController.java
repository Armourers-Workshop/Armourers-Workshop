package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.texture.TextureAnimation;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class TextureAnimationController {

    private static final OpenMatrix4f IDENTITY = OpenMatrix4f.identity();

    public static final TextureAnimationController NONE = new TextureAnimationController(0, 0, TextureAnimation.Mode.LOOP);
    public static final TextureAnimationController DEFAULT = new TextureAnimationController(25, 256, TextureAnimation.Mode.LOOP);

    private final int frameCount;
    private final float frameTime;

    private final OpenMatrix4f[] frames;

    public TextureAnimationController(TextureAnimation animation) {
        this(animation.getFrameTime(), animation.getFrameCount(), animation.getFrmaeMode());
    }

    public TextureAnimationController(int frameTime, int frameCount, TextureAnimation.Mode frameMode) {
        this.frames = _genTextureMatrices(frameCount, frameMode);
        this.frameTime = Math.max(frameTime, 1) / 1000f;
        this.frameCount = frames.length;
    }

    public static TextureAnimationController of(RenderType renderType) {
        // is default?
        if (renderType == SkinRenderType.FACE_SOLID || renderType == SkinRenderType.FACE_LIGHTING || renderType == SkinRenderType.FACE_TRANSLUCENT || renderType == SkinRenderType.FACE_LIGHTING_TRANSLUCENT) {
            return DEFAULT;
        }
        // is custom?
        var storage = TextureManager.Entry.of(renderType);
        if (storage != null) {
            return storage.getAnimationController();
        }
        return NONE;
    }

    public OpenMatrix4f getTextureMatrix(float animationTicks) {
        if (frameCount != 0) {
            var idx = (int) (animationTicks / frameTime);
            return frames[idx % frameCount];
        }
        return IDENTITY;
    }

    private OpenMatrix4f[] _genTextureMatrices(int total, TextureAnimation.Mode mode) {
        if (total <= 0) {
            return new OpenMatrix4f[0];
        }
        if (mode.equals(TextureAnimation.Mode.LOOP)) {
            var frames = new OpenMatrix4f[total];
            for (var i = 0; i < total; ++i) {
                frames[i] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.equals(TextureAnimation.Mode.BACKWARDS)) {
            var frames = new OpenMatrix4f[total];
            for (var i = 0; i < total; ++i) {
                frames[total - i - 1] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.equals(TextureAnimation.Mode.BACK_AND_FORTH)) {
            var frames = new OpenMatrix4f[total + total - 2];
            for (var i = 0; i < total; ++i) {
                frames[i] = _genTextureMatrix(i / (float) total);
            }
            for (var i = 1; i < total; ++i) {
                frames[total - i - 1] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.getFrames() != null) {
            var indexes = mode.getFrames();
            var frames = new OpenMatrix4f[indexes.length];
            for (var i = 0; i < indexes.length; ++i) {
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
