package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
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
    
    public static void removeSkinDataFromStack(ItemStack stack, boolean overrideLock) {
        if (!stackHasSkinData(stack)) {
            return;
        }
        
        SkinDescriptor skinData = getSkinDescriptorFromStack(stack);
        if (skinData.lockSkin) {
            if (!overrideLock) {
                return;
            }
        }
        
        NBTTagCompound itemCompound = stack.getTagCompound();
        if (itemCompound.hasKey(SkinDescriptor.TAG_SKIN_DATA)) {
            itemCompound.removeTag(SkinDescriptor.TAG_SKIN_DATA);
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
    
    public static ISkinType getSkinTypeFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return null;
        }
        
        SkinDescriptor skinData = new SkinDescriptor();
        skinData.readFromCompound(stack.getTagCompound());
        
        return skinData.getIdentifier().getSkinType();
    }
    
    public static int getSkinIdFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return -1;
        }
        
        SkinDescriptor skinData = new SkinDescriptor();
        skinData.readFromCompound(stack.getTagCompound());
        
        return skinData.getIdentifier().getSkinLocalId();
    }
    
    public static boolean isSkinLockedOnStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return false;
        }
        
        SkinDescriptor skinData = new SkinDescriptor();
        skinData.readFromCompound(stack.getTagCompound());
        
        return skinData.lockSkin;
    }
    
    public static void addSkinDataToStack(ItemStack stack, SkinIdentifier identifier, ISkinDye skinDye, boolean lockSkin) {
        SkinDescriptor skinData = new SkinDescriptor(identifier, skinDye, lockSkin);
        addSkinDataToStack(stack, skinData);
    }
    
    public static void addSkinDataToStack(ItemStack stack, SkinIdentifier identifier, boolean lockSkin, ISkinDye skinDye) {
        SkinDescriptor skinData;
        if (skinDye != null) {
            skinData = new SkinDescriptor(identifier, skinDye, lockSkin);
        } else {
            skinData = new SkinDescriptor(identifier, lockSkin);
        }
        addSkinDataToStack(stack, skinData);
    }
    
    public static void addSkinDataToStack(ItemStack stack, SkinDescriptor skinPointer) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        skinPointer.writeToCompound(stack.getTagCompound());
    }
    
    public static ItemStack makeEquipmentSkinStack(Skin skin, ISkinDye skinDye) {
        ItemStack stack = new ItemStack(ModItems.skin, 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, new SkinIdentifier(skin), false, skinDye);
        return stack;
    }
    
    public static ItemStack makeEquipmentSkinStack(Skin skin, SkinIdentifier identifier) {
        ItemStack stack = new ItemStack(ModItems.skin, 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, identifier, false, null);
        return stack;
    }
    
    public static ItemStack makeEquipmentSkinStack(Skin skin) {
        ItemStack stack = new ItemStack(ModItems.skin, 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, new SkinIdentifier(skin), false, null);
        return stack;
    }
    
    public static ItemStack makeEquipmentSkinStack(SkinDescriptor skinDescriptor) {
        ItemStack stack = new ItemStack(ModItems.skin, 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, skinDescriptor.getIdentifier(), false, new SkinDye(skinDescriptor.getSkinDye()));
        return stack;
    }
    
    public static ItemStack makeArmouerContainerStack(Skin skin) {
        ItemStack stack = new ItemStack(ModItems.armourContainer[skin.getSkinType().getVanillaArmourSlotId()], 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, new SkinIdentifier(skin), false, null);
        return stack;
    }
    
    public static ItemStack makeArmouerContainerStack(SkinDescriptor skinPointer) {
        ItemStack stack = new ItemStack(ModItems.armourContainer[skinPointer.getIdentifier().getSkinType().getVanillaArmourSlotId()], 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, skinPointer.getIdentifier(), false, new SkinDye(skinPointer.getSkinDye()));
        return stack;
    }
    
    public static void addSkinPointerToStack(ItemStack stack, SkinDescriptor skinPointer) {
        if (stackHasSkinData(stack)) {
            SkinDescriptor skinData = getSkinDescriptorFromStack(stack);
            if (!skinData.lockSkin) {
                if (!skinData.getIdentifier().equals(skinPointer.getIdentifier()) | !skinData.skinDye.equals(skinPointer.getSkinDye())) {
                    addSkinDataToStack(stack, skinPointer);
                }
            }
        } else {
            addSkinDataToStack(stack, skinPointer);
        }
    }
    
    public static void removeRenderIdFromStack(ItemStack stack) {
        removeSkinDataFromStack(stack, false);
    }
}
