package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.init.items.ItemArmourContainerItem;
import moe.plushie.armourers_workshop.common.init.items.ItemSkinTemplate;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SkinNBTHelper {

    public static boolean stackHasSkinData(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }
        if (!stack.hasTagCompound()) {
            return false;
        }

        NBTTagCompound itemCompound = stack.getTagCompound();
        if (!itemCompound.hasKey(SkinDescriptor.TAG_SKIN_DATA)) {
            return false;
        }

        return true;
    }

    public static boolean compoundHasSkinData(NBTTagCompound compound) {
        if (compound == null) {
            return false;
        }
        if (!compound.hasKey(SkinDescriptor.TAG_SKIN_DATA)) {
            return false;
        }
        return true;
    }

    public static void removeSkinDataFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return;
        }
        NBTTagCompound itemCompound = stack.getTagCompound();
        if (itemCompound.hasKey(SkinDescriptor.TAG_SKIN_DATA)) {
            itemCompound.removeTag(SkinDescriptor.TAG_SKIN_DATA);
        }
    }

    public static void removeSkinData(NBTTagCompound compound) {
        if (!compoundHasSkinData(compound)) {
            return;
        }
        if (compound.hasKey(SkinDescriptor.TAG_SKIN_DATA)) {
            compound.removeTag(SkinDescriptor.TAG_SKIN_DATA);
        }
    }

    public static SkinDescriptor getSkinDescriptorFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return null;
        }

        SkinDescriptor skinData = new SkinDescriptor();
        skinData.readFromCompound(stack.getTagCompound());

        return skinData;
    }

    public static SkinDescriptor getSkinDescriptork(NBTTagCompound compound) {
        if (!compoundHasSkinData(compound)) {
            return null;
        }

        SkinDescriptor skinData = new SkinDescriptor();
        skinData.readFromCompound(compound);

        return skinData;
    }

    public static ISkinType getSkinTypeFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return null;
        }

        SkinDescriptor skinData = new SkinDescriptor();
        skinData.readFromCompound(stack.getTagCompound());

        return skinData.getIdentifier().getSkinType();
    }

    @Deprecated
    public static void addSkinDataToStack(ItemStack stack, ISkinIdentifier identifier, ISkinDye skinDye) {
        SkinDescriptor skinData = new SkinDescriptor(identifier, skinDye);
        addSkinDataToStack(stack, skinData);
    }

    public static void addSkinDataToStack(ItemStack stack, SkinDescriptor descriptor) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        descriptor.writeToCompound(stack.getTagCompound());
    }

    public static void addSkinDataToStack(NBTTagCompound compound, SkinDescriptor descriptor) {
        descriptor.writeToCompound(compound);
    }

    public static ItemStack makeEquipmentSkinStack(SkinDescriptor descriptor) {
        return ((ItemSkinTemplate) ModItems.SKIN_TEMPLATE).makeSkinStack(descriptor);
    }

    public static ItemStack makeArmouerContainerStack(SkinDescriptor descriptor) {
        return ((ItemArmourContainerItem) ModItems.ARMOUR_CONTAINER_ITEM).makeSkinStack(descriptor);
    }
}
