package moe.plushie.armourers_workshop.core.skin.part.chest;

import moe.plushie.armourers_workshop.api.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

import java.awt.*;

public class RightArmPartType extends SkinPartType implements ISkinPartTypeTextured {

    public RightArmPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-3, -16, -14, 14, 32, 28);
        this.guideSpace = new Rectangle3i(-1, -10, -2, 4, 12, 4);
        this.offset = new Vector3i(-10, -7, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        ModelChest.MODEL.renderRightArm(scale);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureSkinPos() {
        return new Point(40, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public Point getTextureBasePos() {
        return new Point(40, 16);
    }

    @Override
    public Point getTextureOverlayPos() {
        return new Point(40, 32);
    }

    @Override
    public Vector3i getTextureModelSize() {
        return new Vector3i(4, 12, 4);
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(-5, 2, 0);
    }

    @Override
    public float getRenderPolygonOffset() {
        return -0.04f;
    }

    @Override
    public boolean isModelOverridden(ISkinProperties properties) {
        return properties.get(SkinProperty.MODEL_OVERRIDE_ARM_RIGHT);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties properties) {
        return properties.get(SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT);
    }
}
