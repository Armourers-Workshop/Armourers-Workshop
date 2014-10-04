package riskyken.armourersWorkshop.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.common.custom.equipment.ExtendedPropsEquipmentData;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModForgeEventHandler {
    
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        ModLogger.log(event.entity);
        if (event.target instanceof EntityPlayerMP) {
            EntityPlayerMP targetPlayer = (EntityPlayerMP) event.target;
            ExtendedPropsEquipmentData.get((EntityPlayer) event.entity).sendCustomArmourDataToPlayer(targetPlayer);
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
        if (event.entity instanceof EntityPlayer && ExtendedPropsEquipmentData.get((EntityPlayer) event.entity) == null) {
            ExtendedPropsEquipmentData.register((EntityPlayer) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            ExtendedPropsEquipmentData.get((EntityPlayer) event.entity).sendCustomArmourDataToPlayer((EntityPlayerMP) event.entity);
        }
    }
    
    @SubscribeEvent
    public void onLivingDeathEvent (LivingDeathEvent  event) {
        if (!event.entity.worldObj.isRemote && event.entity instanceof EntityPlayerMP) {
            ExtendedPropsEquipmentData playerData = ExtendedPropsEquipmentData.get((EntityPlayer) event.entity);
            playerData.dropItems();
        }
    }
}
