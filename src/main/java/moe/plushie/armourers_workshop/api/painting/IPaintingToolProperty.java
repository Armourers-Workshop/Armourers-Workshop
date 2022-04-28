package moe.plushie.armourers_workshop.api.painting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public interface IPaintingToolProperty<T> {

    String getName();

    T empty();

    T get(CompoundNBT nbt);

    void set(CompoundNBT nbt, T value);

    default T get(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getTag();
        if (nbt != null && nbt.contains("Options", Constants.NBT.TAG_COMPOUND)) {
            return get(nbt.getCompound("Options"));
        }
        return empty();
    }

    default void setValue(ItemStack itemStack, T value) {
        CompoundNBT newNBT = null;
        CompoundNBT nbt = itemStack.getTag();
        if (nbt != null) {
            newNBT = nbt.getCompound("Options");
        }
        if (newNBT == null) {
            newNBT = new CompoundNBT();
        }
        set(newNBT, value);
        if (newNBT.size() != 0) {
            itemStack.getOrCreateTag().put("Options", newNBT);
        } else if (nbt != null) {
            nbt.remove("Options");
        }
    }
//    @Deprecated
//    public boolean getToolHasColour(ItemStack stack);
//
//    int getToolColour(ItemStack stack);
//
//    void setToolColour(ItemStack stack, int colour);
//
//    void setToolPaintType(ItemStack stack, ISkinPaintType paintType);
//
//    ISkinPaintType getToolPaintType(ItemStack stack);
}
