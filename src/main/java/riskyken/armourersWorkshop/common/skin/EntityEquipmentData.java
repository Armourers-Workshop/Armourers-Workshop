package riskyken.armourersWorkshop.common.skin;

import java.util.HashMap;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

public class EntityEquipmentData implements IEntityEquipment {
    
    private static final String TAG_SKIN_LIST = "skinList";
    private static final String TAG_SKIN_TYPE = "skinType";
    private static final String TAG_EQUIPMENT_ID = "equipmentId";
    
    private int slotCount = 1;
    private HashMap<String, Integer> skinId = new HashMap<String, Integer>();
    private HashMap<String, ISkinDye> skinDye = new HashMap<String, ISkinDye>();
    
    public static EntityEquipmentData readFromByteBuf(ByteBuf buf) {
        int slotCount = buf.readByte();
        EntityEquipmentData eed = new EntityEquipmentData(slotCount);
        eed.fromBytes(buf);
        return eed;
    }
    
    public static void writeToByteBuf(EntityEquipmentData eed,ByteBuf buf) {
        eed.toBytes(buf);
    }
    
    public EntityEquipmentData(int slotCount) {
        this.slotCount = slotCount;
    }
    
    @Override
    public void addEquipment(ISkinType skinType, int slotIndex, ISkinPointer skinPointer) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        skinId.remove(key);
        skinDye.remove(key);
        
        skinId.put(key, skinPointer.getSkinId());
        if (skinPointer.getSkinDye() != null) {
            skinDye.put(key, skinPointer.getSkinDye());
        }
    }
    
    @Override
    public void removeEquipment(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        skinId.remove(key);
        skinDye.remove(key);
    }
    
    @Override
    public boolean haveEquipment(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        return this.skinId.containsKey(key);
    }
    
    @Override
    public int getEquipmentId(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        if (this.skinId.containsKey(key)) {
            return this.skinId.get(key);
        }
        return 0;
    }
    
    @Override
    public ISkinPointer getSkinPointer(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        if (this.skinId.containsKey(key)) {
            int skinId = this.skinId.get(key);
            ISkinDye skinDye = getSkinDye(skinType, slotIndex);
            if (skinDye != null) {
                return new SkinPointer(skinType, skinId, new SkinDye(skinDye));
            } else {
                return new SkinPointer(skinType, skinId);
            }
        }
        return null;
    }
    
    @Override
    public ISkinDye getSkinDye(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        return skinDye.get(key);
    }
    
    @Override
    public int getNumberOfSlots() {
        return slotCount;
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < skinId.size(); i++) {
            String skinName = (String) skinId.keySet().toArray()[i];
            int equipmentId = skinId.get(skinName);
            NBTTagCompound item = new NBTTagCompound();
            item.setString(TAG_SKIN_TYPE, skinName);
            item.setInteger(TAG_EQUIPMENT_ID, equipmentId);
            items.appendTag(item);
        }
        compound.setTag(TAG_SKIN_LIST, items);
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagList itemsList = compound.getTagList(TAG_SKIN_LIST, NBT.TAG_COMPOUND);
        skinId.clear();
        for (int i = 0; i < itemsList.tagCount(); i++) {
            NBTTagCompound item = (NBTTagCompound)itemsList.getCompoundTagAt(i);
            String skinName = item.getString(TAG_SKIN_TYPE);
            int equipmentId = item.getInteger(TAG_EQUIPMENT_ID);
            skinId.put(skinName, equipmentId);
        }
    }
    
    private void toBytes(ByteBuf buf) {
        buf.writeByte(slotCount);
        buf.writeByte(skinId.size());
        for (int i = 0; i < skinId.size(); i++) {
            String skinName = (String) skinId.keySet().toArray()[i];
            int equipmentId = skinId.get(skinName);
            ISkinDye dye = skinDye.get(skinName);
            ByteBufUtils.writeUTF8String(buf, skinName);
            buf.writeInt(equipmentId);
            dye.writeToBuf(buf);
        }
    }
    
    private void fromBytes(ByteBuf buf) {
        int itemCount = buf.readByte();
        skinId.clear();
        for (int i = 0; i < itemCount; i++) {
            String skinName = ByteBufUtils.readUTF8String(buf);
            int equipmentId = buf.readInt();
            skinId.put(skinName, equipmentId);
            SkinDye dye = new SkinDye();
            dye.readFromBuf(buf);
            skinDye.put(skinName, dye);
        }
    }

    public boolean hasCustomEquipment() {
        return this.skinId.size() > 0;
    }
}
