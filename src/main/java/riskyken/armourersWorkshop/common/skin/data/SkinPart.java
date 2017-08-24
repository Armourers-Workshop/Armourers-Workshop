package riskyken.armourersWorkshop.common.skin.data;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPart;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.skin.ClientSkinPartData;
import riskyken.armourersWorkshop.common.skin.cubes.CubeMarkerData;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.plushieWrapper.common.world.BlockLocation;

public class SkinPart implements ISkinPart {
    
    private Rectangle3D partBounds;
    private Rectangle3D[][][] blockGrid;
    private SkinCubeData cubeData;
    private ArrayList<CubeMarkerData> markerBlocks;
    private ISkinPartType skinPart;
    @SideOnly(Side.CLIENT)
    private ClientSkinPartData clientSkinPartData;
    
    public SkinPart(SkinCubeData cubeData, ISkinPartType skinPart, ArrayList<CubeMarkerData> markerBlocks) {
        this.cubeData = cubeData;
        this.skinPart = skinPart;
        this.markerBlocks = markerBlocks;
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
        if (skinPart == SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:block.base") |
            skinPart == SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:block.multiblock")) {
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
    
    public void setSkinPart(ISkinPartType skinPart) {
        this.skinPart = skinPart;
        setupPartBounds();
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
            return  ForgeDirection.getOrientation(cmd.meta - 1);
        }
        return null;
    }

    @Override
    public String toString() {
        return "SkinPart [cubeData=" + cubeData + ", markerBlocks=" + markerBlocks + ", skinPart=" + skinPart.getRegistryName() + "]";
    }
}
