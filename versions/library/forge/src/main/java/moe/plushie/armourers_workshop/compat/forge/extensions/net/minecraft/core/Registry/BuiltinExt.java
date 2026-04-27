package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.core.Registry;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeRegistry;
import moe.plushie.armourers_workshop.core.utils.TypedProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[16, )")
@Extension
public class BuiltinExt {

    public static TypedProvider<Item> createItemRegistryFO(@ThisClass Class<?> clazz) {
        return AbstractForgeRegistry.from(BuiltInRegistriesExt.ITEM);
    }

    public static TypedProvider<Block> createBlockRegistryFO(@ThisClass Class<?> clazz) {
        return AbstractForgeRegistry.from(BuiltInRegistriesExt.BLOCK);
    }

    public static TypedProvider<IMenuType<?>> createMenuTypeRegistryFO(@ThisClass Class<?> clazz) {
        return AbstractForgeRegistry.from(BuiltInRegistriesExt.MENU).map(Supplier::get);
    }

    public static TypedProvider<SoundEvent> createSoundEventRegistryFO(@ThisClass Class<?> clazz) {
        return AbstractForgeRegistry.from(BuiltInRegistriesExt.SOUND_EVENT);
    }
}
