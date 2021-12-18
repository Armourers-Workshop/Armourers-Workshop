package moe.plushie.armourers_workshop.core.api.common.skin;

import moe.plushie.armourers_workshop.core.skin.type.Point3D;

import java.awt.Point;

public interface ISkinPartTypeTextured extends ISkinPartType {

    /** Location of the texture in skin storage. */
    Point getTextureSkinPos();

    /** Should this texture be mirrored? */
    boolean isTextureMirrored();

    /** UV location of the models base texture. */
    Point getTextureBasePos();

    /** UV location of the models overlay texture. */
    Point getTextureOverlayPos();

    /** Size of the model the texture is used on. */
    Point3D getTextureModelSize();
}
