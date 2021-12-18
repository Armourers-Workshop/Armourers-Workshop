package moe.plushie.armourers_workshop.core.skin.type.item;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;

public class SkinItemPartBase extends AbstractSkinPartType {

    public SkinItemPartBase() {
        super();
        this.buildingSpace = new Rectangle3D(-10, -20, -28, 20, 62, 56);
        this.guideSpace = new Rectangle3D(-2, -2, 2, 4, 4, 8);
        //Offset -1 to match old skin system.
        this.offset = new Point3D(0, -1, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        ModelHand.MODEL.render(scale);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
}
