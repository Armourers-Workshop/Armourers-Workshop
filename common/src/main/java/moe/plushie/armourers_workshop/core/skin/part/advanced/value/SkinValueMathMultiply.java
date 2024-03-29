package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SkinValueMathMultiply extends AdvancedSkinRegistry.AdvancedSkinMathValue {

    public SkinValueMathMultiply() {
        super("multiply");
    }

    @Override
    public float getValue(Level level, Entity entity, Skin skin, float... data) {
        if (data.length < getInputs().length) {
            return 0F;
        }
        return data[0] * data[1];
    }

    @Override
    public String[] getInputs() {
        return new String[]{"value_1", "value_2"};
    }
}
