package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SkinValueTime extends AdvancedSkinValue<Integer> {

    public SkinValueTime() {
        super("time");
    }

    @Override
    public Integer getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        if (world != null) {
            Integer.valueOf((int) (world.provider.getWorldTime() % 24000L));
        }
        return Integer.valueOf(0);
    }
    
    @Override
    public Class getType() {
        return Integer.class;
    }
}
