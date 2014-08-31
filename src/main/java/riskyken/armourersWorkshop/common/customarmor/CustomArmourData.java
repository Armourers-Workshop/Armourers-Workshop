package riskyken.armourersWorkshop.common.customarmor;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class CustomArmourData {

    private static final String TAG_TYPE = "type";
    private static final String TAG_PART = "part";
    private static final String TAG_ARMOUR_DATA = "armourData";
    
    private ArrayList<ArmourBlockData> armourData;
    private ArmourerType type;
    private ArmourPart part;

    public CustomArmourData(ArrayList armourData, ArmourerType type,
            ArmourPart part) {
        this.armourData = armourData;
        this.type = type;
        this.part = part;
    }

    public CustomArmourData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomArmourData(NBTTagCompound compound) {
        readFromNBT(compound);
    }

    public ArmourerType getArmourType() {
        return this.type;
    }

    public ArmourPart getArmourPart() {
        return this.part;
    }

    public ArrayList<ArmourBlockData> getArmourData() {
        return armourData;
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeByte(part.ordinal());
        buf.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        type = ArmourerType.getOrdinal(buf.readByte());
        part = ArmourPart.getOrdinal(buf.readByte());
        int size = buf.readInt();
        armourData = new ArrayList<ArmourBlockData>();
        for (int i = 0; i < size; i++) {
            armourData.add(new ArmourBlockData(buf));
        }
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_TYPE, (byte) type.ordinal());
        compound.setByte(TAG_PART, (byte) part.ordinal());
        
        NBTTagList blockData = new NBTTagList();
        for (int i = 0; i < armourData.size(); i++) {
            ArmourBlockData data = armourData.get(i);
            NBTTagCompound dataNBT = new NBTTagCompound();
            data.writeToNBT(dataNBT);
            blockData.appendTag(dataNBT);
        }
        compound.setTag(TAG_ARMOUR_DATA, blockData);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        type = ArmourerType.getOrdinal(compound.getByte(TAG_TYPE));
        part = ArmourPart.getOrdinal(compound.getByte(TAG_PART));
        
        NBTTagList blockData = compound.getTagList(TAG_ARMOUR_DATA, NBT.TAG_COMPOUND);
        armourData = new ArrayList<ArmourBlockData>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            armourData.add(new ArmourBlockData(data));
        }
    }
}
