package riskyken.armourersWorkshop.common.custom.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.api.common.event.AddEntityEquipmentEvent;
import riskyken.armourersWorkshop.api.common.event.AddEntityEquipmentEvent.IAddEntityEquipmentListener;
import riskyken.armourersWorkshop.api.common.event.RemoveEntityEquipmentEvent;
import riskyken.armourersWorkshop.api.common.event.RemoveEntityEquipmentEvent.IRemoveEntityEquipmentListener;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityEquipmentDataManager implements IAddEntityEquipmentListener, IRemoveEntityEquipmentListener{
    
    public EntityEquipmentDataManager() {
        MinecraftForge.EVENT_BUS.register(this);
        AddEntityEquipmentEvent.addListener(this);
        RemoveEntityEquipmentEvent.addListener(this);
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
    public void onRemoveEntityEquipmentEvent(Entity entity, EnumArmourType armourType) {
        ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        if (entityProps == null) {
            return;
        }
        entityProps.removeCustomEquipment(armourType);
    }

    @Override
    public void onAddEntityEquipmentEvent(Entity entity, EnumArmourType armourType, int equipmentId) {
        ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        if (entityProps == null) {
            ExtendedPropsEntityEquipmentData.register(entity);
        }
        entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        entityProps.addCustomEquipment(armourType, equipmentId);
    }
}
