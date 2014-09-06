package riskyken.armourersWorkshop.common.customarmor.data;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.common.customarmor.ArmourType;

public class CustomArmourItemData {
    
    private static final String TAG_CUSTOM_NAME = "customName";
    private static final String TAG_TYPE = "type";
    private static final String TAG_PARTS = "parts";
    
    private ArmourType type;
    private ArrayList<CustomArmourPartData> parts;
    
    public CustomArmourItemData(ArmourType type, ArrayList<CustomArmourPartData> parts) {
        this.type = type;
        this.parts = parts;
    }

    public CustomArmourItemData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomArmourItemData(NBTTagCompound compound) {
        readFromNBT(compound);
    }
    
    public CustomArmourItemData(DataInputStream stream) throws IOException {
        readFromStream(stream);
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        type = ArmourType.getOrdinal(buf.readByte());
        int size = buf.readByte();
        parts = new ArrayList<CustomArmourPartData>();
        for (int i = 0; i < size; i++) {
            parts.add(new CustomArmourPartData(buf));
        }
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_TYPE, (byte) type.ordinal());
        
        NBTTagList blockData = new NBTTagList();
        for (int i = 0; i < parts.size(); i++) {
            CustomArmourPartData data = parts.get(i);
            NBTTagCompound dataNBT = new NBTTagCompound();
            data.writeToNBT(dataNBT);
            blockData.appendTag(dataNBT);
        }
        compound.setTag(TAG_PARTS, blockData);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        type = ArmourType.getOrdinal(compound.getByte(TAG_TYPE));
        
        NBTTagList blockData = compound.getTagList(TAG_PARTS, NBT.TAG_COMPOUND);
        parts = new ArrayList<CustomArmourPartData>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            parts.add(new CustomArmourPartData(data));
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(type.ordinal());
        stream.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream) throws IOException {
        type = ArmourType.getOrdinal(stream.readByte());
        int size = stream.readByte();
        parts = new ArrayList<CustomArmourPartData>();
        for (int i = 0; i < size; i++) {
            parts.add(new CustomArmourPartData(stream));
        }
    }
    
    public ArmourType getType() {
        return type;
    }
    
    public ArrayList<CustomArmourPartData> getParts() {
        return parts;
    }
}
