package riskyken.armourersWorkshop.common.skin.npc;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.api.common.skin.npc.INpcSkinDataHandler;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class NpcSkinDataHandler implements INpcSkinDataHandler {
    
    public static NpcSkinDataHandler INSTANCE;
    
    private static HashMap<Class<? extends EntityLivingBase>, INpcSkinDataHandler> entityMap;
    //private static HashMap<Class<? extends EntityLivingBase>> validEntities;
    
    public static void init() {
        INSTANCE = new NpcSkinDataHandler();
    }
    
    public NpcSkinDataHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        //validEntities = new HashSet<Class<? extends EntityLivingBase>>();
        registerEntity(EntityZombie.class);
    }
    
    @Override
    public void registerEntity(Class<? extends EntityLivingBase> entityClass) {
        //validEntities.add(entityClass);
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
    
    @Override
    public boolean isValidEntity(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            //if (validEntities.contains(entity.getClass())) {
                //return true;
            //}
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
