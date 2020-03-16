package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinValue;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class SkinValueConstantInt extends AdvancedSkinValue<Integer> {

    public SkinValueConstantInt() {
        super("constant_int");
    }

    @Override
    public Integer getValue(World world, EntityLivingBase entityLivingBase, Skin skin, SkinPart skinPart) {
        return 360;
    }

    @Override
    public Class getType() {
        return Integer.class;
    }
}
