package riskyken.armourersWorkshop.common.customEquipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.api.common.customEquipment.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.customEquipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class EntityEquipmentDataManager implements IEquipmentDataHandler {
    
    public static final EntityEquipmentDataManager INSTANCE = new EntityEquipmentDataManager();
    
    public EntityEquipmentDataManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.target instanceof EntityPlayerMP) {
            EntityPlayerMP targetPlayer = (EntityPlayerMP) event.target;
            ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity).sendCustomArmourDataToPlayer(targetPlayer);
        
            ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(event.entity);
            if (entityProps != null) {
                entityProps.sendCustomEquipmentDataToPlayer(targetPlayer);
            }
        }
    }
    
    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        if (event.target instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.entity;
        }
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity) == null) {
            ExtendedPropsPlayerEquipmentData.register((EntityPlayer) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity).sendCustomArmourDataToPlayer((EntityPlayerMP) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onLivingDeathEvent (LivingDeathEvent  event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            ExtendedPropsPlayerEquipmentData playerData = ExtendedPropsPlayerEquipmentData.get((EntityPlayer) event.entity);
            playerData.dropItems();
        }
    }

    @Override
    public EntityEquipmentData getCustomEquipmentForEntity(Entity entity) {
        ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        if (entityProps != null) {
            return entityProps.getEquipmentData();
        }
        return null;
    }

    @Override
    public void removeCustomEquipmentFromEntity(Entity entity) {
        ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        if (entityProps == null) {
            return;
        }
        entityProps.removeCustomEquipment(EnumArmourType.HEAD);
        entityProps.removeCustomEquipment(EnumArmourType.CHEST);
        entityProps.removeCustomEquipment(EnumArmourType.LEGS);
        entityProps.removeCustomEquipment(EnumArmourType.SKIRT);
        entityProps.removeCustomEquipment(EnumArmourType.FEET);
    }

    @Override
    public void setCustomEquipmentOnEntity(Entity entity, IEntityEquipment equipmentData) {
        ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        if (entityProps == null) {
            ExtendedPropsEntityEquipmentData.register(entity);
        }
        entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        entityProps.setEquipmentData(equipmentData);
    }

    @Override
    public void setCustomEquipmentOnPlayer(EntityPlayer player, IEntityEquipment equipmentData) {
        ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            ExtendedPropsPlayerEquipmentData.register(player);
        }
        entityProps = ExtendedPropsPlayerEquipmentData.get(player);
        entityProps.setEquipmentData(equipmentData);
    }

    @Override
    public IEntityEquipment getCustomEquipmentForPlayer(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get(player);
        if (entityProps != null) {
            return entityProps.getEquipmentData();
        }
        return null;
    }

    @Override
    public void removeCustomEquipmentFromPlayer(EntityPlayer player) {
        ExtendedPropsPlayerEquipmentData entityProps = ExtendedPropsPlayerEquipmentData.get(player);
        if (entityProps == null) {
            return;
        }
        entityProps.removeCustomEquipment(EnumArmourType.HEAD);
        entityProps.removeCustomEquipment(EnumArmourType.CHEST);
        entityProps.removeCustomEquipment(EnumArmourType.LEGS);
        entityProps.removeCustomEquipment(EnumArmourType.SKIRT);
        entityProps.removeCustomEquipment(EnumArmourType.FEET);
    }
}
