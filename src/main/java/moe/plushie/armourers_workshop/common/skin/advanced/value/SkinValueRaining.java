package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SkinValueRaining extends AdvancedSkinValue<Boolean> {

    public SkinValueRaining() {
        super("raining");
    }

    @Override
    public Boolean getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        if (world != null) {
            return Boolean.valueOf(world.isRaining());
        }
        return Boolean.FALSE;
    }

    @Override
    public Class getType() {
        return Boolean.class;
    }
}
