package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SkinValueTime extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueTime() {
        super("time");
    }

    @Override
    public float getValue(Level level, Entity entity, Skin skin, SkinPart skinPart) {
        if (level != null) {
//            return (world.provider.getWorldTime() % 24000L);
            return (level.getGameTime() % 24000L);
        }
        return 0F;
    }
}
