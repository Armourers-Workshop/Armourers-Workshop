package riskyken.armourersWorkshop.common.handler;

import java.util.BitSet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EquipmentDataHandler implements IEquipmentDataHandler {

    public static final EquipmentDataHandler INSTANCE = new EquipmentDataHandler();
    
    @Override
    public void setCustomEquipmentOnPlayer(EntityPlayer player, ItemStack stack) {
        ExtendedPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.setEquipmentStack(stack);
    }

    @Override
    public ItemStack[] getAllCustomEquipmentForPlayer(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getAllEquipmentStacks();
    }

    @Override
    public ItemStack getCustomEquipmentForPlayer(EntityPlayer player, ISkinType skinType) {
        ExtendedPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        return entityProps.getEquipmentStack(skinType);
    }

    @Override
    public void clearAllCustomEquipmentFromPlayer(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearAllEquipmentStacks();
    }

    @Override
    public void clearCustomEquipmentFromPlayer(EntityPlayer player, ISkinType skinType) {
        ExtendedPropsPlayerEquipmentData entityProps = getExtendedPropsPlayerForPlayer(player);
        entityProps.clearEquipmentStack(skinType);
    }
    
    @SideOnly(Side.CLIENT)
    private IEntityEquipment getLocalPlayerEquipment(Entity entity) {
        return EquipmentModelRenderer.INSTANCE.getPlayerCustomEquipmentData(entity);
    }
    
    @Override
    public ISkinType getSkinTypeFromStack(ItemStack stack) {
        if (!hasItemStackGotEquipmentData(stack)) {
            return null;
        }
        Item item = stack.getItem();
        if (item == ModItems.equipmentSkin) {
            int damage = stack.getItemDamage();
            if (damage >= 0 & damage < 6) {
                return SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(damage);
            }
        }
        if (item == ModItems.armourContainer[0]) {
            return SkinTypeRegistry.skinHead;
        }
        if (item == ModItems.armourContainer[1]) {
            return SkinTypeRegistry.skinChest;
        }
        if (item == ModItems.armourContainer[2]) {
            return SkinTypeRegistry.skinLegs;
        }
        if (item == ModItems.armourContainer[3]) {
            return SkinTypeRegistry.skinFeet;
        }
        return null;
    }
    
    @Override
    public boolean hasItemStackGotEquipmentData(ItemStack stack) {
        return EquipmentNBTHelper.itemStackHasCustomEquipment(stack);
    }
    
    @Override
    public int getEquipmentIdFromItemStack(ItemStack stack) {
        return EquipmentNBTHelper.getEquipmentIdFromStack(stack);
    }

    public ItemStack getCustomEquipmentItemStack(int equipmentId) {
        CustomEquipmentItemData armourItemData = EquipmentDataCache.INSTANCE.getEquipmentData(equipmentId);
        if (armourItemData == null) { return null; }
        ItemStack stackOutput = new ItemStack(ModItems.equipmentSkin, 1, SkinTypeRegistry.INSTANCE.getLegacyIdForSkin(armourItemData.getSkinType()));
        NBTTagCompound armourNBT = new NBTTagCompound();
        armourItemData.writeClientDataToNBT(armourNBT);
        stackOutput.setTagCompound(new NBTTagCompound());
        stackOutput.getTagCompound().setTag(LibCommonTags.TAG_ARMOUR_DATA, armourNBT);;
        return stackOutput;
    }

    @Override
    public IInventory getPlayersEquipmentInventory(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            return null;
        }
        return entityProps;
    }

    @Override
    public boolean isArmourRenderOverridden(EntityPlayer player, int slotId) {
        ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get(player);
        BitSet armourOverride = entityProps.getArmourOverride();
        if (slotId < 4 & slotId >= 0) {
            return armourOverride.get(slotId);
        }
        return false;
    }
    
    private ExtendedPropsPlayerEquipmentData getExtendedPropsPlayerForPlayer(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            ExtendedPropsPlayerEquipmentData.register(player);
        }
        return ExtendedPropsPlayerEquipmentData.get(player);
    }
}
