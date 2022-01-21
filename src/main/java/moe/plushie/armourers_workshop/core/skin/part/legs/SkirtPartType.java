package moe.plushie.armourers_workshop.core.skin.part.legs;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

public class SkirtPartType extends SkinPartType {

    public SkirtPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-10, -12, -10, 20, 15, 20);
        this.guideSpace = new Rectangle3i(-4, -12, -2, 8, 12, 4);
        // offset -1 to match old skin system.
        this.offset = new Vector3i(0, -1, 20);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
//        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(2 * scale, 0, 0);
//        ModelLegs.MODEL.renderLeftLeft(scale);
//        GL11.glTranslated(-4 * scale, 0, 0);
//        ModelLegs.MODEL.renderRightLeg(scale);
//        GL11.glTranslated(2 * scale, 0, 0);
//        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
//        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 12, 0);
    }


//    @Override
//    public ISkinProperty[] getProperties() {
//        return Arrays.asList(SkinProperty.MODEL_LEGS_LIMIT_LIMBS);
//    }
}
