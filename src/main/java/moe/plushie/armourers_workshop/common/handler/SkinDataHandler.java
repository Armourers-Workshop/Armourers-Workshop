package moe.plushie.armourers_workshop.common.handler;

import java.io.InputStream;

import moe.plushie.armourers_workshop.api.common.skin.ISkinDataHandler;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SkinDataHandler implements ISkinDataHandler {

    public static final SkinDataHandler INSTANCE = new SkinDataHandler();
    
    
    @Override
    public void setEntitySkin(EntityLivingBase entityLivingBase, ItemStack stack, int index) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entityLivingBase);
        ISkinDescriptor skinDescriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (skinCapability != null & skinDescriptor != null) {
            skinCapability.setSkinDescriptor(skinDescriptor.getIdentifier().getSkinType(), index, skinDescriptor);
            skinCapability.syncToAllTracking();
        }
    }
    
    @Override
    public ItemStack getEntitySkin(EntityLivingBase entityLivingBase, ISkinType skinType, int index) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entityLivingBase);
        if (skinCapability != null) {
            return skinCapability.getSkinStack(skinType, index);
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void removeEntitySkin(EntityLivingBase entityLivingBase, ISkinType skinType, int index) {
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(entityLivingBase);
        if (skinCapability != null) {
            skinCapability.setSkinDescriptor(skinType, index, null);
            skinCapability.syncToAllTracking();
        }
    }
    
    @Override
    public boolean isValidSkin(ItemStack stack) {
        return stack.getItem() == ModItems.skin && stackHasSkinDescriptor(stack);
    }
    
    @Override
    public boolean stackHasSkinDescriptor(ItemStack stack) {
        return SkinNBTHelper.stackHasSkinData(stack);
    }
    
    @Override
    public ISkinDescriptor getSkinDescriptorFromStack(ItemStack stack) {
        return SkinNBTHelper.getSkinDescriptorFromStack(stack);
    }
    
    @Override
    public void saveSkinDescriptorOnStack(ISkinDescriptor skinDescriptor, ItemStack stack) {
        SkinDescriptor sp = new SkinDescriptor(skinDescriptor);
        SkinNBTHelper.addSkinDataToStack(stack, sp);
    }
    
    @Override
    public boolean compoundHasSkinDescriptor(NBTTagCompound compound) {
        return SkinNBTHelper.compoundHasSkinData(compound);
    }

    @Override
    public ISkinDescriptor readSkinDescriptorFromCompound(NBTTagCompound compound) {
        if (!SkinNBTHelper.compoundHasSkinData(compound)) {
            return null;
        }
        SkinDescriptor sp = new SkinDescriptor();
        sp.readFromCompound(compound);
        return sp;
    }

    @Override
    public void writeSkinDescriptorToCompound(ISkinDescriptor skinPointer, NBTTagCompound compound) {
        if (compound == null) {
            return;
        }
        SkinDescriptor sp = new SkinDescriptor(skinPointer);
        sp.writeToCompound(compound);
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
    
    @Override
    public boolean isArmourRenderOverridden(EntityPlayer player, EntityEquipmentSlot equipmentSlot) {
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            return wardrobeCap.getArmourOverride(equipmentSlot);
        }
        return false;
    }
}
