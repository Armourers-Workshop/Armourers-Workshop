package riskyken.armourersWorkshop.common.skin.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPart;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeMarkerData;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;

public class SkinPart implements ISkinPart {
    
    private Rectangle3D partBounds;
    private SkinCubeData cubeData;
    private ArrayList<CubeMarkerData> markerBlocks;
    private ISkinPartType skinPart;
    @SideOnly(Side.CLIENT)
    private ClientSkinPartData clientSkinPartData;
    @SideOnly(Side.CLIENT)
    public boolean isClippingGuide;
    
    public SkinPart(SkinCubeData cubeData, ISkinPartType skinPart, ArrayList<CubeMarkerData> markerBlocks) {
        this.cubeData = cubeData;
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
    
    @SideOnly(Side.CLIENT)
    public int getModelCount() {
        return clientSkinPartData.getModelCount();
    }
    
    private void setupPartBounds() {
        int minX = 127;
        int maxX = -127;
        
        int minY = 127;
        int maxY = -127;
        
        int minZ = 127;
        int maxZ = -127;
        
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            byte[] loc = cubeData.getCubeLocation(i);
            int x = loc[0];
            int y = loc[1];
            int z = loc[2];
            
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
        
        int xSize = maxX - minX;
        int ySize = maxY - minY;
        int zSize = maxZ - minZ;
        
        int xOffset = minX;
        int yOffset = minY;
        int zOffset = minZ;
        
        partBounds = new Rectangle3D(xOffset, yOffset, zOffset, xSize + 1, ySize + 1, zSize + 1);
    }
    
    public SkinCubeData getCubeData() {
        return cubeData;
    }
    
    public void clearCubeData() {
        cubeData = null;
    }
    
    public Rectangle3D getPartBounds() {
        return partBounds;
    }

    @Override
    public ISkinPartType getPartType() {
        return this.skinPart;
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
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeUTF(skinPart.getRegistryName());
        cubeData.writeToStream(stream);
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
        cubeData = new SkinCubeData();
        cubeData.readFromStream(stream, version, this);
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
        return "SkinPart [cubeData=" + cubeData + ", markerBlocks=" + markerBlocks + ", skinPart=" + skinPart.getRegistryName() + "]";
    }
}
