package moe.plushie.armourers_workshop.core.skin.part.advanced.trigger;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SkinTriggerAlways extends AdvancedSkinRegistry.AdvancedSkinTrigger {

    public SkinTriggerAlways() {
        super("always");
    }

    @Override
    public boolean canTrigger(Level world, Entity entity, Skin skin, SkinPart skinPart) {
        return true;
    }
}
