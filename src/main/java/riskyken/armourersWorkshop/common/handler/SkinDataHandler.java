package riskyken.armourersWorkshop.common.handler;

import java.io.InputStream;
import java.util.BitSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import riskyken.armourersWorkshop.api.common.skin.ISkinDataHandler;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.capability.IWardrobeCapability;
import riskyken.armourersWorkshop.common.crafting.ItemSkinningRecipes;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class SkinDataHandler implements ISkinDataHandler {

    public static final SkinDataHandler INSTANCE = new SkinDataHandler();
    
    @CapabilityInject(IWardrobeCapability.class)
    private static final Capability<IWardrobeCapability> WARDROBE_CAP = null;
    
    @Override
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack, int columnIndex) {
        IWardrobeCapability wardrobe = getPlayerWardrobe(player);
        if (wardrobe != null) {
            wardrobe.setSkinStack(stack, columnIndex);
        }
        return false;
    }
    
    @Override
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType, int columnIndex) {
        IWardrobeCapability wardrobe = getPlayerWardrobe(player);
        if (wardrobe != null) {
            return wardrobe.getSkinStack(skinType, columnIndex);
        }
        return null;
    }
    
    @Override
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType, int columnIndex) {
        IWardrobeCapability wardrobe = getPlayerWardrobe(player);
        if (wardrobe != null) {
            wardrobe.removeSkinStack(skinType, columnIndex);
        }
    }
    
    @Deprecated
    @Override
    public boolean setSkinOnPlayer(EntityPlayer player, ItemStack stack) {
        return setSkinOnPlayer(player, stack, 0);
    }
    
    @Deprecated
    @Override
    public ItemStack getSkinFormPlayer(EntityPlayer player, ISkinType skinType) {
        return getSkinFormPlayer(player, skinType, 0);
    }
    
    @Deprecated
    @Override
    public void removeSkinFromPlayer(EntityPlayer player, ISkinType skinType) {
        removeSkinFromPlayer(player, skinType, 0);
    }
    
    @Override
    public boolean isStackValidSkin(ItemStack stack) {
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
        IWardrobeCapability wardrobe = getPlayerWardrobe(player);
        if (wardrobe != null) {
            BitSet armourOverride = wardrobe.getArmourOverride();
            if (slotId < 4 & slotId >= 0) {
                return armourOverride.get(slotId);
            }
        }
        return false;
    }
    
    private IWardrobeCapability getPlayerWardrobe(EntityPlayer player) {
        return player.getCapability(WARDROBE_CAP, null);
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
