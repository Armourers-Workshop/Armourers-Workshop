package riskyken.armourersWorkshop.common.skin.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.skin.entity.IEntitySkinHandler;
import riskyken.armourersWorkshop.api.common.skin.entity.ISkinnableEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileList;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public final class EntitySkinHandler implements IEntitySkinHandler {
    
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
        registerEntity(new SkinnableEntityChicken());
        registerEntity(new SkinnableEntityCreeper());
        registerEntity(new SkinnableEntityGhast());
        registerEntity(new SkinnableEntitySkeleton());
        registerEntity(new SkinnableEntityZombie());
    }
    
    @Override
    public void registerEntity(ISkinnableEntity skinnableEntity) {
        if (skinnableEntity == null) {
            return;
        }
        if (skinnableEntity.getEntityClass() == null) {
            return;
        }
        ModLogger.log(String.format("Registering %s as a skinnable entity.", skinnableEntity.getEntityClass()));
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
            //giveRandomSkin(event.entity);
        }
    }
    
    private void giveRandomSkin(Entity entity) {
        ExPropsEntityEquipmentData entityEquipmentData = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
        if (entityEquipmentData == null) {
            return;
        }
        
        ArrayList<ISkinType> skinTypes = entityEquipmentData.getSkinInventory().getSkinTypes();
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            LibraryFile libraryFile = getRandomSkinOfType(skinType);
            if (libraryFile == null) {
                continue;
            }
            
            Skin skin = SkinIOUtils.loadSkinFromFileName(libraryFile.getFullName() + SkinIOUtils.SKIN_FILE_EXTENSION);
            if (skin == null) {
                continue;
            }
            skin.getProperties().setProperty(Skin.KEY_FILE_NAME, libraryFile.getFullName() + SkinIOUtils.SKIN_FILE_EXTENSION);
            CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, libraryFile);
            
            ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(skin);
            if (skinStack == null) {
                continue;
            }
            entityEquipmentData.getSkinInventory().setInventorySlotContents(i, skinStack);
        }
    }
    
    public LibraryFile getRandomSkinOfType(ISkinType skinType) {
        ILibraryManager libraryManager = ArmourersWorkshop.getProxy().libraryManager;
        LibraryFileList fileList = libraryManager.getClientPublicFileList();
        
        ArrayList<LibraryFile> typeList = new ArrayList<LibraryFile>();
        
        for (int i = 0; i < fileList.getFileCount(); i++) {
            LibraryFile libraryFile = fileList.getFileList().get(i);
            if (libraryFile.skinType == skinType) {
                typeList.add(libraryFile);
            }
        }
        Random random = new Random();
        if (!typeList.isEmpty()) {
            return typeList.get(random.nextInt(typeList.size()));
        }
        return null;
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
