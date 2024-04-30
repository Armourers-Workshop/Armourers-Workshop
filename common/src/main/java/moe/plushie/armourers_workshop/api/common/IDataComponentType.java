package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IDataComponentType<T> {

    boolean has(ItemStack itemStack);

    @Nullable
    T get(ItemStack itemStack);

    T getOrDefault(ItemStack itemStack, T defaultValue);

    void set(ItemStack itemStack, @Nullable T value);

    void remove(ItemStack itemStack);
}
