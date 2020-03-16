package moe.plushie.armourers_workshop.common.skin.advanced.action;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPart;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinAction;
import net.minecraft.util.EnumFacing;

public class SkinActionRotate extends AdvancedSkinAction {

    public SkinActionRotate() {
        super("rotate");
    }

    @Override
    public void trigger(Object... data) {
        AdvancedPart advancedPart = (AdvancedPart) data[0];
        EnumFacing.Axis axis = (EnumFacing.Axis) data[1];
        Float angle = (Float) data[2];

        switch (axis) {
        case X:
            advancedPart.setRotationAngleOffset(angle.doubleValue(), 0D, 0D);
            break;
        case Y:
            advancedPart.setRotationAngleOffset(0D, angle.doubleValue(), 0D);
            break;
        case Z:
            advancedPart.setRotationAngleOffset(0D, 0D, angle.doubleValue());
            break;
        }
    }

    @Override
    public int getInputCount() {
        return 3;
    }

    @Override
    public Class getInputType(int index) {
        if (index == 0) {
            return AdvancedPart.class;
        }
        if (index == 1) {
            return EnumFacing.Axis.class;
        }
        if (index == 2) {
            return Float.class;
        }
        return Void.class;
    }
}
