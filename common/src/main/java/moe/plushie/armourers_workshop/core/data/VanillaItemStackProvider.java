package moe.plushie.armourers_workshop.core.data;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import moe.plushie.armourers_workshop.utils.LazyValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;

public class VanillaItemStackProvider implements IItemStackProvider {

    private final LazyValue<EntityType<?>> customNPCEntityType = LazyValue.of(() -> EntityType.byString("customnpcs:customnpc").orElse(null));

    @Override
    public Iterable<ItemStack> getArmorSlots(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.getArmorSlots();
        }
        return Collections.emptyList();
    }

    @Override
    public Iterable<ItemStack> getHandSlots(Entity entity) {
        // Noppes:
        // I disabled that, because there was some mod in the past that had a disarm enchantment, which was also disarming my npcs
        // and getHandSlots isnt really used for anything in minecrafts code, so I removed it
        if (entity instanceof LivingEntity livingEntity && entity.getType() == customNPCEntityType.get()) {
            return Lists.newArrayList(livingEntity.getMainHandItem(), livingEntity.getOffhandItem());
        }
        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity.getHandSlots();
        }
        return Collections.emptyList();
    }
}
