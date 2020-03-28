package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class SkinValueInWater extends AdvancedSkinValue {

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
