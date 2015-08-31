package riskyken.armourersWorkshop.common.skin.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPart;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeFactory;
import riskyken.armourersWorkshop.common.skin.cubes.CubeMarkerData;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.cubes.LegacyCubeHelper;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;

public class SkinPart implements ISkinPart {
    
    private static final String TAG_PART_NAME = "partName";
    private static final String TAG_CUBE_LIST = "cubeList";
    private static final String TAG_ID = "id";
    
    private Rectangle3D partBounds;
    private ArrayList<ICube> armourData;
    private ArrayList<CubeMarkerData> markerBlocks;
    private ISkinPartType skinPart;
    @SideOnly(Side.CLIENT)
    private ClientSkinPartData clientSkinPartData;
    
    public SkinPart(ArrayList armourData, ISkinPartType skinPart, ArrayList<CubeMarkerData> markerBlocks) {
        this.armourData = armourData;
        this.skinPart = skinPart;
        this.markerBlocks = markerBlocks;
        setupPartBounds();
    }
    
    public SkinPart(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        readFromStream(stream, version);
        setupPartBounds();
    }
    
    @SideOnly(Side.CLIENT)
    public void setClientSkinPartData(ClientSkinPartData clientSkinPartData) {
        this.clientSkinPartData = clientSkinPartData;
    }
    
    @SideOnly(Side.CLIENT)
    public ClientSkinPartData getClientSkinPartData() {
        return clientSkinPartData;
    }
    
    private void setupPartBounds() {
        int minX = 127;
        int maxX = -127;
        
        int minY = 127;
        int maxY = -127;
        
        int minZ = 127;
        int maxZ = -127;
        
        for (int i = 0; i < armourData.size(); i++) {
            ICube c = armourData.get(i);
            int x = c.getX();
            int y = c.getY();
            int z = c.getZ();
            
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
            
            if (z < minZ) {
                minZ = z;
            }
            if (z > maxZ) {
                maxZ = z;
            }
        }
        
        IRectangle3D rec = skinPart.getBuildingSpace();
        
        int xSize = maxX - minX;
        int ySize = maxY - minY;
        int zSize = maxZ - minZ;
        
        int xOffset = rec.getX() + rec.getWidth() + minX;
        int yOffset = rec.getY() - minY + rec.getHeight() - ySize - 1;
        int zOffset = rec.getZ() - minZ + rec.getDepth() - zSize - 1;
        
        partBounds = new Rectangle3D(xOffset, yOffset, zOffset, xSize + 1, ySize + 1, zSize + 1);
    }
    
    public Rectangle3D getPartBounds() {
        return partBounds;
    }

    @Override
    public ISkinPartType getPartType() {
        return this.skinPart;
    }

    public ArrayList<ICube> getArmourData() {
        return armourData;
    }
    
    public ArrayList<CubeMarkerData> getMarkerBlocks() {
        return markerBlocks;
    }
    
    @Override
    public int getMarkerCount() {
        return markerBlocks.size();
    }
    
    @Override
    public Point3D getMarker(int index) {
        if (index >= 0 & index < markerBlocks.size()) {
            CubeMarkerData cmd = markerBlocks.get(index);
            return  new Point3D(cmd.x, cmd.y, cmd.z);
        }
        return null;
    }
    
    @Override
    public ForgeDirection getMarkerSide(int index) {
        if (index >= 0 & index < markerBlocks.size()) {
            CubeMarkerData cmd = markerBlocks.get(index);
            return  ForgeDirection.getOrientation(cmd.meta);
        }
        return null;
    }
    
    
    //TODO Remove from mini armourer.
    public SkinPart(ISkinPartType skinPart) {
        this.armourData = new ArrayList<ICube>();
        this.skinPart = skinPart;
        this.markerBlocks = new ArrayList<CubeMarkerData>();
    }
    
    //TODO Remove from mini armourer.
    public void writeToCompound(NBTTagCompound compound) {
        compound.setString(TAG_PART_NAME, skinPart.getRegistryName());
        NBTTagList cubeList = new NBTTagList();
        for (int i = 0; i < armourData.size(); i++) {
            NBTTagCompound cubeCompound = new NBTTagCompound();
            ICube cube = armourData.get(i);
            cube.writeToCompound(cubeCompound);
            cubeList.appendTag(cubeCompound);
        }
        compound.setTag(TAG_CUBE_LIST, cubeList);
    }
    
    //TODO Remove from mini armourer.
    public void readFromCompound(NBTTagCompound compound) throws InvalidCubeTypeException {
        String partName = compound.getString(TAG_PART_NAME);
        skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(partName);
        NBTTagList cubeList = compound.getTagList(TAG_CUBE_LIST, NBT.TAG_COMPOUND);
        for (int i = 0; i < cubeList.tagCount(); i++) {
            NBTTagCompound cubeCompound = cubeList.getCompoundTagAt(i);
            byte cubeId = cubeCompound.getByte(TAG_ID);
            ICube cube = CubeFactory.INSTANCE.getCubeInstanceFormId(cubeId);
            cube.readFromCompound(cubeCompound);
            armourData.add(cube);
        }
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeUTF(skinPart.getRegistryName());
        stream.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToStream(stream);
        }
        stream.writeInt(markerBlocks.size());
        for (int i = 0; i < markerBlocks.size(); i++) {
            markerBlocks.get(i).writeToStream(stream);
        }
    }
    
    private void readFromStream(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        if (version < 6) {
            skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromLegacyId(stream.readByte());
        } else {
            String regName = stream.readUTF();
            if (regName.equals("armourers:skirt.base")) {
                regName = "armourers:legs.skirt";
            }
            if (regName.equals("armourers:bow.base")) {
                regName = "armourers:bow.frame1";
            }
            skinPart = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(regName);
            
            if (skinPart == null) {
                ModLogger.log(Level.ERROR,"Skin part was null - reg name: " + regName);
            }
        }
        int size = stream.readInt();
        armourData = new ArrayList<ICube>();
        for (int i = 0; i < size; i++) {
            ICube cube;
            if (version < 3) {
                cube = LegacyCubeHelper.loadlegacyCube(stream, version, skinPart);
            } else {
                byte id = stream.readByte();
                cube = CubeFactory.INSTANCE.getCubeInstanceFormId(id);
                cube.readFromStream(stream, version, skinPart);
            }
            armourData.add(cube);
        }
        markerBlocks = new ArrayList<CubeMarkerData>();
        if (version > 8) {
            int markerCount = stream.readInt();
            for (int i = 0; i < markerCount; i++) {
                markerBlocks.add(new CubeMarkerData(stream, version));
            }
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < armourData.size(); i++) {
            result += armourData.get(i).toString();
        }
        for (int i = 0; i < markerBlocks.size(); i++) {
            result += markerBlocks.get(i).toString();
        }
        return "CustomArmourPartData [armourData=" + armourData + "" + result;
    }
}
