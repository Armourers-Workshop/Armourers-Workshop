package moe.plushie.armourers_workshop.core.skin.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SkinValueInWater extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueInWater() {
        super("in_water");
    }

    @Override
    public float getValue(World world, Entity entity, Skin skin, SkinPart skinPart) {
        if (entity != null) {
            if (entity.isInWater()) {
                return 1F;
            }
        }
        return 0;
    }
}
