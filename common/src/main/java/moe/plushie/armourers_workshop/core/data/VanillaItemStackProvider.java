package moe.plushie.armourers_workshop.core.data;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.common.IItemStackProvider;
import moe.plushie.armourers_workshop.utils.LazyValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class VanillaItemStackProvider implements IItemStackProvider {

    private final LazyValue<EntityType<?>> customNPCEntityType = LazyValue.of(() -> EntityType.byString("customnpcs:customnpc").orElse(null));

    @Override
    public Iterable<ItemStack> getArmorSlots(Entity entity) {
        return entity.getArmorSlots();
    }

    @Override
    public Iterable<ItemStack> getHandSlots(Entity entity) {
        // Noppes:
        // I disabled that, because there was some mod in the past that had a disarm enchantment, which was also disarming my npcs
        // and getHandSlots isnt really used for anything in minecrafts code, so I removed it
        if (entity instanceof LivingEntity && entity.getType() == customNPCEntityType.get()) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return Lists.newArrayList(livingEntity.getMainHandItem(), livingEntity.getOffhandItem());
        }
        return entity.getHandSlots();
    }
}
