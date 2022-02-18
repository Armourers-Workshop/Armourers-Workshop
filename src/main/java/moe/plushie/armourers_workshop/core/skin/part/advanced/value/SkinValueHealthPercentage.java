package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class SkinValueHealthPercentage extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueHealthPercentage() {
        super("health_percentage");
    }

    @Override
    public float getValue(World world, Entity entity, Skin skin, SkinPart skinPart) {
        if (entity != null && entity instanceof LivingEntity) {
            LivingEntity entityLivingBase = (LivingEntity) entity;
            return entityLivingBase.getHealth() / entityLivingBase.getMaxHealth();
        }
        return 0F;
    }
}