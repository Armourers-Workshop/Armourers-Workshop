package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinPart;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkinPart implements ISkinPart {

    protected ISkinPartType partType;

    protected CustomVoxelShape renderShape;
    protected Rectangle3i partBounds;

    protected SkinProperties properties;

    private HashMap<Long, Rectangle3i> blockGrid;
    private SkinCubeData cubeData;
    private ArrayList<SkinMarker> markerBlocks;

    public SkinPart(ISkinPartType partType, ArrayList<SkinMarker> markerBlocks, SkinCubeData cubeData) {
        this.partType = partType;
        this.renderShape = cubeData.getRenderShape();

        this.cubeData = cubeData;
        this.markerBlocks = markerBlocks;

        this.setupPartBounds();

        if (markerBlocks != null) {
            cubeData.getUsedCounter().addMarkers(markerBlocks.size());
        }
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
        } else {
            partBounds = new Rectangle3i(this.renderShape.bounds());
        }
    }

    public void setSkinPart(ISkinPartType skinPart) {
        this.partType = skinPart;
        setupPartBounds();
    }

    public Rectangle3i getBlockBounds(int x, int y, int z) {
        if (blockGrid != null) {
            long key = BlockPos.asLong(x, y, z);
            return blockGrid.get(key);
        }
        return null;
    }

    public HashMap<BlockPos, Rectangle3i> getBlockBounds() {
        if (blockGrid != null) {
            HashMap<BlockPos, Rectangle3i> blockBounds = new HashMap<>();
            blockGrid.forEach((key, value) -> blockBounds.put(BlockPos.of(key), value));
            return blockBounds;
        }
        return null;
    }

    private void setupBlockBounds() {
        blockGrid = new HashMap<>();
        cubeData.forEach((i, x, y, z) -> {
            int tx = MathHelper.floor((x + 8) / 16f);
            int ty = MathHelper.floor((y + 8) / 16f);
            int tz = MathHelper.floor((z + 8) / 16f);
            long key = BlockPos.asLong(-tx, -ty, tz);
            Rectangle3i rec = new Rectangle3i(-(x - tx * 16) - 1, -(y - ty * 16) - 1, z - tz * 16, 1, 1, 1);
            blockGrid.computeIfAbsent(key, k -> rec).union(rec);
        });
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
