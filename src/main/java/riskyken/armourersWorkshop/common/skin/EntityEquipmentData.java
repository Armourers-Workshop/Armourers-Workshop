package riskyken.armourersWorkshop.common.skin;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import cpw.mods.fml.common.network.ByteBufUtils;

public class EntityEquipmentData implements IEntityEquipment {
    
    private static final String TAG_SKIN_LIST = "skinList";
    private static final String TAG_SKIN_TYPE = "skinType";
    private static final String TAG_EQUIPMENT_ID = "equipmentId";
    
    private HashMap<String, Integer> equipment = new HashMap<String, Integer>();
    
    public EntityEquipmentData() {
    }
    
    public EntityEquipmentData(ByteBuf buf) {
        fromBytes(buf);
    }
    
    @Override
    public void addEquipment(ISkinType skinType, int equipmentId) {
        String key = skinType.getRegistryName();
        if (this.equipment.containsKey(key)) {
            this.equipment.remove(key);
        }
        this.equipment.put(key, equipmentId);
    }
    
    @Override
    public void removeEquipment(ISkinType skinType) {
        String key = skinType.getRegistryName();
        if (this.equipment.containsKey(key)) {
            this.equipment.remove(key);
        }
    }
    
    @Override
    public boolean haveEquipment(ISkinType skinType) {
        String key = skinType.getRegistryName();
        return this.equipment.containsKey(key);
    }
    
    @Override
    public int getEquipmentId(ISkinType skinType) {
        String key = skinType.getRegistryName();
        if (this.equipment.containsKey(key)) {
            return this.equipment.get(key);
        }
        return 0;
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < equipment.size(); i++) {
            String skinName = (String) equipment.keySet().toArray()[i];
            int equipmentId = equipment.get(skinName);
            NBTTagCompound item = new NBTTagCompound();
            item.setString(TAG_SKIN_TYPE, skinName);
            item.setInteger(TAG_EQUIPMENT_ID, equipmentId);
            items.appendTag(item);
        }
        compound.setTag(TAG_SKIN_LIST, items);
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagList itemsList = compound.getTagList(TAG_SKIN_LIST, NBT.TAG_COMPOUND);
        equipment.clear();
        for (int i = 0; i < itemsList.tagCount(); i++) {
            NBTTagCompound item = (NBTTagCompound)itemsList.getCompoundTagAt(i);
            String skinName = item.getString(TAG_SKIN_TYPE);
            int equipmentId = item.getInteger(TAG_EQUIPMENT_ID);
            equipment.put(skinName, equipmentId);
        }
    }
    
    public void toBytes(ByteBuf buf) {
        buf.writeByte(equipment.size());
        for (int i = 0; i < equipment.size(); i++) {
            String skinName = (String) equipment.keySet().toArray()[i];
            int equipmentId = equipment.get(skinName);
            ByteBufUtils.writeUTF8String(buf, skinName);
            buf.writeInt(equipmentId);
        }
    }
    
    public void fromBytes(ByteBuf buf) {
        int itemCount = buf.readByte();
        equipment.clear();
        for (int i = 0; i < itemCount; i++) {
            String skinName = ByteBufUtils.readUTF8String(buf);
            int equipmentId = buf.readInt();
            equipment.put(skinName, equipmentId);
        }
    }

    public boolean hasCustomEquipment() {
        return this.equipment.size() > 0;
    }
}
