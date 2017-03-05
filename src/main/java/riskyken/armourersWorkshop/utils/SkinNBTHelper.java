package riskyken.armourersWorkshop.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

public class SkinNBTHelper {
    
    private static final String TAG_OLD_SKIN_DATA = "armourData";
    private static final String TAG_OLD_SKIN_ID = "equpmentId";
    
    public static boolean stackHasSkinData(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        
        if (!stack.hasTagCompound()) {
            return false;
        }
        
        NBTTagCompound itemCompound = stack.getTagCompound();
        if (!itemCompound.hasKey(SkinPointer.TAG_SKIN_DATA)) {
            return false;
        }
        
        return true;
    }
    
    public static boolean compoundHasSkinData(NBTTagCompound compound) {
        if (compound == null) {
            return false;
        }
        if (!compound.hasKey(SkinPointer.TAG_SKIN_DATA)) {
            return false;
        }
        return true;
    }
    
    public static void removeSkinDataFromStack(ItemStack stack, boolean overrideLock) {
        if (!stackHasSkinData(stack)) {
            return;
        }
        
        SkinPointer skinData = getSkinPointerFromStack(stack);
        if (skinData.lockSkin) {
            if (!overrideLock) {
                return;
            }
        }
        
        NBTTagCompound itemCompound = stack.getTagCompound();
        if (itemCompound.hasKey(SkinPointer.TAG_SKIN_DATA)) {
            itemCompound.removeTag(SkinPointer.TAG_SKIN_DATA);
        }
    }
    
    public static SkinPointer getSkinPointerFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return null;
        }
        
        SkinPointer skinData = new SkinPointer();
        skinData.readFromCompound(stack.getTagCompound());
        
        return skinData;
    }
    
    public static ISkinType getSkinTypeFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return null;
        }
        
        SkinPointer skinData = new SkinPointer();
        skinData.readFromCompound(stack.getTagCompound());
        
        return skinData.skinType;
    }
    
    public static int getSkinIdFromStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return -1;
        }
        
        SkinPointer skinData = new SkinPointer();
        skinData.readFromCompound(stack.getTagCompound());
        
        return skinData.skinId;
    }
    
    public static boolean isSkinLockedOnStack(ItemStack stack) {
        if (!stackHasSkinData(stack)) {
            return false;
        }
        
        SkinPointer skinData = new SkinPointer();
        skinData.readFromCompound(stack.getTagCompound());
        
        return skinData.lockSkin;
    }
    
    public static void addSkinDataToStack(ItemStack stack, ISkinType skinType, int skinId, ISkinDye skinDye, boolean lockSkin) {
        SkinPointer skinData = new SkinPointer(skinType, skinId, skinDye, lockSkin);
        addSkinDataToStack(stack, skinData);
    }
    
    public static void addSkinDataToStack(ItemStack stack, ISkinType skinType, int skinId, boolean lockSkin, ISkinDye skinDye) {
        SkinPointer skinData;
        if (skinDye != null) {
            skinData = new SkinPointer(skinType, skinId, skinDye, lockSkin);
        } else {
            skinData = new SkinPointer(skinType, skinId, lockSkin);
        }
        addSkinDataToStack(stack, skinData);
    }
    
    public static void addSkinDataToStack(ItemStack stack, SkinPointer skinPointer) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        skinPointer.writeToCompound(stack.getTagCompound());
    }
    
    public static boolean stackHasLegacySkinData(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        
        if (!stack.hasTagCompound()) {
            return false;
        }
        
        NBTTagCompound itemCompound = stack.getTagCompound();
        if (!itemCompound.hasKey(TAG_OLD_SKIN_DATA)) {
            return false;
        }
        
        NBTTagCompound skinDataCompound = itemCompound.getCompoundTag(TAG_OLD_SKIN_DATA);
        if (!skinDataCompound.hasKey(TAG_OLD_SKIN_ID)) {
            return false;
        }
        
        return true;
    }
    
    public static int getLegacyIdFromStack(ItemStack stack) {
        if (stack == null) {
            return -1;
        }
        if (!stack.hasTagCompound()) {
            return -1;
        }
        
        NBTTagCompound itemCompound = stack.getTagCompound();
        if (!itemCompound.hasKey(TAG_OLD_SKIN_DATA)) {
            return -1;
        }
        
        NBTTagCompound skinDataCompound = itemCompound.getCompoundTag(TAG_OLD_SKIN_DATA);
        if (!skinDataCompound.hasKey(TAG_OLD_SKIN_ID)) {
            return -1;
        }
        
        return skinDataCompound.getInteger(TAG_OLD_SKIN_ID);
    }
    
    public static ItemStack makeEquipmentSkinStack(Skin equipmentItemData) {
        ItemStack stack = new ItemStack(ModItems.equipmentSkin, 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, equipmentItemData.getSkinType(), equipmentItemData.hashCode(), false, null);
        return stack;
    }
    
    public static ItemStack makeEquipmentSkinStack(SkinPointer skinPointer) {
        ItemStack stack = new ItemStack(ModItems.equipmentSkin, 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, skinPointer.getSkinType(), skinPointer.getSkinId(), false, new SkinDye(skinPointer.getSkinDye()));
        return stack;
    }
    
    public static ItemStack makeArmouerContainerStack(Skin equipmentItemData) {
        ItemStack stack = new ItemStack(ModItems.armourContainer[equipmentItemData.getSkinType().getVanillaArmourSlotId()], 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, equipmentItemData.getSkinType(), equipmentItemData.hashCode(), false, null);
        return stack;
    }
    
    public static ItemStack makeArmouerContainerStack(SkinPointer skinPointer) {
        ItemStack stack = new ItemStack(ModItems.armourContainer[skinPointer.getSkinType().getVanillaArmourSlotId()], 1);
        stack.setTagCompound(new NBTTagCompound());
        addSkinDataToStack(stack, skinPointer.getSkinType(), skinPointer.skinId, false, new SkinDye(skinPointer.getSkinDye()));
        return stack;
    }
    
    public static void addRenderIdToStack(ItemStack stack, ISkinType skinType, int skinId, ISkinDye skinDye) {
        if (stackHasSkinData(stack)) {
            SkinPointer skinData = getSkinPointerFromStack(stack);
            if (!skinData.lockSkin) {
                if (skinData.skinId != skinId | !skinData.skinDye.equals(skinDye)) {
                    addSkinDataToStack(stack, skinType, skinId, skinDye, false);
                }
            }
        } else {
            addSkinDataToStack(stack, skinType, skinId, skinDye, false);
        }
    }
    
    public static void removeRenderIdFromStack(ItemStack stack) {
        removeSkinDataFromStack(stack, false);
    }
    

}
