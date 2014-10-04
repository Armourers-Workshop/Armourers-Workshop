package riskyken.armourersWorkshop.common.custom.equipment;

import io.netty.buffer.ByteBuf;

import java.util.BitSet;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;

public class EntityEquipmentData {
    
    private static final String TAG_HAVE_EQUIPMENT = "haveEquipment";
    private static final String TAG_EQUIPMENT_ID = "equipmentId";
    
    private BitSet haveEquipment = new BitSet(5);
    private int[] equipmentId = new int[5];
    
    public EntityEquipmentData() {}
    
    public EntityEquipmentData(ByteBuf buf) {
        fromBytes(buf);
    }
    
    public void addEquipment(ArmourType type, int equipmentId) {
        this.equipmentId[type.ordinal() - 1] = equipmentId;
        this.haveEquipment.set(type.ordinal() - 1, true);
    }
    
    public void removeEquipment(ArmourType type) {
        this.haveEquipment.set(type.ordinal() - 1, false);
    }
    
    public boolean haveEquipment(ArmourType type) {
        return this.haveEquipment.get(type.ordinal() - 1);
    }
    
    public int getEquipmentId(ArmourType type) {
        return this.equipmentId[type.ordinal() - 1];
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        for (int i = 0; i < 5; i++) {
            compound.setBoolean(TAG_HAVE_EQUIPMENT + i, this.haveEquipment.get(i));
            compound.setInteger(TAG_EQUIPMENT_ID + i, this.equipmentId[i]);
        }
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        for (int i = 0; i < 5; i++) {
            this.haveEquipment.set(i, compound.getBoolean(TAG_HAVE_EQUIPMENT + i));
            this.equipmentId[i] = compound.getInteger(TAG_EQUIPMENT_ID + i);
        }
    }
    
    public void toBytes(ByteBuf buf) {
        for (int i = 0; i < 5; i++) {
            buf.writeBoolean(this.haveEquipment.get(i));
            buf.writeInt(this.equipmentId[i]);
        }
    }
    
    private void fromBytes(ByteBuf buf) {
        for (int i = 0; i < 5; i++) {
            this.haveEquipment.set(i, buf.readBoolean());
            this.equipmentId[i] = buf.readInt();
        }
    }
}
