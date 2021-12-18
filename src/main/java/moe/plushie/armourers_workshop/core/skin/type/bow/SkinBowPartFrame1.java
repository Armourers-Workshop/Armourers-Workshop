package moe.plushie.armourers_workshop.core.skin.type.bow;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;

public class SkinBowPartFrame1 extends AbstractSkinPartType {

    public SkinBowPartFrame1() {
        super();
        this.buildingSpace = new Rectangle3D(-10, -20, -46, 20, 62, 64);
        this.guideSpace = new Rectangle3D(-2, -2, 2, 4, 4, 8);
        this.offset = new Point3D(0, 0, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        ModelHand.MODEL.render(scale);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public int getMinimumMarkersNeeded() {
        return 1;
    }

    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }

    @Override
    public boolean isPartRequired() {
        return true;
    }
}
