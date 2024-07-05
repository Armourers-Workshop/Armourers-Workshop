package moe.plushie.armourers_workshop.api.common;

/**
 * <a href="https://shaderlabs.org/wiki/LabPBR_Material_Standard">LabPBR Material Standard</a>
 */
public interface ITextureProperties {

    // suffix _e
    boolean isEmissive();

    boolean isParticle();

    // suffix _n
    boolean isNormal();

    // suffix _s
    boolean isSpecular();
}
