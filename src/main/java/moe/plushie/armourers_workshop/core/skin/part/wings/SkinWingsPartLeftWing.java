package moe.plushie.armourers_workshop.core.skin.part.wings;

import moe.plushie.armourers_workshop.core.api.action.ICanRotation;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Point3D;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;

public class SkinWingsPartLeftWing extends SkinPartType implements ICanRotation {

    public SkinWingsPartLeftWing() {
        super();
        this.buildingSpace = new Rectangle3D(-32, -24, -20, 32, 48, 48);
        this.guideSpace = new Rectangle3D(-4, -12, -4, 8, 12, 4);
        this.offset = new Point3D(0, -1, 2);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, 0, -2 * scale);
//
//        ModelChest.MODEL.renderChest(scale);
//
//        GL11.glTranslated(0, 0, 2 * scale);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public boolean isMirror() {
        return true;
    }

    @Override
    public int getPolygonOffset() {
        return 7;
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(0, 0, 2);
    }

    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }

    @Override
    public int getMinimumMarkersNeeded() {
        return 1;
    }

//    @Override
//    public Collection<ISkinProperty<?>> getProperties() {
//        return Arrays.asList(
//                SkinProperty.WINGS_FLYING_SPEED,
//                SkinProperty.WINGS_IDLE_SPEED,
//                SkinProperty.WINGS_MAX_ANGLE,
//                SkinProperty.WINGS_MIN_ANGLE,
//                SkinProperty.WINGS_MOVMENT_TYPE);
//    }
}
