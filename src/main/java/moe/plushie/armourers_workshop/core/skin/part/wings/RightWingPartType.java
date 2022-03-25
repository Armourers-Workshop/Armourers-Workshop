package moe.plushie.armourers_workshop.core.skin.part.wings;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.api.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

public class RightWingPartType extends SkinPartType implements ICanRotation {

    public RightWingPartType() {
        super();
        this.buildingSpace = new Rectangle3i(0, -24, -20, 32, 48, 48);
        this.guideSpace = new Rectangle3i(-4, -12, -4, 8, 12, 4);
        this.offset = new Vector3i(0, -1, 2);
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

}
