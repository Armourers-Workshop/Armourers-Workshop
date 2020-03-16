package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SkinValueHealthPercentage extends AdvancedSkinValue<Float> {

    public SkinValueHealthPercentage() {
        super("health_percentage");
    }

    @Override
    public Float getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        if (entityLivingBase != null) {
            return Float.valueOf(entityLivingBase.getHealth() / entityLivingBase.getMaxHealth());
        }
        return Float.valueOf(0F);
    }

    @Override
    public Class getType() {
        return Float.class;
    }
}
