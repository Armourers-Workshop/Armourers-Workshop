package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.value.SkinValueRegistry.SkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class ValueHealth extends SkinValue {

    public ValueHealth() {
        super("health");
    }

    @Override
    public float getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        if (entityLivingBase != null) {
            return entityLivingBase.getHealth() / entityLivingBase.getMaxHealth();
        }
        return 0F;
    }
}
