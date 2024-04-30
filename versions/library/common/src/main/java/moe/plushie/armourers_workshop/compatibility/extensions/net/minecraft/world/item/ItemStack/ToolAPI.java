package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.item.ItemStack;

import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class ToolAPI {

    public static <T> void set(@This ItemStack stack, IConfigurableToolProperty<? super T> key, @Nullable T value) {
        CompoundTag tag = stack.get(ModDataComponents.TOOL_OPTIONS.get());
        if (tag != null) {
            tag = tag.copy();
        } else {
            tag = new CompoundTag();
        }
        key.set(tag, value);
        if (tag.isEmpty()) {
            stack.remove(ModDataComponents.TOOL_OPTIONS.get());
        } else {
            stack.set(ModDataComponents.TOOL_OPTIONS.get(), tag);
        }
    }

    public static <T> T get(@This ItemStack stack, IConfigurableToolProperty<T> key) {
        CompoundTag tag = stack.get(ModDataComponents.TOOL_OPTIONS.get());
        if (tag != null) {
            return key.get(tag);
        }
        return key.empty();
    }
}
