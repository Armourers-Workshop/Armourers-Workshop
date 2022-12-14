package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.ISkinPart;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkinPart implements ISkinPart {

    protected int id = 0;

    protected ISkinPartType partType;

    protected OpenVoxelShape renderShape;
    protected Rectangle3i partBounds;

    protected SkinTransform transform;
    protected SkinProperties properties;

    private HashMap<Long, Rectangle3i> blockGrid;
    private SkinCubeData cubeData;
    private final ArrayList<SkinMarker> markerBlocks;

    public SkinPart(ISkinPartType partType, ArrayList<SkinMarker> markerBlocks, SkinCubeData cubeData) {
        this.partType = partType;
        this.renderShape = cubeData.getRenderShape();

        this.transform = SkinTransform.IDENTIFIER;
        this.cubeData = cubeData;
        this.markerBlocks = markerBlocks;

        this.setupPartBounds();

        if (markerBlocks != null) {
            cubeData.getUsedCounter().addMarkers(markerBlocks.size());
        }
    }

    public int getId() {
        return id;
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

    public OpenVoxelShape getRenderShape() {
        if (getType() == SkinPartTypes.ITEM_ARROW) {
            return OpenVoxelShape.empty();
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
            int tx = MathUtils.floor((x + 8) / 16f);
            int ty = MathUtils.floor((y + 8) / 16f);
            int tz = MathUtils.floor((z + 8) / 16f);
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

    public int getParent() {
        return 0;
    }

    public String getName() {
        return null;
    }


    public void setTransform(SkinTransform transform) {
        this.transform = transform;
    }

    public SkinTransform getTransform() {
        return transform;
    }

    public SkinPaintData getPaintData() {
        return null;
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

    public static class Empty extends SkinPart {

        public Empty(ISkinPartType partType, Rectangle3i bounds, OpenVoxelShape renderShape) {
            super(partType, new ArrayList<>(), new SkinCubeData(partType));
            this.partBounds = bounds;
            this.renderShape = renderShape;
            this.setProperties(new SkinProperties());
        }
    }
}
