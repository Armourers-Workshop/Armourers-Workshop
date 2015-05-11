package riskyken.armourersWorkshop.common.handler;

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
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class EquipmentDataHandler implements ISkinDataHandler {

    public static final EquipmentDataHandler INSTANCE = new EquipmentDataHandler();
    
    @Override
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.setEquipmentStack(stack);
        return false;
    }

    @Override
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getEquipmentStack(skinType);
    }

    @Override
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType) {
        ExPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearEquipmentStack(skinType);
    }
    
    @Override
    public boolean isValidEquipmentSkin(ItemStack stack) {
        return (stack != null && stack.getItem() == ModItems.equipmentSkin && stackHasSkinPointer(stack));
    }

    @Override
    public boolean stackHasSkinPointer(ItemStack stack) {
        return EquipmentNBTHelper.stackHasSkinData(stack);
    }

    @Override
    public ISkinPointer getSkinPointerFromStack(ItemStack stack) {
        return EquipmentNBTHelper.getSkinPointerFromStack(stack);
    }

    @Override
    public void saveSkinPointerOnStack(ISkinPointer skinPointer, ItemStack stack) {
        if (stack == null) {
            return;
        }
        SkinPointer sp = new SkinPointer(skinPointer);
        EquipmentNBTHelper.addSkinDataToStack(stack, sp);
    }
    
    @Override
    public boolean compoundHasSkinPointer(NBTTagCompound compound) {
        return EquipmentNBTHelper.compoundHasSkinData(compound);
    }

    @Override
    public ISkinPointer readSkinPointerFromCompound(NBTTagCompound compound) {
        if (!EquipmentNBTHelper.compoundHasSkinData(compound)) {
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
}
