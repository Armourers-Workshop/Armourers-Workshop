package moe.plushie.armourers_workshop.core.skin.type.bow;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import org.lwjgl.opengl.GL11;

public class SkinBowPartArrow extends AbstractSkinPartType {

    public SkinBowPartArrow() {
        super();
        this.buildingSpace = new Rectangle3D(-5, -5, -2, 11, 11, 16);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, 0, 21);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        if (showHelper) {
            //ModelArrow.MODEL.render(scale, true);
        }
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
}
