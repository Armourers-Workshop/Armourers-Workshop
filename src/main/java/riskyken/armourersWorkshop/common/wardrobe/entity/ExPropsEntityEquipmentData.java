package riskyken.armourersWorkshop.common.wardrobe.entity;

import java.util.ArrayList;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import riskyken.armourersWorkshop.api.common.skin.entity.ISkinnableEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.inventory.IInventorySlotUpdate;
import riskyken.armourersWorkshop.common.inventory.InventoryEntitySkin;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerEntitySkinData;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.wardrobe.EntityEquipmentData;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ExPropsEntityEquipmentData implements IExtendedEntityProperties, IInventorySlotUpdate {
    
    private static final String TAG_EXT_PROP_NAME = "entityCustomEquipmentData";
    private static final String TAG_ADDED_SPAWN_ITEMS = "addedSpawnItems";
    
    private final Entity entity;
    private EntityEquipmentData equipmentData;
    private final InventoryEntitySkin skinInventory;
    private boolean allowNetworkUpdates;
    
    public ExPropsEntityEquipmentData(Entity entity, ISkinnableEntity skinnableEntity) {
        allowNetworkUpdates = true;
        this.entity = entity;
        this.equipmentData = new EntityEquipmentData();
        
        ArrayList<ISkinType> skinTypes = new ArrayList<ISkinType>();
        skinnableEntity.getValidSkinTypes(skinTypes);
        this.skinInventory = new InventoryEntitySkin(this, skinTypes);
    }
    
    @Override
    public void setInventorySlotContents(IInventory inventory, int slotId, ItemStack stack) {
        if (entity.worldObj.isRemote) {
            return;
        }
        if (stack == null) {
            ISkinType skinType = getSkinTypeForSlot(slotId);
            equipmentData.removeEquipment(skinType, 0);
        } else {
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(stack);
            equipmentData.addEquipment(skinData.getIdentifier().getSkinType(), 0, skinData);
        }
        sendEquipmentDataToPlayerToAllPlayersAround();
    }
    
    private ISkinType getSkinTypeForSlot(int slotId) {
        return skinInventory.getSkinTypes().get(slotId);
    }
    
    private void sendEquipmentDataToPlayerToAllPlayersAround() {
        if (!allowNetworkUpdates) {
            return;
        }
        TargetPoint p = new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerEntitySkinData(equipmentData, entity.getEntityId()), p);
    }
    
    public void sendEquipmentDataToPlayer(EntityPlayerMP targetPlayer) {
        PacketHandler.networkWrapper.sendTo(new MessageServerEntitySkinData(equipmentData, entity.getEntityId()), targetPlayer);
    }
    
    public InventoryEntitySkin getSkinInventory() {
        return skinInventory;
    }
    
    public EntityEquipmentData getEquipmentData() {
        return equipmentData;
    }
    
    public void setEquipmentData(EntityEquipmentData equipmentData) {
        this.equipmentData = equipmentData;
    }
    
    @Override
    public void saveNBTData(NBTTagCompound compound) {
        this.skinInventory.saveItemsToNBT(compound);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        allowNetworkUpdates = false;
        equipmentData.clear();
        skinInventory.loadItemsFromNBT(compound);
        allowNetworkUpdates = true;
    }
    
    private void addSpawnItems() {
        if (entity.worldObj != null && !entity.worldObj.isRemote) {
            EntitySkinHandler.INSTANCE.giveRandomSkin(this);
        }
    }
    
    @Override
    public void init(Entity entity, World world) {
        addSpawnItems();
    }
    
    public static final void register(Entity entity, ISkinnableEntity skinnableEntity) {
        entity.registerExtendedProperties(TAG_EXT_PROP_NAME, new ExPropsEntityEquipmentData(entity, skinnableEntity));
    }
    
    private static final ExPropsEntityEquipmentData get(Entity entity) {
        return (ExPropsEntityEquipmentData) entity.getExtendedProperties(TAG_EXT_PROP_NAME);
    }
    
    public static final ExPropsEntityEquipmentData getExtendedPropsForEntity(Entity entity) {
        return ExPropsEntityEquipmentData.get(entity);
    }
    
    public Entity getEntity() {
        return entity;
    }
}
