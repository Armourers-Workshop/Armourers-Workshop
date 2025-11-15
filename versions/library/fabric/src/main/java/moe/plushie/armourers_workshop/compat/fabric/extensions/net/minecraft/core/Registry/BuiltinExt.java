package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.fabric.AbstractFabricRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.16, )")
@Extension
public class BuiltinExt {

    public static TypedProvider<Item> createItemRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.ITEM);
    }

    public static TypedProvider<Block> createBlockRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.BLOCK);
    }

    public static TypedProvider<IMenuType<?>> createMenuTypeRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.MENU).map(Supplier::get);
    }

    public static TypedProvider<SoundEvent> createSoundEventRegistryFA(@ThisClass Class<?> clazz) {
        return AbstractFabricRegistry.from(BuiltInRegistriesExt.SOUND_EVENT);
    }
}
