package riskyken.armourersWorkshop.common.skin.entity;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;

public final class EntitySkinHandler {
    
    public static EntitySkinHandler INSTANCE;
    
    private HashMap<Class <? extends EntityLivingBase>, ISkinnableEntity> entityMap;
    
    public static void init() {
        INSTANCE = new EntitySkinHandler();
    }
    
    public EntitySkinHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        entityMap = new HashMap<Class <? extends EntityLivingBase>, ISkinnableEntity>();
        registerEntities();
    }
    
    private void registerEntities() {
        //registerEntity(new SkinnableEntityZombie());
        //registerEntity(new SkinnableEntityChicken());
    }
    
    @Override
    public void registerEntity(ISkinnableEntity skinnableEntity) {
        if (skinnableEntity == null) {
            return;
        }
        if (skinnableEntity.getEntityClass() == null) {
            return;
        }
        entityMap.put(skinnableEntity.getEntityClass(), skinnableEntity);
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
            if (entityMap.containsKey(entity.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean canUseWandOfStyleOnEntity(Entity entity) {
        if (isValidEntity(entity)) {
            ISkinnableEntity skinnableEntity = entityMap.get(entity.getClass());
            return skinnableEntity.canUseWandOfStyle();
        }
        return false;
    }
    
    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing event) {
        if (isValidEntity(event.entity)) {
            ISkinnableEntity skinnableEntity = entityMap.get(event.entity.getClass());
            ExPropsEntityEquipmentData.register(event.entity, skinnableEntity);
        }
    }
    
    @SideOnly(Side.CLIENT)
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

    public ArrayList<ISkinnableEntity> getRegisteredEntities() {
        ArrayList<ISkinnableEntity> entityList = new ArrayList<ISkinnableEntity>();
        for (int i = 0; i < entityMap.size(); i++) {
            Class <? extends EntityLivingBase> entityClass;
            entityClass = (Class <? extends EntityLivingBase>)entityMap.keySet().toArray()[i];
            ISkinnableEntity entity = entityMap.get(entityClass);
            if (entity != null) {
                entityList.add(entity);
            }
        }
        return entityList;
    }
}
