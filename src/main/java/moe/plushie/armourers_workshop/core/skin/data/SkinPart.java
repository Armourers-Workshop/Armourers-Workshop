package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPart;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class SkinPart implements ISkinPart {

    protected ISkinPartType partType;

    protected CustomVoxelShape renderShape;
    protected Rectangle3i partBounds;

    protected SkinProperties properties;

    private Rectangle3i[][][] blockGrid;
    private SkinCubeData cubeData;
    private ArrayList<SkinMarker> markerBlocks;

    public SkinPart(ISkinPartType partType, ArrayList<SkinMarker> markerBlocks, SkinCubeData cubeData) {
        this.partType = partType;
        this.renderShape = cubeData.getRenderShape();
        this.partBounds = new Rectangle3i(this.renderShape.bounds());

        this.cubeData = cubeData;
        this.markerBlocks = markerBlocks;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public void setProperties(SkinProperties properties) {
        this.properties = properties;
    }


    public int getModelCount() {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public CustomVoxelShape getRenderShape() {
        if (getType() == SkinPartTypes.ITEM_ARROW) {
            return CustomVoxelShape.empty();
        }
        return renderShape;
    }

    private void setupPartBounds() {
        if (partType == SkinPartTypes.BLOCK || partType == SkinPartTypes.BLOCK_MULTI) {
            setupBlockBounds();
        }
    }

    public void setSkinPart(ISkinPartType skinPart) {
        this.partType = skinPart;
        setupPartBounds();
    }

    public Rectangle3i getBlockBounds(int x, int y, int z) {
        if (blockGrid != null) {
            x = MathHelper.clamp(x, 0, blockGrid.length);
            y = MathHelper.clamp(y, 0, blockGrid[1].length);
            z = MathHelper.clamp(z, 0, blockGrid[0][1].length);
            return blockGrid[x][y][z];
        }
        return null;
    }

    public Rectangle3i[][][] getBlockGrid() {
        return blockGrid;
    }

    private void setupBlockBounds() {
        blockGrid = new Rectangle3i[3][3][3];
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
                    Rectangle3i rec = blockGrid[ix][iy][iz];
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
            blockGrid[loc.getX()][loc.getY()][loc.getZ()] = new Rectangle3i(127, 127, 127, -127, -127, -127);
        }
        Rectangle3i rec = blockGrid[loc.getX()][loc.getY()][loc.getZ()];
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

    public Rectangle3i getPartBounds() {
        return partBounds;
    }

    @Override
    public ISkinPartType getType() {
        return this.partType;
    }

    @Override
    public List<SkinMarker> getMarkers() {
        return markerBlocks;
    }

    @Override
    public String toString() {
        return "SkinPart [cubeData=" + cubeData + ", markerBlocks=" + markerBlocks + ", skinPart=" + partType.getRegistryName() + "]";
    }
}
