package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinMathValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SkinValueMathMultiply extends AdvancedSkinMathValue {

    public SkinValueMathMultiply() {
        super("multiply");
    }

    @Override
    public float getValue(World world, Entity entity, Skin skin, float... data) {
        if (data.length < getInputs().length) {
            return 0F;
        }
        return data[0] * data[1];
    }

    @Override
    public String[] getInputs() {
        return new String[] { "value_1", "value_2" };
    }
}
