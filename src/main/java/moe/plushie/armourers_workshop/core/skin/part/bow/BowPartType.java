package moe.plushie.armourers_workshop.core.skin.part.bow;

import com.google.common.collect.Range;
import moe.plushie.armourers_workshop.core.api.action.ICanHeld;
import moe.plushie.armourers_workshop.core.api.action.ICanUse;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.util.math.vector.Vector3i;

public class BowPartType extends SkinPartType implements ICanHeld, ICanUse {

    private final Range<Integer> useRange;

    public BowPartType(int frame) {
        super();
        this.buildingSpace = new Rectangle3i(-10, -20, -46, 20, 62, 64);
        this.guideSpace = new Rectangle3i(-2, -2, 2, 4, 4, 8);
        this.offset = getFrameOffset(frame);
        this.useRange = getFrameUseRange(frame);
    }

    public static Range<Integer> getFrameUseRange(int frame) {
        switch (frame) {
            case 0:
                return Range.closed(0, 12);
            case 1:
                return Range.closed(13, 17);
            default:
                return Range.closed(18, 30);
        }
    }

    public static Vector3i getFrameOffset(int frame) {
        switch (frame) {
            case 0:
                return new Vector3i(-21, 0, 0);
            case 1:
                return new Vector3i(0, 0, 0);
            case 2:
                return new Vector3i(21, 0, 0);
            default:
                return null;
        }
    }

    @Override
    public Range<Integer> getUseRange() {
        return useRange;
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
