package riskyken.armourersWorkshop.common.handler;

import java.io.InputStream;
import java.util.BitSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.skin.ISkinDataHandler;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.crafting.ItemSkinningRecipes;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class SkinDataHandler implements ISkinDataHandler {

    public static final SkinDataHandler INSTANCE = new SkinDataHandler();
    
    @Override
    public void setSkinOnPlayer(EntityPlayer player, ItemStack stack, int index) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.setEquipmentStack(stack, index);
    }
    
    @Deprecated
    @Override
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack) {
        setSkinOnPlayer(player, stack, 0);
        return false;
    }
    
    @Override
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType, int index) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getEquipmentStack(skinType, index);
    }
    
    @Deprecated
    @Override
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType) {
        return getSkinFormPlayer(player, skinType, 0);
    }
    
    @Override
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType, int index) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearEquipmentStack(skinType, index);
    }
    
    @Deprecated
    @Override
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType) {
        removeSkinFromPlayer(player, skinType, 0);
    }
    
    @Override
    public boolean isValidEquipmentSkin(ItemStack stack) {
        return (stack != null && stack.getItem() == ModItems.equipmentSkin && stackHasSkinPointer(stack));
    }

    @Override
    public boolean stackHasSkinPointer(ItemStack stack) {
        return SkinNBTHelper.stackHasSkinData(stack);
    }

    @Override
    public ISkinPointer getSkinPointerFromStack(ItemStack stack) {
        return SkinNBTHelper.getSkinPointerFromStack(stack);
    }

    @Override
    public void saveSkinPointerOnStack(ISkinPointer skinPointer, ItemStack stack) {
        if (stack == null) {
            return;
        }
        SkinPointer sp = new SkinPointer(skinPointer);
        SkinNBTHelper.addSkinDataToStack(stack, sp);
    }
    
    @Override
    public boolean compoundHasSkinPointer(NBTTagCompound compound) {
        return SkinNBTHelper.compoundHasSkinData(compound);
    }

    @Override
    public ISkinPointer readSkinPointerFromCompound(NBTTagCompound compound) {
        if (!SkinNBTHelper.compoundHasSkinData(compound)) {
            return null;
        }
        SkinPointer sp = new SkinPointer();
        sp.readFromCompound(compound);
        return sp;
    }

    @Override
    public void writeSkinPointerToCompound(ISkinPointer skinPointer, NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        SkinPointer sp = new SkinPointer(skinPointer);
        sp.writeToCompound(compound);
    }
    
    @Override
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId) {
        ExPropsPlayerEquipmentData entityProps = ExPropsPlayerEquipmentData.get(player);
        BitSet armourOverride = entityProps.getArmourOverride();
        if (slotId < 4 & slotId >= 0) {
            return armourOverride.get(slotId);
        }
        return false;
    }
    
    private ExPropsPlayerEquipmentData getExtendedPropsPlayerForPlayer(EntityPlayer player) {
        ExPropsPlayerEquipmentData entityProps = ExPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            ExPropsPlayerEquipmentData.register(player);
        }
        return ExPropsPlayerEquipmentData.get(player);
    }

    @Override
    public void setItemAsSkinnable(Item item) {
        ItemSkinningRecipes.addSkinnableItem(item);
    }

    @Override
    public ISkinPointer addSkinToCache(InputStream inputStream) {
        if (inputStream != null) {
            Skin skin = CommonSkinCache.INSTANCE.addSkinToCache(inputStream);
            if (skin != null) {
                SkinPointer sp = new SkinPointer(skin.getSkinType(), skin.lightHash(), false);
                return sp;
            }
        }
        return null;
    }
}
