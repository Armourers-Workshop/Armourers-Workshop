package moe.plushie.armourers_workshop.core.skin.part.chest;

import moe.plushie.armourers_workshop.api.math.ITexturePos;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class ChestPartType extends SkinPartType implements ISkinPartTypeTextured {

    public ChestPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-24, -30, -32, 48, 44, 64);
        this.guideSpace = new Rectangle3i(-4, -12, -2, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 0);
        this.renderOffset = Vector3i.ZERO;
        this.renderPolygonOffset = 1;
    }

    @Override
    public ITexturePos getTextureSkinPos() {
        return new TexturePos(16, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public ITexturePos getTextureBasePos() {
        return new TexturePos(16, 16);
    }

    @Override
    public ITexturePos getTextureOverlayPos() {
        return new TexturePos(16, 32);
    }

    @Override
    public IVector3i getTextureModelSize() {
        return new Vector3i(8, 12, 4);
    }
}
