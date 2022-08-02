package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SkinValueRaining extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueRaining() {
        super("raining");
    }

    @Override
    public float getValue(Level level, Entity entity, Skin skin, SkinPart skinPart) {
        if (level != null) {
            if (level.isRaining()) {
                return 1F;
            }
        }
        return 0F;
    }
}
