package riskyken.armourersWorkshop.common.skin.data;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.skin.data.ISkin;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.exception.NewerFileVersionException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Skin implements ISkin {
    
    public static final int FILE_VERSION = 6;
    
    private String authorName;
    private String customName;
    private String tags;
    private ISkinType equipmentSkinType;
    private ArrayList<SkinPart> parts;
    
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
    
    public Skin(String authorName, String customName, String tags, ISkinType equipmentSkinType, ArrayList<SkinPart> equipmentSkinParts) {
        this.authorName = authorName;
        this.customName = customName;
        this.tags = tags;
        this.equipmentSkinType = equipmentSkinType;
        this.parts = equipmentSkinParts;
    }

    @SideOnly(Side.CLIENT)
    public void cleanUpDisplayLists() {
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).cleanUpDisplayLists();
        }
    }
    
    public Skin(ByteBuf buf) {
        readFromBuf(buf);
    }
    
    public Skin(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        readFromStream(stream);
    }
    
    public void writeToBuf(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.authorName);
        ByteBufUtils.writeUTF8String(buf, this.customName);
        ByteBufUtils.writeUTF8String(buf, this.tags);
        ByteBufUtils.writeUTF8String(buf, this.equipmentSkinType.getRegistryName());
        buf.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        this.authorName = ByteBufUtils.readUTF8String(buf);
        this.customName = ByteBufUtils.readUTF8String(buf);
        this.tags = ByteBufUtils.readUTF8String(buf);
        this.equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(ByteBufUtils.readUTF8String(buf));
        int size = buf.readByte();
        parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            parts.add(new SkinPart(buf));
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(FILE_VERSION);
        stream.writeUTF(this.authorName);
        stream.writeUTF(this.customName);
        stream.writeUTF(this.tags);
        stream.writeUTF(this.equipmentSkinType.getRegistryName());
        stream.writeByte(parts.size());
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        int fileVersion = stream.readInt();
        if (fileVersion > FILE_VERSION) {
            throw new NewerFileVersionException();
        }
        this.authorName = stream.readUTF();
        this.customName = stream.readUTF();
        if (!(fileVersion < 4)) {
            this.tags = stream.readUTF(); 
        } else {
            this.tags = "";
        }
        if (fileVersion < 5) {
            equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(stream.readByte() - 1);
        } else {
            equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(stream.readUTF());
        }
        
        if (equipmentSkinType == null) {
            throw new InvalidCubeTypeException();
        }
        
        int size = stream.readByte();
        parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            parts.add(new SkinPart(stream, fileVersion));
        }
    }
    
    public static String readSkinTypeNameFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
        int fileVersion = stream.readInt();
        if (fileVersion > FILE_VERSION) {
            throw new NewerFileVersionException();
        }
        String authorName = stream.readUTF();
        String customName = stream.readUTF();
        String tags = "";
        if (!(fileVersion < 4)) {
            tags = stream.readUTF(); 
        }
        String skinTypeName;
        
        if (fileVersion < 5) {
            ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(stream.readByte() - 1);
            if (skinType != null) {
                skinTypeName = skinType.getRegistryName();
            } else {
                skinTypeName = ""; 
            }
        } else {
            skinTypeName = stream.readUTF();
        }
        return skinTypeName;
    }
    
    @Override
    public ISkinType getSkinType() {
        return equipmentSkinType;
    }
    
    public ArrayList<SkinPart> getParts() {
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
        Skin other = (Skin) obj;
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
        if (equipmentSkinType != other.equipmentSkinType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CustomArmourItemData [authorName=" + authorName
                + ", customName=" + customName + ", type=" + equipmentSkinType.getName().toUpperCase() + "]";
    }
}
