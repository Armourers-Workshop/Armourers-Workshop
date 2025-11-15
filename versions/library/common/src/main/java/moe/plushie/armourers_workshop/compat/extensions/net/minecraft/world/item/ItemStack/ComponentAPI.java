package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.item.ItemStack;

import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class ComponentAPI {

    public static <T> boolean has(@This ItemStack stack, IDataComponentType<? super T> key) {
        return key.has(stack);
    }

    public static <T> void set(@This ItemStack stack, IDataComponentType<? super T> key, @Nullable T value) {
        key.set(stack, value);
    }

    @Nullable
    public static <T> T get(@This ItemStack stack, IDataComponentType<? super T> key) {
        return Objects.unsafeCast(key.get(stack));
    }

    public static <T> T getOrDefault(@This ItemStack stack, IDataComponentType<? super T> key, T defaultValue) {
        return Objects.unsafeCast(key.getOrDefault(stack, defaultValue));
    }

    public static <T> void remove(@This ItemStack stack, IDataComponentType<? super T> key) {
        key.remove(stack);
    }
}
