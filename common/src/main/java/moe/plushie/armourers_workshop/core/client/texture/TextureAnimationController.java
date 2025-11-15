package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureAnimation;

@OnlyIn(Dist.CLIENT)
public class TextureAnimationController {

    private static final OpenMatrix4f IDENTITY = OpenMatrix4f.identity();

    public static final TextureAnimationController NONE = new TextureAnimationController(0, 0, SkinTextureAnimation.Mode.LOOP);
    public static final TextureAnimationController DEFAULT = new TextureAnimationController(25, 256, SkinTextureAnimation.Mode.LOOP);

    private final int frameCount;
    private final float frameTime;

    private final OpenMatrix4f[] frames;

    public TextureAnimationController(SkinTextureAnimation animation) {
        this(animation.frameTime(), animation.frameCount(), animation.frmaeMode());
    }

    public TextureAnimationController(int frameTime, int frameCount, SkinTextureAnimation.Mode frameMode) {
        this.frames = _genTextureMatrices(frameCount, frameMode);
        this.frameTime = Math.max(frameTime, 1) / 1000f;
        this.frameCount = frames.length;
    }

    public static TextureAnimationController of(IRenderType renderType) {
        // is default?
        if (renderType == SkinRenderType.BLOCK_FACE_SOLID || renderType == SkinRenderType.BLOCK_FACE_LIGHTING || renderType == SkinRenderType.BLOCK_FACE_TRANSLUCENT || renderType == SkinRenderType.BLOCK_FACE_LIGHTING_TRANSLUCENT) {
            return DEFAULT;
        }
        // is custom?
        var storage = SmartTexture.of(renderType);
        if (storage != null) {
            return storage.animationController();
        }
        return NONE;
    }

    public OpenMatrix4f getTextureMatrix(double animationTime) {
        if (frameCount != 0) {
            var idx = (int) (animationTime / frameTime);
            return frames[idx % frameCount];
        }
        return IDENTITY;
    }

    private OpenMatrix4f[] _genTextureMatrices(int total, SkinTextureAnimation.Mode mode) {
        if (total <= 0) {
            return new OpenMatrix4f[0];
        }
        if (mode.equals(SkinTextureAnimation.Mode.LOOP)) {
            var frames = new OpenMatrix4f[total];
            for (var i = 0; i < total; ++i) {
                frames[i] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.equals(SkinTextureAnimation.Mode.BACKWARDS)) {
            var frames = new OpenMatrix4f[total];
            for (var i = 0; i < total; ++i) {
                frames[total - i - 1] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.equals(SkinTextureAnimation.Mode.BACK_AND_FORTH)) {
            var frames = new OpenMatrix4f[total + total - 2];
            for (var i = 0; i < total; ++i) {
                frames[i] = _genTextureMatrix(i / (float) total);
            }
            for (var i = 1; i < total; ++i) {
                frames[total - i - 1] = _genTextureMatrix(i / (float) total);
            }
            return frames;
        }
        if (mode.frames() != null) {
            var indexes = mode.frames();
            var frames = new OpenMatrix4f[indexes.length];
            for (var i = 0; i < indexes.length; ++i) {
                frames[i] = _genTextureMatrix(OpenMath.clamp(indexes[i], 0, total - 1) / (float) total);
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
