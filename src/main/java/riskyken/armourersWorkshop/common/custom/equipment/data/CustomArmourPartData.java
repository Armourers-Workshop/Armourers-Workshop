package riskyken.armourersWorkshop.common.custom.equipment.data;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourPart;

public class CustomArmourPartData {
    
    private static final String TAG_BLOCK_DATA = "blockData";
    private static final String TAG_PART = "part";
    
    private ArrayList<CustomEquipmentBlockData> armourData;
    private ArmourPart part;

    public CustomArmourPartData(ArrayList armourData,
            ArmourPart part) {
        this.armourData = armourData;
        this.part = part;
    }

    public CustomArmourPartData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomArmourPartData(NBTTagCompound compound) {
        readFromNBT(compound);
    }

    public CustomArmourPartData(DataInputStream stream) throws IOException {
        readFromStream(stream);
    }

    public ArmourPart getArmourPart() {
        return this.part;
    }

    public ArrayList<CustomEquipmentBlockData> getArmourData() {
        return armourData;
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(part.ordinal());
        buf.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        part = ArmourPart.getOrdinal(buf.readByte());
        int size = buf.readInt();
        armourData = new ArrayList<CustomEquipmentBlockData>();
        for (int i = 0; i < size; i++) {
            armourData.add(new CustomEquipmentBlockData(buf));
        }
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_PART, (byte) part.ordinal());
        
        NBTTagList blockData = new NBTTagList();
        for (int i = 0; i < armourData.size(); i++) {
            CustomEquipmentBlockData data = armourData.get(i);
            NBTTagCompound dataNBT = new NBTTagCompound();
            data.writeToNBT(dataNBT);
            blockData.appendTag(dataNBT);
        }
        compound.setTag(TAG_BLOCK_DATA, blockData);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        part = ArmourPart.getOrdinal(compound.getByte(TAG_PART));
        
        NBTTagList blockData = compound.getTagList(TAG_BLOCK_DATA, NBT.TAG_COMPOUND);
        armourData = new ArrayList<CustomEquipmentBlockData>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            armourData.add(new CustomEquipmentBlockData(data));
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(part.ordinal());
        stream.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream) throws IOException {
        part = ArmourPart.getOrdinal(stream.readByte());
        int size = stream.readInt();
        armourData = new ArrayList<CustomEquipmentBlockData>();
        for (int i = 0; i < size; i++) {
            armourData.add(new CustomEquipmentBlockData(stream));
        }
    }
}
