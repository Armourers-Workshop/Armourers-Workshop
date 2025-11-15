package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class EntityEquipmentManager {

    private static final List<Provider> PROVIDERS = Collections.newList(new Builtin());

    public static void register(Provider provider) {
        PROVIDERS.add(provider);
    }

    public static void getHandSlots(Entity entity, Consumer<ItemStack> handler) {
        for (var provider : PROVIDERS) {
            var slots = provider.getHandSlots(entity);
            if (slots != null) {
                slots.forEach(handler);
            }
        }
    }

    public static void getArmorSlots(Entity entity, Consumer<ItemStack> handler) {
        for (var provider : PROVIDERS) {
            var slots = provider.getArmorSlots(entity);
            if (slots != null) {
                slots.forEach(handler);
            }
        }
    }

    public interface Provider {

        default Iterable<ItemStack> getArmorSlots(Entity entity) {
            return null;
        }

        default Iterable<ItemStack> getHandSlots(Entity entity) {
            return null;
        }
    }

    private static class Builtin implements Provider {

        private final IRegistryHolder<?> customNPCEntityType = AbstractEntityTypeBuilder.lazy("customnpcs:customnpc");

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
                return Collections.newList(livingEntity.getMainHandItem(), livingEntity.getOffhandItem());
            }
            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity.getHandSlots();
            }
            return Collections.emptyList();
        }
    }
}
