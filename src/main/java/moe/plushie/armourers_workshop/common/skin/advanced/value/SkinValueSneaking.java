package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SkinValueSneaking extends AdvancedSkinValue<Boolean> {

    public SkinValueSneaking() {
        super("sneaking");
    }

    @Override
    public Boolean getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        if (entityLivingBase != null) {
            return Boolean.valueOf(entityLivingBase.isSneaking());
        }
        return Boolean.FALSE;
    }

    @Override
    public Class getType() {
        return Boolean.class;
    }
}
