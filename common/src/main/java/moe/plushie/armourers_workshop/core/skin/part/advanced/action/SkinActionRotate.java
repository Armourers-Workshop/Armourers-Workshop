package moe.plushie.armourers_workshop.core.skin.part.advanced.action;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SkinActionRotate extends AdvancedSkinRegistry.AdvancedSkinAction {

    public SkinActionRotate() {
        super("rotate");
    }

    @Override
    public void trigger(Level level, Entity entity, Skin skin, float... data) {
        if (data.length < getInputs().length) {
            return;
        }

        // TODO:
//        AdvancedPart advancedPart = skin.getAdvancedPart(Math.round(data[0]));
//        EnumFacing.Axis axis = EnumFacing.Axis.values()[MathHelper.clamp(Math.round(data[1]), 0, EnumFacing.Axis.values().length)];
//        float angle = data[2];
//
//        switch (axis) {
//        case X:
//            advancedPart.setRotationAngleOffset(angle, 0D, 0D);
//            break;
//        case Y:
//            advancedPart.setRotationAngleOffset(0D, angle, 0D);
//            break;
//        case Z:
//            advancedPart.setRotationAngleOffset(0D, 0D, angle);
//            break;
//        }
    }

    @Override
    public String[] getInputs() {
        return new String[]{"part", "axis", "angle"};
    }
}
