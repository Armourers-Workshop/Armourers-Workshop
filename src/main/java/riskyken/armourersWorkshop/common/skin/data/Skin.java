package riskyken.armourersWorkshop.common.skin.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkin;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPart;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.skin.SkinModelTexture;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.exception.NewerFileVersionException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class Skin implements ISkin {
    
    public static final int FILE_VERSION = 11;
    
    private String authorName;
    private String customName;
    private String tags;
    private ISkinType equipmentSkinType;
    private int[] paintData;
    private ArrayList<SkinPart> parts;
    public int requestId;
    private int lightHash = 0;
    
    @SideOnly(Side.CLIENT)
    public SkinModelTexture skinModelTexture;
    
    
    @SideOnly(Side.CLIENT)
    public int paintTextureId;
    
    private int[] averageR = new int[10];
    private int[] averageG = new int[10];
    private int[] averageB = new int[10];
    
    public void setAverageDyeValues(int[] r, int[] g, int[] b) {
        this.averageR = r;
        this.averageG = g;
        this.averageB = b;
    }
    
    @SideOnly(Side.CLIENT)
    public Rectangle3D getSkinBounds() {
        int x = 0;
        int y = 0;
        int z = 0;
        
        int width = 1;
        int height = 1;
        int depth = 1;
        
        for (int i = 0; i < getPartCount(); i++) {
            if (!(getSkinType() == SkinTypeRegistry.skinBow && i > 0)) {
                
                SkinPart skinPart = getParts().get(i);
                Rectangle3D bounds = skinPart.getPartBounds();
                
                width = Math.max(width, bounds.getWidth());
                height = Math.max(height, bounds.getHeight());
                depth = Math.max(depth, bounds.getDepth());
                
                x = bounds.getX();
                y = bounds.getY();
                z = bounds.getZ();
                
                if (hasPaintData()) {
                    IRectangle3D skinRec = skinPart.getPartType().getGuideSpace();
                    
                    width = Math.max(width, skinRec.getWidth());
                    height = Math.max(height, skinRec.getHeight());
                    depth = Math.max(depth, skinRec.getDepth());
                    
                    x = Math.max(x, skinRec.getX());
                    y = Math.max(y, skinRec.getY());
                    z = Math.max(z, skinRec.getZ());
                }

            }
        }
        
        if (getPartCount() == 0) {
            for (int i = 0; i < getSkinType().getSkinParts().size(); i++) {
                ISkinPartType part = getSkinType().getSkinParts().get(i);
                
                IRectangle3D skinRec = part.getGuideSpace();
                
                width = Math.max(width, skinRec.getWidth());
                height = Math.max(height, skinRec.getHeight());
                depth = Math.max(depth, skinRec.getDepth());
                
                x = Math.min(x, skinRec.getX());
                y = Math.max(y, skinRec.getY());
                z = Math.min(z, skinRec.getZ());
            }
        }
        
        if (hasPaintData()) {
        }
        
        return new Rectangle3D(x, y, z, width, height, depth);
    }
    
    public int[] getAverageDyeColour(int dyeNumber) {
        return new int[] { averageR[dyeNumber], averageG[dyeNumber], averageB[dyeNumber] };
    }
    
    
    /** Number of ticks from when this skin was last used. */
    private int ticksFromLastAccess = 0;
    
    public void onUsed() {
        ticksFromLastAccess = 0;
    }
    
    public void tick() {
        ticksFromLastAccess++;
    }
    
    public boolean needsCleanup(int maxUnusedTicks) {
        if (ticksFromLastAccess > maxUnusedTicks) {
            return true;
        }
        return false;
    }
    
    public Skin(String authorName, String customName, String tags, ISkinType equipmentSkinType, int[] paintData, ArrayList<SkinPart> equipmentSkinParts) {
        this.authorName = authorName;
        this.customName = customName;
        this.tags = tags;
        this.equipmentSkinType = equipmentSkinType;
        this.paintData = null;
        //Check if the paint data has any paint on it.
        if (paintData != null) {
            boolean validPaintData = false;
            for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                if (paintData[i] >>> 16 != 255) {
                    validPaintData = true;
                    break;
                }
            }
            if (validPaintData) {
                this.paintData = paintData;
            }
        }
        
        this.parts = equipmentSkinParts;
    }

    @SideOnly(Side.CLIENT)
    public void cleanUpDisplayLists() {
        for (int i = 0; i < parts.size(); i++) {
            parts.get(i).getClientSkinPartData().cleanUpDisplayLists();
        }
        if (hasPaintData()) {
            skinModelTexture.deleteGlTexture();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void blindPaintTexture() {
        if (hasPaintData()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, paintTextureId);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public int getModelCount() {
        int count = 0;
            for (int i = 0; i < parts.size(); i++) {
                count += parts.get(i).getModelCount();
            }
        return count;
    }
    
    public int getPartCount() {
        return parts.size();
    }
    
    public int lightHash() {
        if (lightHash == 0) {
            lightHash = this.hashCode();
        }
        return lightHash;
    }
    
    public Skin(DataInputStream stream) throws IOException, NewerFileVersionException, InvalidCubeTypeException {
        readFromStream(stream);
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(FILE_VERSION);
        stream.writeUTF(this.authorName);
        stream.writeUTF(this.customName);
        stream.writeUTF(this.tags);
        stream.writeUTF(this.equipmentSkinType.getRegistryName());
        if (this.paintData != null) {
            stream.writeBoolean(true);
            for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                stream.writeInt(paintData[i]);
            }
        } else {
            stream.writeBoolean(false);
        }
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
            String regName = stream.readUTF();
            if (regName.equals(SkinTypeRegistry.skinSkirt.getRegistryName())) {
                regName = SkinTypeRegistry.skinLegs.getRegistryName();
            }
            equipmentSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
        }
        
        if (equipmentSkinType == null) {
            throw new InvalidCubeTypeException();
        }
        
        this.paintData = null;
        if (fileVersion > 7) {
            boolean hasPaintData = stream.readBoolean();
            if (hasPaintData) {
                this.paintData = new int[SkinTexture.TEXTURE_SIZE];
                for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                    this.paintData[i] = stream.readInt();
                }
            }
        }
        
        int size = stream.readByte();
        parts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            parts.add(new SkinPart(stream, fileVersion));
        }
    }
    
    public static ISkinType readSkinTypeNameFromStream(DataInputStream stream) throws IOException, NewerFileVersionException {
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
        
        
        ISkinType skinType;
        
        if (fileVersion < 5) {
            skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromLegacyId(stream.readByte() - 1);
        } else {
            String regName = stream.readUTF();
            if (regName.equals(SkinTypeRegistry.skinSkirt.getRegistryName())) {
                regName = SkinTypeRegistry.skinLegs.getRegistryName();
            }
            skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(regName);
        }
        return skinType;
    }
    
    @Override
    public ISkinType getSkinType() {
        return equipmentSkinType;
    }
    
    public boolean hasPaintData() {
        return paintData != null;
    }
    
    public int[] getPaintData() {
        return paintData;
    }
    
    public ArrayList<SkinPart> getParts() {
        return parts;
    }
    
    public SkinPart getSkinPartFromType(ISkinPartType skinPartType) {
        for (int i = 0; i < parts.size(); i++) {
            if (parts.get(i).getPartType() == skinPartType) {
                return parts.get(i);
            }
        }
        return null;
    }
    
    @Override
    public ArrayList<ISkinPart> getSubParts() {
        ArrayList<ISkinPart> partList = new ArrayList<ISkinPart>();
        for (int i = 0; i < parts.size(); i++) {
            partList.add(parts.get(i));
        }
        return partList;
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
            ICube cube = CubeRegistry.INSTANCE.getCubeFormId((byte) i);
            totalCubes += getTotalOfCubeType(cube);
        }
        return totalCubes;
    }
    
    public int getTotalOfCubeType(ICube cube) {
        int totalOfCube = 0;
        int cubeId = cube.getId();
        for (int i = 0; i < parts.size(); i++) {
            totalOfCube += parts.get(i).getClientSkinPartData().totalCubesInPart[cubeId];
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
        String returnString = "Skin [authorName=" + authorName
                + ", customName=" + customName + ", type=" + equipmentSkinType.getName().toUpperCase();
        if (this.paintData != null) {
            returnString += ", paintData=" + Arrays.hashCode(paintData);
        }
        returnString += "]";
        return returnString;
    }

    public int getMarkerCount() {
        int count = 0;
        for (int i = 0; i < parts.size(); i++) {
            count += parts.get(i).getMarkerBlocks().size();
        }
        return count;
    }
}
