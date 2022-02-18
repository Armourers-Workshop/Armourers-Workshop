package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SkinValueTime extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueTime() {
        super("time");
    }

    @Override
    public float getValue(World world, Entity entity, Skin skin, SkinPart skinPart) {
        if (world != null) {
//            return (world.provider.getWorldTime() % 24000L);
            return (world.getGameTime() % 24000L);
        }
        return 0F;
    }
}
