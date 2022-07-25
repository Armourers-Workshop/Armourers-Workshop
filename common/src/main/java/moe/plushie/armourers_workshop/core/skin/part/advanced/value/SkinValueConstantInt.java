package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SkinValueConstantInt extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueConstantInt() {
        super("constant_int");
    }

    @Override
    public float getValue(Level level, Entity entity, Skin skin, SkinPart skinPart) {
        return 360;
    }
}
