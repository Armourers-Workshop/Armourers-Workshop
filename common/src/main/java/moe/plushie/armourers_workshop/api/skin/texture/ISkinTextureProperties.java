package moe.plushie.armourers_workshop.api.skin.texture;

/**
 * <a href="https://shaderlabs.org/wiki/LabPBR_Material_Standard">LabPBR Material Standard</a>
 */
public interface ISkinTextureProperties {

    // suffix _e
    boolean isEmissive();

    boolean isTranslucent();

    // suffix _n
    boolean isNormal();

    // suffix _s
    boolean isSpecular();

    // GL_TEXTURE_MIN_FILTER = GL_LINEAR_MIPMAP_LINEAR or GL_NEAREST, GL_TEXTURE_MAG_FILTER = GL_NEAREST
    boolean isBlurFilter();

    // GL_TEXTURE_WRAP_S = GL_CLAMP_TO_EDGE, GL_TEXTURE_WRAP_T = GL_CLAMP_TO_EDGE
    boolean isClampToEdge();
}
