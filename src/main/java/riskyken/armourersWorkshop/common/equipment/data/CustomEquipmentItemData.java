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

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.equipment.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.equipment.cubes.ICube;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CustomEquipmentItemData {
    
    private static final String TAG_TYPE = "type";
    private static final String TAG_PARTS = "parts";
    private static final String TAG_TAGS = "tags";
    
    public static final int FILE_VERSION = 4;
    
    private String authorName;
    private String customName;
    private String tags;
    private EnumEquipmentType type;
    private ArrayList<CustomEquipmentPartData> parts;
    
    private int timeFromRender = 0;
    
    public void onRender() {
        timeFromRender = 0;
    }
    
    public void tick() {
        timeFromRender++;
    }
    
    public boolean needsCleanup() {
        if (timeFromRender > ConfigHandler.modelCacheTime) {
            return true;
        }
        return false;
    }
    
    public CustomEquipmentItemData(String authorName, String customName, String tags, EnumEquipmentType type, ArrayList<CustomEquipmentPartData> parts) {
        this.authorName = authorName;
        this.customName = customName;
        this.tags = tags;
        this.type = type;
        this.parts = parts;
    }

    @SideOnly(Side.CLIENT)
    public void cleanUpDisplayLists() {
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).cleanUpDisplayLists();
        }
    }
    
    public CustomEquipmentItemData(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public CustomEquipmentItemData(NBTTagCompound compound) {
        readFromNBT(compound);
    }
    
    public CustomEquipmentItemData(DataInputStream stream) throws IOException {
        readFromStream(stream);
    }
    
    public void writeToBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.authorName);
        ByteBufUtils.writeUTF8String(buf, this.customName);
        ByteBufUtils.writeUTF8String(buf, this.tags);
        buf.writeByte(type.ordinal());
        buf.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        this.authorName = ByteBufUtils.readUTF8String(buf);
        this.customName = ByteBufUtils.readUTF8String(buf);
        this.tags = ByteBufUtils.readUTF8String(buf);
        type = EnumEquipmentType.getOrdinal(buf.readByte());
        int size = buf.readByte();
        parts = new ArrayList<CustomEquipmentPartData>();
        for (int i = 0; i < size; i++) {
            parts.add(new CustomEquipmentPartData(buf));
        }
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setString(LibCommonTags.TAG_AUTHOR_NAME, this.authorName);
        compound.setString(LibCommonTags.TAG_CUSTOM_NAME, this.customName);
        compound.setString(LibCommonTags.TAG_CUSTOM_NAME, this.customName);
        compound.setString(TAG_TAGS, this.tags);
        compound.setByte(TAG_TYPE, (byte) type.ordinal());
        NBTTagList blockData = new NBTTagList();
        for (int i = 0; i < parts.size(); i++) {
            CustomEquipmentPartData data = parts.get(i);
            NBTTagCompound dataNBT = new NBTTagCompound();
            data.writeToNBT(dataNBT);
            blockData.appendTag(dataNBT);
        }
        compound.setTag(TAG_PARTS, blockData);
    }
    
    public void writeClientDataToNBT(NBTTagCompound compound) {
        compound.setInteger(LibCommonTags.TAG_EQUIPMENT_ID, this.hashCode());
    }
    
    public static boolean hasArmourData(NBTTagCompound compound) {
        return compound.hasKey(TAG_PARTS);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        this.authorName = compound.getString(LibCommonTags.TAG_AUTHOR_NAME);
        this.customName = compound.getString(LibCommonTags.TAG_CUSTOM_NAME);
        this.tags = compound.getString(TAG_TAGS);
        type = EnumEquipmentType.getOrdinal(compound.getByte(TAG_TYPE));
        NBTTagList blockData = compound.getTagList(TAG_PARTS, NBT.TAG_COMPOUND);
        parts = new ArrayList<CustomEquipmentPartData>();
        for (int i = 0; i < blockData.tagCount(); i++) {
            NBTTagCompound data = (NBTTagCompound)blockData.getCompoundTagAt(i);
            parts.add(new CustomEquipmentPartData(data));
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(FILE_VERSION);
        stream.writeUTF(this.authorName);
        stream.writeUTF(this.customName);
        stream.writeUTF(this.tags);
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
        if (!(fileVersion < 4)) {
            this.tags = stream.readUTF(); 
        } else {
            this.tags = "";
        }
        type = EnumEquipmentType.getOrdinal(stream.readByte());
        int size = stream.readByte();
        parts = new ArrayList<CustomEquipmentPartData>();
        for (int i = 0; i < size; i++) {
            parts.add(new CustomEquipmentPartData(stream, fileVersion));
        }
    }
    
    public EnumEquipmentType getType() {
        return type;
    }
    
    public ArrayList<CustomEquipmentPartData> getParts() {
        return parts;
    }
    
    public String getCustomName() {
        return customName;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public int getTotalCubes() {
        int totalCubes = 0;
        for (int i = 0; i < CubeRegistry.INSTANCE.getTotalCubes(); i++) {
            Class<? extends ICube> cubeClass = CubeRegistry.INSTANCE.getCubeFormId((byte) i);
            totalCubes += getTotalOfCubeType(cubeClass);
        }
        return totalCubes;
    }
    
    public int getTotalOfCubeType(Class<? extends ICube> cubeClass) {
        int totalOfCube = 0;
        int cubeId = CubeRegistry.INSTANCE.getIdForCubeClass(cubeClass);
        for (int i = 0; i < parts.size(); i++) {
            totalOfCube += parts.get(i).totalCubesInPart[cubeId];
        }
        return totalOfCube;
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
        CustomEquipmentItemData other = (CustomEquipmentItemData) obj;
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
