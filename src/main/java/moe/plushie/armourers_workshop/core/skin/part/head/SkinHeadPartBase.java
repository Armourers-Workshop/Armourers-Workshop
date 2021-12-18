package moe.plushie.armourers_workshop.core.skin.part.head;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Point3D;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;

import java.awt.*;

public class SkinHeadPartBase extends SkinPartType implements ISkinPartTypeTextured {

    public SkinHeadPartBase() {
        super();
        this.buildingSpace = new Rectangle3D(-32, -24, -32, 64, 56, 64);
        this.guideSpace = new Rectangle3D(-4, 0, -4, 8, 8, 8);
        this.offset = new Point3D(0, 0, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        ModelHead.MODEL.render(scale, !SkinProperty.MODEL_HIDE_OVERLAY_HEAD.getValue(skinProps));
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureSkinPos() {
        return new Point(0, 0);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public Point getTextureBasePos() {
        return new Point(0, 0);
    }

    @Override
    public Point getTextureOverlayPos() {
        return new Point(32, 0);
    }

    @Override
    public Point3D getTextureModelSize() {
        return new Point3D(8, 8, 8);
    }

    @Override
    public int getPolygonOffset() {
        return 6;
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(0, 0, 0);
    }

    @Override
    public Rectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(-4, -8, -4, 8, 8, 8);
    }

    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_OVERRIDE_HEAD);
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return skinProps.get(SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
    }

//    @Override
//    public ISkinProperty[] getProperties() {
//        return Arrays.asList(
//                SkinProperty.MODEL_OVERRIDE_HEAD,
//                SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
//    }
}
