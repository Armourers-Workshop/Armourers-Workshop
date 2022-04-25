package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.core.entity.EntityProfiles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class ModCompatible {

    private static EntityType<?> customNPCEntityType;

    public static void registerCustomEntityType() {
        // custom npc
        EntityType.byString("customnpcs:customnpc").ifPresent(entityType -> {
            EntityProfiles.register(entityType, EntityProfiles.MANNEQUIN);
            customNPCEntityType = entityType;
        });
    }

    public static Iterable<ItemStack> getArmorSlots(Entity entity) {
        return entity.getArmorSlots();
    }

    public static Iterable<ItemStack> getHandSlots(Entity entity) {
        // Noppes:
        // I disabled that, because there was some mod in the past that had a disarm enchantment, which was also disarming my npcs
        // and getHandSlots isnt really used for anything in minecrafts code, so I removed it
        if (entity instanceof LivingEntity && entity.getType() == customNPCEntityType) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return Arrays.asList(livingEntity.getMainHandItem(), livingEntity.getOffhandItem());
        }
        return entity.getHandSlots();
    }
}
