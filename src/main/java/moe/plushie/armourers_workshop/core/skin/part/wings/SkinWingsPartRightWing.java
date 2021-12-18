package moe.plushie.armourers_workshop.core.skin.part.wings;

import moe.plushie.armourers_workshop.core.api.action.ICanRotation;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Point3D;
import moe.plushie.armourers_workshop.core.utils.Rectangle3D;

public class SkinWingsPartRightWing extends SkinPartType implements ICanRotation {

    public SkinWingsPartRightWing() {
        super();
        this.buildingSpace = new Rectangle3D(0, -24, -20, 32, 48, 48);
        this.guideSpace = new Rectangle3D(-4, -12, -4, 8, 12, 4);
        this.offset = new Point3D(0, -1, 2);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
        //GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        //GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        //ModelChest.MODEL.renderChest(scale);
        //GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        //GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
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

}
