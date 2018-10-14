package moe.plushie.armourers_workshop.common.handler;

import java.io.InputStream;
import java.util.BitSet;

import moe.plushie.armourers_workshop.api.common.skin.ISkinDataHandler;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SkinDataHandler implements ISkinDataHandler {

    public static final SkinDataHandler INSTANCE = new SkinDataHandler();
    
    @Override
    public void setSkinOnPlayer(EntityPlayer player, ItemStack stack, int index) {
        ExPropsPlayerSkinData entityProps = getExtendedPropsPlayerForPlayer(player);
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
        ExPropsPlayerSkinData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getEquipmentStack(skinType, index);
    }
    
    @Deprecated
    @Override
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType) {
        return getSkinFormPlayer(player, skinType, 0);
    }
    
    @Override
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType, int index) {
        ExPropsPlayerSkinData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearEquipmentStack(skinType, index);
    }
    
    @Deprecated
    @Override
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType) {
        removeSkinFromPlayer(player, skinType, 0);
    }
    
    @Override
    public boolean isValidEquipmentSkin(ItemStack stack) {
        return (stack != null && stack.getItem() == ModItems.Skin && stackHasSkinPointer(stack));
    }

    @Override
    public boolean stackHasSkinPointer(ItemStack stack) {
        return SkinNBTHelper.stackHasSkinData(stack);
    }

    @Override
    public ISkinDescriptor getSkinPointerFromStack(ItemStack stack) {
        return SkinNBTHelper.getSkinDescriptorFromStack(stack);
    }

    @Override
    public void saveSkinPointerOnStack(ISkinDescriptor skinPointer, ItemStack stack) {
        if (stack == null) {
            return;
        }
        SkinDescriptor sp = new SkinDescriptor(skinPointer);
        SkinNBTHelper.addSkinDataToStack(stack, sp);
    }
    
    @Override
    public boolean compoundHasSkinPointer(NBTTagCompound compound) {
        return SkinNBTHelper.compoundHasSkinData(compound);
    }

    @Override
    public ISkinDescriptor readSkinPointerFromCompound(NBTTagCompound compound) {
        if (!SkinNBTHelper.compoundHasSkinData(compound)) {
            return null;
        }
        SkinDescriptor sp = new SkinDescriptor();
        sp.readFromCompound(compound);
        return sp;
    }

    @Override
    public void writeSkinPointerToCompound(ISkinDescriptor skinPointer, NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        SkinDescriptor sp = new SkinDescriptor(skinPointer);
        sp.writeToCompound(compound);
    }
    
    @Override
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId) {
        ExPropsPlayerSkinData entityProps = ExPropsPlayerSkinData.get(player);
        BitSet armourOverride = entityProps.getArmourOverride();
        if (slotId < 4 & slotId >= 0) {
            return armourOverride.get(slotId);
        }
        return false;
    }
    
    private ExPropsPlayerSkinData getExtendedPropsPlayerForPlayer(EntityPlayer player) {
        ExPropsPlayerSkinData entityProps = ExPropsPlayerSkinData.get(player);
        if (entityProps == null) {
            ExPropsPlayerSkinData.register(player);
        }
        return ExPropsPlayerSkinData.get(player);
    }

    @Override
    public ISkinDescriptor addSkinToCache(InputStream inputStream) {
        if (inputStream != null) {
            Skin skin = CommonSkinCache.INSTANCE.addSkinToCache(inputStream);
            if (skin != null) {
                SkinDescriptor sp = new SkinDescriptor(new SkinIdentifier(skin), false);
                return sp;
            }
        }
        return null;
    }
}
