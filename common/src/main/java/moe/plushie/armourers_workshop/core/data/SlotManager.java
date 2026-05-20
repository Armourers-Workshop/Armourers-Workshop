package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class SlotManager {

    private static final List<Provider> PROVIDERS = Collections.newList(new Builtin());

    public static Collection<? extends Provider> getProviders() {
        return PROVIDERS;
    }

    public static <T extends Entity> void registerHandSlot(Class<T> entityClass, String source, Function<T, ItemStack> provider) {
        registerHandSlots(entityClass, source, it -> Collections.singleton(provider.apply(it)));
    }

    public static <T extends Entity> void registerHandSlots(Class<T> entityClass, String source, Function<T, ? extends Collection<ItemStack>> provider) {
        register(new SlotManager.Provider() {

            @Override
            public String source() {
                return source;
            }

            @Override
            public Iterable<ItemStack> getHandSlots(Entity entity) {
                if (entityClass.isInstance(entity)) {
                    return provider.apply(entityClass.cast(entity));
                }
                return null;
            }
        });
    }

    public static <T extends Entity> void registerArmorSlot(Class<T> entityClass, String source, Function<T, ItemStack> provider) {
        registerArmorSlots(entityClass, source, it -> Collections.singleton(provider.apply(it)));
    }

    public static <T extends Entity> void registerArmorSlots(Class<T> entityClass, String source, Function<T, ? extends Collection<ItemStack>> provider) {
        register(new SlotManager.Provider() {

            @Override
            public String source() {
                return source;
            }

            @Override
            public Iterable<ItemStack> getArmorSlots(Entity entity) {
                if (entityClass.isInstance(entity)) {
                    return provider.apply(entityClass.cast(entity));
                }
                return null;
            }
        });
    }

    public static void register(Provider provider) {
        PROVIDERS.add(provider);
    }

    public interface Provider {

        default String source() {
            return null;
        }

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
