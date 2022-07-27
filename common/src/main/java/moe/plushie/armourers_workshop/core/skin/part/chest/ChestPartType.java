package moe.plushie.armourers_workshop.core.skin.part.chest;

import moe.plushie.armourers_workshop.api.math.ITexturePos;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class ChestPartType extends SkinPartType implements ISkinPartTypeTextured {

    public ChestPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-12, -30, -32, 24, 44, 64);
        this.guideSpace = new Rectangle3i(-4, -12, -2, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 0);
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

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 0, 0);
    }

    @Override
    public float getRenderPolygonOffset() {
        return 1;
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_OVERRIDE_CHEST);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_CHEST);
    }
}
