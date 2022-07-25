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

public class LeftArmPartType extends SkinPartType implements ISkinPartTypeTextured {

    public LeftArmPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-19, -22, -16, 24, 38, 32);
        this.guideSpace = new Rectangle3i(-3, -10, -2, 4, 12, 4);
        this.offset = new Vector3i(18, -1, 0);
    }

    @Override
    public boolean isTextureMirrored() {
        return true;
    }

    @Override
    public ITexturePos getTextureSkinPos() {
        return new TexturePos(40, 16);
    }

    @Override
    public ITexturePos getTextureBasePos() {
        return new TexturePos(32, 48);
    }

    @Override
    public ITexturePos getTextureOverlayPos() {
        return new TexturePos(48, 48);
    }

    @Override
    public IVector3i getTextureModelSize() {
        return new Vector3i(4, 12, 4);
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(5, 2, 0);
    }

    @Override
    public float getRenderPolygonOffset() {
        return 4;
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_OVERRIDE_ARM_LEFT);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT);
    }
}
