package riskyken.armourersWorkshop.common.equipment.npc;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class NpcEquipmentDataHandler {
    
    public static NpcEquipmentDataHandler INSTANCE;
    
    public static void init() {
        INSTANCE = new NpcEquipmentDataHandler();
    }
    
    public NpcEquipmentDataHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.entity.worldObj.isRemote) {
            return;
        }
        
        Entity entity = event.target;
        ExPropsEntityEquipmentData props = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
        if (props != null) {
            props.sendEquipmentDataToPlayer((EntityPlayerMP) event.entityPlayer);
        }
    }
    
    @SubscribeEvent
    public void onStopTracking(PlayerEvent.StopTracking event) {
        //ModLogger.log("stop tracking");
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (event.entity instanceof EntityZombie) {
            ExPropsEntityEquipmentData.register(event.entity);
        }
        if (event.entity instanceof EntitySkeleton) {
            ExPropsEntityEquipmentData.register(event.entity);
        }
    }
    
    public void receivedEquipmentData(EntityEquipmentData equipmentData, int entityId) {
        World world = Minecraft.getMinecraft().theWorld;
        Entity entity = world.getEntityByID(entityId);
        if (entity != null) {
            ExPropsEntityEquipmentData props = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
            if (props != null) {
                props.setEquipmentData(equipmentData);
            }
        }
    }
}
