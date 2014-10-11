package riskyken.armourersWorkshop.common.equipment;

import io.netty.buffer.ByteBuf;

import java.util.BitSet;

import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;

public class EntityEquipmentData implements IEntityEquipment {
    
    private static final String TAG_HAVE_EQUIPMENT = "haveEquipment";
    private static final String TAG_EQUIPMENT_ID = "equipmentId";
    
    private BitSet haveEquipment = new BitSet(6);
    private int[] equipmentId = new int[6];
    
    public EntityEquipmentData() {}
    
    public EntityEquipmentData(ByteBuf buf) {
        fromBytes(buf);
    }
    
    @Override
    public void addEquipment(EnumEquipmentType type, int equipmentId) {
        this.equipmentId[type.ordinal() - 1] = equipmentId;
        this.haveEquipment.set(type.ordinal() - 1, true);
    }
    
    @Override
    public void removeEquipment(EnumEquipmentType type) {
        this.haveEquipment.set(type.ordinal() - 1, false);
    }
    
    @Override
    public boolean haveEquipment(EnumEquipmentType type) {
        return this.haveEquipment.get(type.ordinal() - 1);
    }
    
    @Override
    public int getEquipmentId(EnumEquipmentType type) {
        return this.equipmentId[type.ordinal() - 1];
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        for (int i = 0; i < 6; i++) {
            compound.setBoolean(TAG_HAVE_EQUIPMENT + i, this.haveEquipment.get(i));
            compound.setInteger(TAG_EQUIPMENT_ID + i, this.equipmentId[i]);
        }
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        for (int i = 0; i < 6; i++) {
            this.haveEquipment.set(i, compound.getBoolean(TAG_HAVE_EQUIPMENT + i));
            this.equipmentId[i] = compound.getInteger(TAG_EQUIPMENT_ID + i);
        }
    }
    
    public void toBytes(ByteBuf buf) {
        for (int i = 0; i < 6; i++) {
            buf.writeBoolean(this.haveEquipment.get(i));
            buf.writeInt(this.equipmentId[i]);
        }
    }
    
    public void fromBytes(ByteBuf buf) {
        for (int i = 0; i < 6; i++) {
            this.haveEquipment.set(i, buf.readBoolean());
            this.equipmentId[i] = buf.readInt();
        }
    }

    public boolean hasCustomEquipment() {
        for (int i = 0; i < 6; i++) {
            if (this.haveEquipment.get(i)) {
                return true;
            }
        }
        return false;
    }
}
