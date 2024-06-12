package moe.plushie.armourers_workshop.core.skin.part.bow;

import com.google.common.collect.Range;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;

public class BowPartType extends SkinPartType implements ICanHeld, ICanUse {

    private final int frame;
    private final Range<Integer> useRange;

    public BowPartType(int frame) {
        super();
        this.frame = frame;
        this.buildingSpace = new Rectangle3i(-12, -42, -46, 24, 84, 64);
        this.guideSpace = new Rectangle3i(-2, -2, 2, 4, 4, 8);
        this.offset = getFrameOffset(frame);
        this.useRange = getFrameUseRange(frame);
    }

    public static Range<Integer> getFrameUseRange(int frame) {
        // (float)(p_239429_0_.getUseDuration() - p_239429_2_.getUseItemRemainingTicks()) / 20.0F;
        // pulling: 1, 0
        // pulling: 1, 0.65
        // pulling: 1, 0.9
        return switch (frame) {
            case 0 -> Range.closed(0, 0);
            case 1 -> Range.closed(1, 12);
            case 2 -> Range.closed(13, 17);
            default -> Range.closed(18, 30);
        };
    }

    public static Vector3i getFrameOffset(int frame) {
        return switch (frame) {
            case 0 -> new Vector3i(-50, 0, 0);
            case 1 -> new Vector3i(-25, 0, 0);
            case 2 -> new Vector3i(0, 0, 0);
            case 3 -> new Vector3i(25, 0, 0);
            default -> null;
        };
    }

    @Override
    public Range<Integer> getUseRange() {
        return useRange;
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
        // frame 0 is not required.
        return frame != 0;
    }
}
