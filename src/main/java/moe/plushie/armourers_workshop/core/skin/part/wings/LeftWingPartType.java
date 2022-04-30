package moe.plushie.armourers_workshop.core.skin.part.wings;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.api.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

public class LeftWingPartType extends SkinPartType implements ICanRotation {

    public LeftWingPartType() {
        super();
        this.buildingSpace = new Rectangle3i(-32, -24, -20, 32, 48, 48);
        this.guideSpace = new Rectangle3i(-4, -12, -4, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 2);
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
    public Vector3i getRenderOffset() {
        return new Vector3i(0, 0, 2);
    }

    @Override
    public float getRenderPolygonOffset() {
        return -0.1f;
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
