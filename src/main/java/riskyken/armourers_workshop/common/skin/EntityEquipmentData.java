package riskyken.armourers_workshop.common.skin;

import java.util.HashMap;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourers_workshop.api.common.skin.IEntityEquipment;
import riskyken.armourers_workshop.api.common.skin.data.ISkinDye;
import riskyken.armourers_workshop.api.common.skin.data.ISkinPointer;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;

public class EntityEquipmentData implements IEntityEquipment {
    
    private static final String TAG_SKIN_LIST = "skinList";
    private static final String TAG_SKIN_TYPE = "skinType";
    private static final String TAG_EQUIPMENT_ID = "equipmentId";
    
    private HashMap<String, ISkinPointer> skinPointerMap = new HashMap<String, ISkinPointer>();
    
    public static EntityEquipmentData readFromByteBuf(ByteBuf buf) {
        EntityEquipmentData eed = new EntityEquipmentData();
        eed.fromBytes(buf);
        return eed;
    }
    
    public static void writeToByteBuf(EntityEquipmentData eed,ByteBuf buf) {
        eed.toBytes(buf);
    }
    
    @Override
    public void addEquipment(ISkinType skinType, int slotIndex, ISkinPointer skinPointer) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        skinPointerMap.remove(key);
        skinPointerMap.put(key, skinPointer);
    }
    
    @Override
    public void removeEquipment(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        skinPointerMap.remove(key);
    }
    
    @Override
    public boolean haveEquipment(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        return skinPointerMap.containsKey(key);
    }
    
    @Override
    public ISkinPointer getSkinPointer(ISkinType skinType, int slotIndex) {
        String key = skinType.getRegistryName() + ":" + slotIndex;
        return skinPointerMap.get(key);
    }
    
    private void toBytes(ByteBuf buf) {
        buf.writeByte(skinPointerMap.size());
        for (int i = 0; i < skinPointerMap.size(); i++) {
            String skinName = (String) skinPointerMap.keySet().toArray()[i];
            NBTTagCompound compound = new NBTTagCompound();
            SkinPointer skinPointer = (SkinPointer) skinPointerMap.get(skinName);
            skinPointer.writeToCompound(compound);
            ByteBufUtils.writeTag(buf, compound);
            ByteBufUtils.writeUTF8String(buf, skinName);
        }
    }
    
    public void clear() {
        skinPointerMap.clear();
    }
    
    private void fromBytes(ByteBuf buf) {
        int itemCount = buf.readByte();
        skinPointerMap.clear();
        for (int i = 0; i < itemCount; i++) {
            NBTTagCompound compound = ByteBufUtils.readTag(buf);
            String skinKey = ByteBufUtils.readUTF8String(buf);
            SkinPointer skinPointer = new SkinPointer();
            skinPointer.readFromCompound(compound);
            skinPointerMap.put(skinKey, skinPointer);
        }
    }

    public boolean hasCustomEquipment() {
        return skinPointerMap.size() > 0;
    }
    
    @Deprecated
    @Override
    public int getNumberOfSlots() {
        return 0;
    }
    
    @Deprecated
    @Override
    public int getEquipmentId(ISkinType skinType, int slotIndex) {
        return 0;
    }
    
    @Deprecated
    @Override
    public ISkinDye getSkinDye(ISkinType skinType, int slotIndex) {
        return null;
    }
}
