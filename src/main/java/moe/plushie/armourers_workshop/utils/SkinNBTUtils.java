package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.common.ISkinNBTUtils;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SkinNBTUtils implements ISkinNBTUtils {

    public static final SkinNBTUtils INSTANCE = new SkinNBTUtils();

    private SkinNBTUtils() {
    }

    @Override
    public void setSkinDescriptor(ItemStack itemStack, ISkinDescriptor skinDescriptor) {
        if (itemStack.isEmpty() | skinDescriptor == null) {
            return;
        }
        SkinNBTHelper.addSkinDataToStack(itemStack, (SkinDescriptor) skinDescriptor);
    }

    @Override
    public ISkinDescriptor getSkinDescriptor(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            return SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
        }
        return null;
    }

    @Override
    public void removeSkinDescriptor(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            SkinNBTHelper.removeSkinDataFromStack(itemStack);
        }
    }

    @Override
    public boolean hasSkinDescriptor(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        return SkinNBTHelper.stackHasSkinData(itemStack);
    }

    @Override
    public void setSkinDescriptor(NBTTagCompound compound, ISkinDescriptor skinDescriptor) {
        if (compound.isEmpty() | skinDescriptor == null) {
            return;
        }
        SkinNBTHelper.addSkinDataToStack(compound, (SkinDescriptor) skinDescriptor);
    }

    @Override
    public ISkinDescriptor getSkinDescriptor(NBTTagCompound compound) {
        if (compound == null) {
            return null;
        }
        return SkinNBTHelper.getSkinDescriptork(compound);
    }

    @Override
    public void removeSkinDescriptor(NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        SkinNBTHelper.removeSkinData(compound);
    }

    @Override
    public boolean hasSkinDescriptor(NBTTagCompound compound) {
        if (compound != null) {
            return SkinNBTHelper.compoundHasSkinData(compound);
        }
        return false;
    }
}
