package moe.plushie.armourers_workshop.common.skin.advanced.trigger;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinTrigger;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SkinTriggerAlways extends AdvancedSkinTrigger {

    public SkinTriggerAlways() {
        super("always");
    }
    
    @Override
    public boolean canTrigger(World world, Entity entity, Skin skin, SkinPart skinPart) {
        return true;
    }
}
