package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IRegistry;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Supplier;

public abstract class AbstractForgeRegistries extends ForgeRegistries {

    public static final IForgeRegistry<MenuType<?>> MENU_TYPES = ForgeRegistries.CONTAINERS;
    public static final IForgeRegistry<EntityType<?>> ENTITY_TYPES = ForgeRegistries.ENTITIES;
    public static final IForgeRegistry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = ForgeRegistries.BLOCK_ENTITIES;

    public static <T extends IForgeRegistryEntry<T>> IRegistry<T> wrap(IForgeRegistry<T> registry) {
        DeferredRegister<T> registry1 = DeferredRegister.create(registry, ModConstants.MOD_ID);
        registry1.register(FMLJavaModLoadingContext.get().getModEventBus());
        return new IRegistry<T>() {
            @Override
            public ResourceLocation getKey(T object) {
                return registry.getKey(object);
            }

            @Override
            public T getValue(ResourceLocation registryName) {
                return registry.getValue(registryName);
            }

            @Override
            public <I extends T> Supplier<I> register(String name, Supplier<? extends I> provider) {
                return registry1.register(name, provider);
            }
        };
    }

    public static boolean isModBusEvent(Class<?> clazz) {
        return IModBusEvent.class.isAssignableFrom(clazz);
    }
}
