package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.math.ITexturePos;
import moe.plushie.armourers_workshop.api.math.IVector3i;

public interface ISkinPartTypeTextured extends ISkinPartType {

    /**
     * Should this texture be mirrored?
     */
    boolean isTextureMirrored();

    /**
     * Location of the texture in skin storage.
     */
    ITexturePos getTextureSkinPos();

    /**
     * UV location of the models base texture.
     */
    ITexturePos getTextureBasePos();

    /**
     * UV location of the models overlay texture.
     */
    ITexturePos getTextureOverlayPos();

    /**
     * Size of the model the texture is used on.
     *
     * @return
     */
    IVector3i getTextureModelSize();

}
