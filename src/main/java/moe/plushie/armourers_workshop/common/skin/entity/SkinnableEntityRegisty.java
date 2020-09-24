package moe.plushie.armourers_workshop.common.skin.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntityRegisty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.library.ILibraryManager;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.library.LibraryFileList;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public final class SkinnableEntityRegisty implements ISkinnableEntityRegisty {

    public static SkinnableEntityRegisty INSTANCE;

    private HashMap<Class<? extends Entity>, ISkinnableEntity> entityMap;

    public static void init() {
        INSTANCE = new SkinnableEntityRegisty();
    }

    public SkinnableEntityRegisty() {
        MinecraftForge.EVENT_BUS.register(this);
        entityMap = new HashMap<Class<? extends Entity>, ISkinnableEntity>();
        registerEntities();
    }

    private void registerEntities() {
        registerEntity(new SkinnableEntityChicken());
        registerEntity(new SkinnableEntityCreeper());
        registerEntity(new SkinnableEntityGhast());
        registerEntity(new SkinnableEntityPlayer());
        registerEntity(new SkinnableEntitySkeleton());
        registerEntity(new SkinnableEntitySlime());
        registerEntity(new SkinnableEntityMannequin());
        // registerEntity(new SkinnableEntityZombie());
    }

    @Override
    public void registerEntity(ISkinnableEntity skinnableEntity) {
        if (skinnableEntity == null) {
            ModLogger.log(Level.WARN, "A mod tried to register a null skinnable entity.");
            return;
        }
        if (skinnableEntity.getEntityClass() == null) {
            ModLogger.log(Level.WARN, "A mod tried to register a skinnable entity with a null class.");
            return;
        }
        ModLogger.log(String.format("Registering %s as a skinnable entity.", skinnableEntity.getEntityClass()));
        entityMap.put(skinnableEntity.getEntityClass(), skinnableEntity);
    }

    @Override
    public ISkinnableEntity getSkinnableEntity(Entity entity) {
        if (entityMap.containsKey(entity.getClass())) {
            return entityMap.get(entity.getClass());
        }
        for (ISkinnableEntity skinnableEntity : entityMap.values()) {
            if (skinnableEntity.getEntityClass().isAssignableFrom(entity.getClass())) {
                return skinnableEntity;
            }
        }
        return null;
    }

    @Override
    public boolean isValidEntity(Entity entity) {
        if (entityMap.containsKey(entity.getClass())) {
            return true;
        }
        return false;
    }

    public boolean canUseWandOfStyleOnEntity(Entity entity, EntityPlayer user) {
        ISkinnableEntity skinnableEntity = getSkinnableEntity(entity);
        if (skinnableEntity != null) {
            return skinnableEntity.canUseWandOfStyle(user);
        }
        return false;
    }

    private void dropEntitySkins(Entity entity) {
        if (ConfigHandler.entityDropSkinChance <= 0) {
            return;
        }
        int rnd = entity.getEntityWorld().rand.nextInt(99) + 1;
        /*if (rnd <= ConfigHandler.entityDropSkinChance) {
            ExPropsEntityEquipmentData entityEquipmentData = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
            if (entityEquipmentData != null) {
                ArrayList<ISkinType> skinTypes = entityEquipmentData.getSkinInventory().getSkinTypes();
                for (int i = 0; i < skinTypes.size(); i++) {
                    ISkinDescriptor skinPointer = entityEquipmentData.getEquipmentData().getSkinPointer(skinTypes.get(i), 0);
                    if (skinPointer != null) {
                        ItemStack stack = SkinNBTHelper.makeEquipmentSkinStack((SkinDescriptor) skinPointer);
                        UtilItems.spawnItemAtEntity(entity, stack);
                    }
                }
            }
        }*/
    }
    
    /*public void giveRandomSkin(Entity entity) {
        giveRandomSkin(ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity));
    }
    
    public void giveRandomSkin(ExPropsEntityEquipmentData entityEquipmentData) {
        if (entityEquipmentData == null) {
            return;
        }
        if (ConfigHandler.enitiySpawnWithSkinsChance <= 0) {
            return;
        }

        
        ArrayList<ISkinType> skinTypes = entityEquipmentData.getSkinInventory().getSkinTypes();
        for (int i = 0; i < skinTypes.size(); i++) {
            int rnd = entityEquipmentData.getEntity().getEntityWorld().rand.nextInt(99) + 1;
            if (rnd >= ConfigHandler.enitiySpawnWithSkinsChance) {
                continue;
            }
            ISkinType skinType = skinTypes.get(i);
            LibraryFile libraryFile = getRandomSkinOfType(skinType);
            if (libraryFile == null) {
                continue;
            }
            SkinIdentifier identifier = new SkinIdentifier(0, libraryFile, 0, skinType);
            ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(identifier));
            
            if (skinStack == null) {
                continue;
            }
            entityEquipmentData.getSkinInventory().setInventorySlotContents(i, skinStack);
        }
    }*/

    public LibraryFile getRandomSkinOfType(ISkinType skinType) {
        ILibraryManager libraryManager = ArmourersWorkshop.getProxy().libraryManager;

        LibraryFileList fileList = null;
        if (ArmourersWorkshop.isDedicated()) {
            fileList = libraryManager.getServerPublicFileList();
        } else {
            fileList = libraryManager.getClientPublicFileList();
        }

        ArrayList<LibraryFile> typeList = fileList.getCachedFileListForSkinType(skinType);
        if (typeList == null) {
            return null;
        }
        ArrayList<LibraryFile> validFiles = new ArrayList<LibraryFile>();
        for (int i = 0; i < typeList.size(); i++) {
            if (typeList.get(i).filePath.startsWith(ConfigHandler.enitiySpawnSkinTargetPath)) {
                validFiles.add(typeList.get(i));
            }
        }
        // touhou
        Random random = new Random();
        if (!validFiles.isEmpty()) {
            return validFiles.get(random.nextInt(validFiles.size()));
        }
        return null;
    }

    public ArrayList<ISkinnableEntity> getRegisteredSkinnableEntities() {
        ArrayList<ISkinnableEntity> entityList = new ArrayList<ISkinnableEntity>();
        for (ISkinnableEntity skinnableEntity : entityMap.values()) {
            entityList.add(skinnableEntity);
        }
        return entityList;
    }
}
