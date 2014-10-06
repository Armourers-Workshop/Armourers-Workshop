package riskyken.armourersWorkshop.common.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsEntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ModItems;

public class EquipmentDataHandler implements IEquipmentDataHandler {

    public static final EquipmentDataHandler INSTANCE = new EquipmentDataHandler();
    
    @Override
    public EntityEquipmentData getCustomEquipmentForEntity(Entity entity) {
        if (entity instanceof EntityPlayer) {
            ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) entity);
            if (entityProps != null) {
                return entityProps.getEquipmentData();
            }
        } else {
            ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
            if (entityProps != null) {
                return entityProps.getEquipmentData();
            }
        }
        return null;
    }

    @Override
    public void removeAllCustomEquipmentFromEntity(Entity entity) {
        if (entity instanceof EntityPlayer) {
            ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) entity);
            if (entityProps == null) {
                return;
            }
            entityProps.removeAllCustomEquipment();
        } else {
            ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
            if (entityProps == null) {
                return;
            }
            entityProps.removeAllCustomEquipment();
        }
    }

    @Override
    public void removeCustomEquipmentFromEntity(Entity entity, EnumArmourType armourType) {
        if (entity instanceof EntityPlayer) {
            ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) entity);
            if (entityProps == null) {
                return;
            }
            entityProps.removeCustomEquipment(armourType);
        } else {
            ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
            if (entityProps == null) {
                return;
            }
            entityProps.removeCustomEquipment(armourType);
        }
    }

    @Override
    public void setCustomEquipmentOnEntity(Entity entity, IEntityEquipment equipmentData) {
        if (entity instanceof EntityPlayer) {
            ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) entity);
            if (entityProps == null) {
                ExtendedPropsPlayerEquipmentData.register((EntityPlayer) entity);
            }
            entityProps = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) entity);
            entityProps.setEquipmentData(equipmentData); 
        } else {
            ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
            if (entityProps == null) {
                ExtendedPropsEntityEquipmentData.register(entity);
            }
            entityProps = ExtendedPropsEntityEquipmentData.get(entity);
            entityProps.setEquipmentData(equipmentData); 
        }
    }

    @Override
    public EnumArmourType getEquipmentType(int equipmentId) {
        CustomArmourItemData data = EquipmentDataCache.INSTANCE.getEquipmentData(equipmentId);
        if (data != null) {
            return data.getType();
        }
        return EnumArmourType.NONE;
    }

    @Override
    public ItemStack getCustomEquipmentItemStack(int equipmentId) {
        CustomArmourItemData armourItemData = EquipmentDataCache.INSTANCE.getEquipmentData(equipmentId);
        if (armourItemData == null) { return null; }
        ItemStack stackOutput = new ItemStack(ModItems.equipmentSkin, 1, armourItemData.getType().ordinal() - 1);
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
}
