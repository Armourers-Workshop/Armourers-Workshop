package moe.plushie.armourers_workshop.common.skin.advanced.action;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPart;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinAction;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SkinActionRotate extends AdvancedSkinAction {

    public SkinActionRotate() {
        super("rotate");
    }

    @Override
    public void trigger(World world, Entity entity, Skin skin, float... data) {
        if (data.length < getInputs().length) {
            return;
        }

        AdvancedPart advancedPart = skin.getAdvancedPart(Math.round(data[0]));
        EnumFacing.Axis axis = EnumFacing.Axis.values()[MathHelper.clamp(Math.round(data[1]), 0, EnumFacing.Axis.values().length)];
        float angle = data[2];

        switch (axis) {
        case X:
            advancedPart.setRotationAngleOffset(angle, 0D, 0D);
            break;
        case Y:
            advancedPart.setRotationAngleOffset(0D, angle, 0D);
            break;
        case Z:
            advancedPart.setRotationAngleOffset(0D, 0D, angle);
            break;
        }
    }

    @Override
    public String[] getInputs() {
        return new String[] { "part", "axis", "angle" };
    }
}
