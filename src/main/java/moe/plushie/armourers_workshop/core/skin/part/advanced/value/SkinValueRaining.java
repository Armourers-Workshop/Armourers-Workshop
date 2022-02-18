package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SkinValueRaining extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueRaining() {
        super("raining");
    }

    @Override
    public float getValue(World world, Entity entity, Skin skin, SkinPart skinPart) {
        if (world != null) {
            if (world.isRaining()) {
                return 1F;
            }
        }
        return 0F;
    }
}