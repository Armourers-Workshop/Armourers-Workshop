package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.ISkinPart;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV1;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class SkinPart implements ISkinPart {

    protected int id;
    protected Integer parentId;
    protected String name;

    protected ISkinPartType partType;

    protected OpenVoxelShape renderShape;
    protected Rectangle3i partBounds;

    protected SkinTransform transform = SkinTransform.IDENTIFIER;
    protected SkinProperties properties = SkinProperties.EMPTY;

    private HashMap<Long, Rectangle3i> blockGrid;
    private SkinCubes cubeData;
    private final ArrayList<SkinMarker> markerBlocks;

    public SkinPart(int id, ISkinPartType partType, ArrayList<SkinMarker> markers, SkinCubes cubes) {
        this.id = id;

        this.partType = partType;
        this.renderShape = cubes.getRenderShape();

        this.cubeData = cubes;
        this.markerBlocks = markers;

        this.setupPartBounds();

        if (markers != null) {
            cubes.getUsedCounter().addMarkers(markers.size());
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

    public SkinCubes getCubeData() {
        return cubeData;
    }

    public void clearCubeData() {
        cubeData = null;
    }

    public void setParent(Integer id) {
        this.parentId = id;
    }

    @Nullable
    public Integer getParent() {
        return this.parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return name;
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

    public Object getBlobs() {
        return null;
    }

    @Override
    public ISkinPartType getType() {
        return this.partType;
    }

    @Override
    public ArrayList<SkinMarker> getMarkers() {
        return markerBlocks;
    }

    @Override
    public String toString() {
        return "SkinPart [cubeData=" + cubeData + ", markerBlocks=" + markerBlocks + ", skinPart=" + partType.getRegistryName() + "]";
    }

    public static class Empty extends SkinPart {

        public Empty(int id, ISkinPartType partType, Rectangle3i bounds, OpenVoxelShape renderShape) {
            super(id, partType, new ArrayList<>(), new SkinCubesV1(0));
            this.partBounds = bounds;
            this.renderShape = renderShape;
            this.setProperties(SkinProperties.create());
        }
    }

    public static class Builder {

        private final ISkinPartType partType;

        private String name;
        private SkinCubes cubes;
        private SkinPaintData paintData;
        private SkinTransform transform = SkinTransform.IDENTIFIER;
        private ArrayList<SkinMarker> markers = new ArrayList<>();
        private SkinProperties properties;
        private Object blobs;

        private int id = 0;
        private Integer parentId;

        public Builder(ISkinPartType partType) {
            this.partType = partType;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder parent(Integer parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder transform(SkinTransform transform) {
            if (transform != null) {
                this.transform = transform;
            }
            return this;
        }

        public Builder cubes(SkinCubes cubes) {
            this.cubes = cubes;
            return this;
        }

        public Builder paintData(SkinPaintData paintData) {
            this.paintData = paintData;
            return this;
        }

        public Builder markers(ArrayList<SkinMarker> markers) {
            if (markers != null) {
                this.markers.addAll(markers);
            }
            return this;
        }

        public Builder properties(SkinProperties properties) {
            this.properties = properties;
            return this;
        }

        public Builder blobs(Object blobs) {
            this.blobs = blobs;
            return this;
        }

        public SkinPart build() {
            SkinPart skinPart = new SkinPart(id, partType, markers, cubes);
            skinPart.setName(name);
            skinPart.setParent(parentId);
            skinPart.setTransform(transform);
            return skinPart;
        }
    }
}
