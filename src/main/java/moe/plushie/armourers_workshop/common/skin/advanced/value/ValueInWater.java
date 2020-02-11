package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueRegistry.SkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class ValueInWater extends SkinValue {

    public ValueInWater() {
        super("in_water");
    }

    @Override
    public float getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        if (entityLivingBase != null) {
            if (entityLivingBase.isInWater()) {
                return 1F;
            }
        }
        return 0F;
    }
}
