package riskyken.armourersWorkshop.common.skin.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.MathHelper;
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
import riskyken.plushieWrapper.common.world.BlockLocation;

public class SkinPart implements ISkinPart {
    
    private Rectangle3D partBounds;
    private Rectangle3D[][][] blockGrid;
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
        partBounds = setupPartBounds();
    }
    
    public SkinPart(DataInputStream stream, int version) throws IOException, InvalidCubeTypeException {
        readFromStream(stream, version);
        partBounds = setupPartBounds();
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
    
    private Rectangle3D setupPartBounds() {
        if (skinPart == SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:block.base")) {
            setupBlockBounds();
        }
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
            minX = Math.min(x, minX);
            maxX = Math.max(x, maxX);
            minY = Math.min(y, minY);
            maxY = Math.max(y, maxY);
            minZ = Math.min(z, minZ);
            maxZ = Math.max(z, maxZ);
        }
        
        int xSize = maxX - minX;
        int ySize = maxY - minY;
        int zSize = maxZ - minZ;
        
        return new Rectangle3D(minX, minY, minZ, xSize + 1, ySize + 1, zSize + 1);
    }
    
    public Rectangle3D getBlockBounds(int x, int y, int z) {
        return blockGrid[x][y][z];
    }
    
    public Rectangle3D[][][] getBlockGrid() {
        return blockGrid;
    }
    
    private void setupBlockBounds() {
        blockGrid = new Rectangle3D[3][3][3];
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            byte[] loc = cubeData.getCubeLocation(i);
            int x = MathHelper.floor_float((float)(loc[0] + 8) / 16F);
            int y = MathHelper.floor_float((float)(loc[1] + 8) / 16F);
            int z = MathHelper.floor_float((float)(loc[2] + 8) / 16F);
            setupBlockBounds(x, y, z, loc[0] - x * 16, loc[1] - y * 16, loc[2] - z * 16);
        }
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    Rectangle3D rec = blockGrid[ix][iy][iz];
                    if (rec != null) {
                        rec.setWidth(rec.getWidth() - rec.getX() + 1);
                        rec.setHeight(rec.getHeight() - rec.getY() + 1);
                        rec.setDepth(rec.getDepth() - rec.getZ() + 1);
                    }
                }
            }
        }
    }
    
    private void setupBlockBounds(int blockX, int blockY, int blockZ, int x, int y, int z) {
        BlockLocation loc = new BlockLocation(blockX + 1, -blockY, blockZ);
        if (blockGrid[loc.x][loc.y][loc.z] == null) {
            blockGrid[loc.x][loc.y][loc.z] = new Rectangle3D(127, 127, 127, -127, -127, -127);
        }
        Rectangle3D rec = blockGrid[loc.x][loc.y][loc.z];
        rec.setX(Math.min(rec.getX(), x));
        rec.setY(Math.min(rec.getY(), y));
        rec.setZ(Math.min(rec.getZ(), z));
        rec.setWidth(Math.max(rec.getWidth(), x));
        rec.setHeight(Math.max(rec.getHeight(), y));
        rec.setDepth(Math.max(rec.getDepth(), z));
        //blockGrid[loc.x][loc.y][loc.z] = rec;
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
                throw new InvalidCubeTypeException();
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
