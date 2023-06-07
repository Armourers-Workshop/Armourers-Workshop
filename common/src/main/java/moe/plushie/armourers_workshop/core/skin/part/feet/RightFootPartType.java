package moe.plushie.armourers_workshop.core.skin.part.feet;

import moe.plushie.armourers_workshop.api.math.ITexturePos;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class RightFootPartType extends SkinPartType implements ISkinPartTypeTextured {

    public RightFootPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-6, -14, -16, 24, 10, 32);
        this.guideSpace = new Rectangle3i(-2, -12, -2, 4, 12, 4);
        this.offset = new Vector3i(-9, 0, 0);
        this.renderOffset = new Vector3i(-2, 12, 0);
        this.renderPolygonOffset = 4;
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public ITexturePos getTextureSkinPos() {
        return new TexturePos(0, 16);
    }

    @Override
    public ITexturePos getTextureBasePos() {
        return new TexturePos(0, 16);
    }

    @Override
    public ITexturePos getTextureOverlayPos() {
        return new TexturePos(0, 32);
    }

    @Override
    public IVector3i getTextureModelSize() {
        return new Vector3i(4, 12, 4);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_OVERRIDE_LEG_RIGHT);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_LEG_RIGHT);
    }
}
