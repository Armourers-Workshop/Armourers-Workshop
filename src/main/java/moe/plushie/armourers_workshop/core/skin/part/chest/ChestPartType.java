package moe.plushie.armourers_workshop.core.skin.part.chest;

import moe.plushie.armourers_workshop.api.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

import java.awt.*;

public class ChestPartType extends SkinPartType implements ISkinPartTypeTextured {

    public ChestPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-12, -24, -32, 24, 38, 64);
        this.guideSpace = new Rectangle3i(-4, -12, -2, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 0);
    }

    @Override
    public Point getTextureSkinPos() {
        return new Point(16, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public Point getTextureBasePos() {
        return new Point(16, 16);
    }

    @Override
    public Point getTextureOverlayPos() {
        return new Point(16, 32);
    }

    @Override
    public Vector3i getTextureModelSize() {
        return new Vector3i(8, 12, 4);
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 0, 0);
    }

    @Override
    public float getRenderPolygonOffset() {
        return -0.01f;
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
