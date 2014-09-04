package riskyken.armourersWorkshop.common.customarmor.data;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;

public class CustomArmourItemData {
    
    private static final String TAG_CUSTOM_NAME = "customName";
    private static final String TAG_TYPE = "type";
    private static final String TAG_PARTS = "parts";
    
    private ArmourerType type;
    private ArrayList<CustomArmourPartData> parts;
    
    public CustomArmourItemData(ArmourerType type, ArrayList<CustomArmourPartData> parts) {
        this.type = type;
        this.parts = parts;
    }

    public CustomArmourItemData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomArmourItemData(NBTTagCompound compound) {
        readFromNBT(compound);
    }
    
    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        type = ArmourerType.getOrdinal(buf.readByte());
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
        type = ArmourerType.getOrdinal(compound.getByte(TAG_TYPE));
        
        NBTTagList blockData = compound.getTagList(TAG_PARTS, NBT.TAG_COMPOUND);
        parts = new ArrayList<CustomArmourPartData>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            parts.add(new CustomArmourPartData(data));
        }
    }
    
    public ArmourerType getType() {
        return type;
    }
    
    public ArrayList<CustomArmourPartData> getParts() {
        return parts;
    }
}
