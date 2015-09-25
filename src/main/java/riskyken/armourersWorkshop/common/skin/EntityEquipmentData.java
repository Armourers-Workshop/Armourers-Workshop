package riskyken.armourersWorkshop.common.skin;

import java.util.HashMap;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public class EntityEquipmentData implements IEntityEquipment {
    
    private static final String TAG_SKIN_LIST = "skinList";
    private static final String TAG_SKIN_TYPE = "skinType";
    private static final String TAG_EQUIPMENT_ID = "equipmentId";
    
    private HashMap<String, Integer> skinId = new HashMap<String, Integer>();
    private HashMap<String, ISkinDye> skinDye = new HashMap<String, ISkinDye>();
    
    public EntityEquipmentData() {
    }
    
    public EntityEquipmentData(ByteBuf buf) {
        fromBytes(buf);
    }
    
    @Override
    public void addEquipment(ISkinType skinType, ISkinPointer skinPointer) {
        String key = skinType.getRegistryName();
        skinId.remove(key);
        skinDye.remove(key);
        
        skinId.put(key, skinPointer.getSkinId());
        if (skinPointer.getSkinDye() != null) {
            skinDye.put(key, skinPointer.getSkinDye());
        }
    }
    
    @Override
    public void removeEquipment(ISkinType skinType) {
        String key = skinType.getRegistryName();
        skinId.remove(key);
        skinDye.remove(key);
    }
    
    @Override
    public boolean haveEquipment(ISkinType skinType) {
        String key = skinType.getRegistryName();
        return this.skinId.containsKey(key);
    }
    
    @Override
    public int getEquipmentId(ISkinType skinType) {
        String key = skinType.getRegistryName();
        if (this.skinId.containsKey(key)) {
            return this.skinId.get(key);
        }
        return 0;
    }
    
    @Override
    public ISkinDye getSkinDye(ISkinType skinType) {
        String key = skinType.getRegistryName();
        return skinDye.get(key);
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
    
    public void toBytes(ByteBuf buf) {
        buf.writeByte(skinId.size());
        for (int i = 0; i < skinId.size(); i++) {
            String skinName = (String) skinId.keySet().toArray()[i];
            int equipmentId = skinId.get(skinName);
            ByteBufUtils.writeUTF8String(buf, skinName);
            buf.writeInt(equipmentId);
        }
    }
    
    public void fromBytes(ByteBuf buf) {
        int itemCount = buf.readByte();
        skinId.clear();
        for (int i = 0; i < itemCount; i++) {
            String skinName = ByteBufUtils.readUTF8String(buf);
            int equipmentId = buf.readInt();
            skinId.put(skinName, equipmentId);
        }
    }

    public boolean hasCustomEquipment() {
        return this.skinId.size() > 0;
    }
}
