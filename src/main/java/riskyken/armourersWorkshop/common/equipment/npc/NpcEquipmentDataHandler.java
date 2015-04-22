package riskyken.armourersWorkshop.common.equipment.npc;

import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
    
    private static HashSet<Class<? extends EntityLivingBase>> validEntities;
    
    public static void init() {
        INSTANCE = new NpcEquipmentDataHandler();
    }
    
    public NpcEquipmentDataHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        validEntities = new HashSet<Class<? extends EntityLivingBase>>();
        validEntities.add(EntityZombie.class);
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
    
    public boolean isValidEntity(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (validEntities.contains(entity.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (isValidEntity(event.entity)) {
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
