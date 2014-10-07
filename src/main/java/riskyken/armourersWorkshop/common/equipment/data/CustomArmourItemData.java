package riskyken.armourersWorkshop.common.equipment.data;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.network.ByteBufUtils;

public class CustomArmourItemData {
    
    private static final String TAG_TYPE = "type";
    private static final String TAG_PARTS = "parts";
    
    private static final int FILE_VERSION = 1;
    
    private String authorName;
    private String customName;
    private EnumArmourType type;
    private ArrayList<CustomArmourPartData> parts;
    
    public CustomArmourItemData(String authorName, String customName, EnumArmourType type, ArrayList<CustomArmourPartData> parts) {
        this.authorName = authorName;
        this.customName = customName;
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
        ByteBufUtils.writeUTF8String(buf, this.authorName);
        ByteBufUtils.writeUTF8String(buf, this.customName);
        buf.writeByte(type.ordinal());
        buf.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        this.authorName = ByteBufUtils.readUTF8String(buf);
        this.customName = ByteBufUtils.readUTF8String(buf);
        type = EnumArmourType.getOrdinal(buf.readByte());
        int size = buf.readByte();
        parts = new ArrayList<CustomArmourPartData>();
        for (int i = 0; i < size; i++) {
            parts.add(new CustomArmourPartData(buf));
        }
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setString(LibCommonTags.TAG_AUTHOR_NAME, this.authorName);
        compound.setString(LibCommonTags.TAG_CUSTOM_NAME, this.customName);
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
    
    public void writeClientDataToNBT(NBTTagCompound compound) {
        compound.setString(LibCommonTags.TAG_AUTHOR_NAME, this.authorName);
        compound.setString(LibCommonTags.TAG_CUSTOM_NAME, this.customName);
        compound.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, this.hashCode());
    }
    
    public static boolean hasArmourData(NBTTagCompound compound) {
        return compound.hasKey(TAG_PARTS);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        this.authorName = compound.getString(LibCommonTags.TAG_AUTHOR_NAME);
        this.customName = compound.getString(LibCommonTags.TAG_CUSTOM_NAME);
        type = EnumArmourType.getOrdinal(compound.getByte(TAG_TYPE));
        NBTTagList blockData = compound.getTagList(TAG_PARTS, NBT.TAG_COMPOUND);
        parts = new ArrayList<CustomArmourPartData>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            parts.add(new CustomArmourPartData(data));
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(FILE_VERSION);
        stream.writeUTF(this.authorName);
        stream.writeUTF(this.customName);
        stream.writeByte(type.ordinal());
        stream.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream) throws IOException {
        int fileVersion = stream.readInt();
        if (fileVersion > FILE_VERSION) {
            ModLogger.log(Level.ERROR, "Can not load custom armour, was saved in newer version.");
        }
        this.authorName = stream.readUTF();
        this.customName = stream.readUTF();
        type = EnumArmourType.getOrdinal(stream.readByte());
        int size = stream.readByte();
        parts = new ArrayList<CustomArmourPartData>();
        for (int i = 0; i < size; i++) {
            parts.add(new CustomArmourPartData(stream));
        }
    }
    
    public EnumArmourType getType() {
        return type;
    }
    
    public ArrayList<CustomArmourPartData> getParts() {
        return parts;
    }
    
    public String getCustomName() {
        return customName;
    }

    @Override
    public int hashCode() {
        String result = this.toString();
        for (int i = 0; i < parts.size(); i++) {
            result += parts.get(i).toString();
        }
        return result.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomArmourItemData other = (CustomArmourItemData) obj;
        if (authorName == null) {
            if (other.authorName != null)
                return false;
        } else if (!authorName.equals(other.authorName))
            return false;
        if (customName == null) {
            if (other.customName != null)
                return false;
        } else if (!customName.equals(other.customName))
            return false;
        if (parts == null) {
            if (other.parts != null)
                return false;
        } else if (!parts.equals(other.parts))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CustomArmourItemData [authorName=" + authorName
                + ", customName=" + customName + ", type=" + type + "]";
    }
}
