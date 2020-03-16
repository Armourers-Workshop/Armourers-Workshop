package moe.plushie.armourers_workshop.common.skin.data;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinPart;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeMarkerData;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        if (skinPart == SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:block.base") | skinPart == SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName("armourers:block.multiblock")) {
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
        if (blockGrid != null) {
            x = MathHelper.clamp(x, 0, blockGrid.length);
            y = MathHelper.clamp(y, 0, blockGrid[1].length);
            z = MathHelper.clamp(z, 0, blockGrid[0][1].length);
            return blockGrid[x][y][z];
        }
        return null;
    }

    public Rectangle3D[][][] getBlockGrid() {
        return blockGrid;
    }

    private void setupBlockBounds() {
        blockGrid = new Rectangle3D[3][3][3];
        for (int i = 0; i < cubeData.getCubeCount(); i++) {
            byte[] loc = cubeData.getCubeLocation(i);
            int x = MathHelper.floor((loc[0] + 8) / 16F);
            int y = MathHelper.floor((loc[1] + 8) / 16F);
            int z = MathHelper.floor((loc[2] + 8) / 16F);
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
        BlockPos loc = new BlockPos(blockX + 1, -blockY, blockZ);
        if (blockGrid[loc.getX()][loc.getY()][loc.getZ()] == null) {
            blockGrid[loc.getX()][loc.getY()][loc.getZ()] = new Rectangle3D(127, 127, 127, -127, -127, -127);
        }
        Rectangle3D rec = blockGrid[loc.getX()][loc.getY()][loc.getZ()];
        rec.setX(Math.min(rec.getX(), x));
        rec.setY(Math.min(rec.getY(), y));
        rec.setZ(Math.min(rec.getZ(), z));
        rec.setWidth(Math.max(rec.getWidth(), x));
        rec.setHeight(Math.max(rec.getHeight(), y));
        rec.setDepth(Math.max(rec.getDepth(), z));
        // blockGrid[loc.x][loc.y][loc.z] = rec;
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
            return new Point3D(cmd.x, cmd.y, cmd.z);
        }
        return null;
    }

    @Override
    public EnumFacing getMarkerSide(int index) {
        if (index >= 0 & index < markerBlocks.size()) {
            CubeMarkerData cmd = markerBlocks.get(index);
            return EnumFacing.byIndex(cmd.meta - 1);
        }
        return null;
    }
    
    

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkinPart other = (SkinPart) obj;
        if (!toString().equals(other.toString()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SkinPart [cubeData=" + cubeData + ", markerBlocks=" + markerBlocks + ", skinPart=" + skinPart.getRegistryName() + "]";
    }
}
