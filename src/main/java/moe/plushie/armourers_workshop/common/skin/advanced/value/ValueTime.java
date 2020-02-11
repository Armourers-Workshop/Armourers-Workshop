package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueRegistry.SkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class ValueTime extends SkinValue {

    public ValueTime() {
        super("time");
    }

    @Override
    public float getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        if (world != null) {
            return (world.provider.getWorldTime() % 24000L);
        }
        return 0;
    }
}
