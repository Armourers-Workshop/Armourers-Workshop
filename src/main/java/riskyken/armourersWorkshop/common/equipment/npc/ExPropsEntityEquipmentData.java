package riskyken.armourersWorkshop.common.equipment.npc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeHelper;
import riskyken.armourersWorkshop.common.inventory.IInventorySlotUpdate;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerEntityEquipmentData;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper.SkinNBTData;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class ExPropsEntityEquipmentData implements IExtendedEntityProperties, IInventorySlotUpdate {
    
    private static final String TAG_EXT_PROP_NAME = "entityCustomEquipmentData";
    private final Entity entity;
    private EntityEquipmentData equipmentData;
    private final InventoryEntitySkin skinInventory;
    
    public ExPropsEntityEquipmentData(Entity entity) {
        this.entity = entity;
        this.equipmentData = new EntityEquipmentData();
        this.skinInventory = new InventoryEntitySkin(this);
    }
    
    @Override
    public void setInventorySlotContents(int slotId, ItemStack stack) {
        if (stack == null) {
            ISkinType skinType = SkinTypeHelper.getSkinTypeForSlot(slotId);
            equipmentData.removeEquipment(skinType);
        } else {
            SkinNBTData skinData = EquipmentNBTHelper.getSkinNBTDataFromStack(stack);
            equipmentData.addEquipment(skinData.skinType, skinData.skinId);
        }
        sendEquipmentDataToPlayerToAllPlayersAround();
    }
    
    private void sendEquipmentDataToPlayerToAllPlayersAround() {
        TargetPoint p = new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 512);
        PacketHandler.networkWrapper.sendToAllAround(new MessageServerEntityEquipmentData(equipmentData, entity.getEntityId()), p);
    }
    
    public void sendEquipmentDataToPlayer(EntityPlayerMP targetPlayer) {
        PacketHandler.networkWrapper.sendTo(new MessageServerEntityEquipmentData(equipmentData, entity.getEntityId()), targetPlayer);
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
        this.equipmentData.saveNBTData(compound);
        this.skinInventory.saveItemsToNBT(compound);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        this.equipmentData.loadNBTData(compound);
        this.skinInventory.loadItemsFromNBT(compound);
    }

    @Override
    public void init(Entity entity, World world) {
    }
    
    public static final void register(Entity entity) {
        entity.registerExtendedProperties(TAG_EXT_PROP_NAME, new ExPropsEntityEquipmentData(entity));
    }
    
    private static final ExPropsEntityEquipmentData get(Entity entity) {
        return (ExPropsEntityEquipmentData) entity.getExtendedProperties(TAG_EXT_PROP_NAME);
    }
    
    public static final ExPropsEntityEquipmentData getExtendedPropsForEntity(Entity entity) {
        return ExPropsEntityEquipmentData.get(entity);
    }
}
