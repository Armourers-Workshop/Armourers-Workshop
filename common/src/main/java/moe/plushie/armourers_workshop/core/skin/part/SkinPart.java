package moe.plushie.armourers_workshop.core.skin.part;

import moe.plushie.armourers_workshop.api.skin.ISkinPart;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SkinPart implements ISkinPart {

    protected String name;

    protected ISkinPartType partType;

    protected ISkinTransform transform = SkinTransform.IDENTITY;
    protected SkinProperties properties = SkinProperties.EMPTY;

    private SkinCubes cubeData;

    private Object blobs;
    private HashMap<BlockPos, Rectangle3i> blockBounds;

    private final ArrayList<SkinPart> children = new ArrayList<>();
    private final ArrayList<SkinMarker> markerBlocks = new ArrayList<>();

    public SkinPart(ISkinPartType partType, Collection<SkinMarker> markers, SkinCubes cubes) {
        this.partType = partType;

        this.cubeData = cubes;
        this.cubeData.getUsedCounter().addMarkers(markers.size());

        this.markerBlocks.addAll(markers);
    }

    public void addPart(SkinPart part) {
        children.add(part);
    }

    public void removePart(SkinPart part) {
        children.remove(part);
    }

    public void setProperties(SkinProperties properties) {
        this.properties = properties;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public int getModelCount() {
        return 0;
    }

    public void setSkinPart(ISkinPartType skinPart) {
        this.partType = skinPart;
    }

    public HashMap<BlockPos, Rectangle3i> getBlockBounds() {
        if (blockBounds != null) {
            return blockBounds;
        }
        HashMap<Long, Rectangle3i> blockGrid = new HashMap<>();
        blockBounds = new HashMap<>();
        cubeData.forEach(cube -> {
            var pos = cube.getPosition();
            var x = pos.getX();
            var y = pos.getY();
            var z = pos.getZ();
            var tx = MathUtils.floor((x + 8) / 16f);
            var ty = MathUtils.floor((y + 8) / 16f);
            var tz = MathUtils.floor((z + 8) / 16f);
            var key = BlockPos.asLong(-tx, -ty, tz);
            var rec = new Rectangle3i(-(x - tx * 16) - 1, -(y - ty * 16) - 1, z - tz * 16, 1, 1, 1);
            blockGrid.computeIfAbsent(key, k -> rec).union(rec);
        });
        blockGrid.forEach((key, value) -> blockBounds.put(BlockPos.of(key), value));
        return blockBounds;
    }

    public SkinCubes getCubeData() {
        return cubeData;
    }

    public void clearCubeData() {
        cubeData = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setTransform(ISkinTransform transform) {
        this.transform = transform;
    }

    public ISkinTransform getTransform() {
        return transform;
    }

    public void setBlobs(Object blobs) {
        this.blobs = blobs;
    }

    public Object getBlobs() {
        return blobs;
    }

    @Override
    public ISkinPartType getType() {
        return this.partType;
    }

    @Override
    public ArrayList<SkinPart> getParts() {
        return children;
    }

    @Override
    public Collection<SkinMarker> getMarkers() {
        return markerBlocks;
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "cubeData", cubeData, "markerBlocks", markerBlocks, "type", partType);
    }

    public static class Builder {

        private final ISkinPartType partType;

        private String name;
        private SkinCubes cubes;
        private ISkinTransform transform = SkinTransform.IDENTITY;
        private ArrayList<SkinMarker> markers = new ArrayList<>();
        private SkinProperties properties;
        private Object blobs;

        public Builder(ISkinPartType partType) {
            this.partType = partType;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder transform(ISkinTransform transform) {
            if (transform != null) {
                this.transform = transform;
            }
            return this;
        }

        public Builder cubes(SkinCubes cubes) {
            this.cubes = cubes;
            return this;
        }

        public Builder markers(Collection<SkinMarker> markers) {
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
            var skinPart = new SkinPart(partType, markers, cubes);
            skinPart.setName(name);
            skinPart.setTransform(transform);
            skinPart.setBlobs(blobs);
            return skinPart;
        }
    }
}
