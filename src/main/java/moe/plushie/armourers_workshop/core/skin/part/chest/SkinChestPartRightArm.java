package moe.plushie.armourers_workshop.core.skin.part.chest;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Point3D;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;

import java.awt.*;

public class SkinChestPartRightArm extends SkinPartType implements ISkinPartTypeTextured {

    public SkinChestPartRightArm() {
        super();
        this.buildingSpace = new Rectangle3D(-3, -16, -14, 14, 32, 28);
        this.guideSpace = new Rectangle3D(-1, -10, -2, 4, 12, 4);
        this.offset = new Point3D(-10, -7, 0);
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
    public Point3D getTextureModelSize() {
        return new Point3D(4, 12, 4);
    }

    @Override
    public int getPolygonOffset() {
        return 4;
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(-5, 2, 0);
    }

    @Override
    public Rectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(4, 0, -2, 4, 12, 4);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties properties) {
        return properties.get(SkinProperty.MODEL_OVERRIDE_ARM_RIGHT);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties properties) {
        return properties.get(SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT);
    }

//
//    @Override
//    public ISkinProperty[] getProperties() {
//        return Arrays.asList(
//                SkinProperty.MODEL_OVERRIDE_ARM_RIGHT,
//                SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT);
//    }
}
