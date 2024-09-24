package moe.plushie.armourers_workshop.core.skin.face;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class SkinCuller {

    private static final int DIRECTION_SIZE = Direction.values().length;

    // joints array:
    private static final ImmutableMap<ISkinPartType, Partition> PARTITIONS2 = ImmutableMap.<ISkinPartType, Partition>builder()
            .put(SkinPartTypes.BIPPED_HAT, new Simple(SkinPartTypes.BIPPED_HAT))
            .put(SkinPartTypes.BIPPED_HEAD, new Simple(SkinPartTypes.BIPPED_HEAD))
            .put(SkinPartTypes.BIPPED_CHEST, new Limb(SkinPartTypes.BIPPED_CHEST, SkinPartTypes.BIPPED_TORSO, 6))
            .put(SkinPartTypes.BIPPED_LEFT_ARM, new Limb(SkinPartTypes.BIPPED_LEFT_ARM, SkinPartTypes.BIPPED_LEFT_HAND, 4))
            .put(SkinPartTypes.BIPPED_RIGHT_ARM, new Limb(SkinPartTypes.BIPPED_RIGHT_ARM, SkinPartTypes.BIPPED_RIGHT_HAND, 4))
            .put(SkinPartTypes.BIPPED_SKIRT, new Simple(SkinPartTypes.BIPPED_SKIRT))
            .put(SkinPartTypes.BIPPED_LEFT_THIGH, new Limb(SkinPartTypes.BIPPED_LEFT_THIGH, SkinPartTypes.BIPPED_LEFT_LEG, 6))
            .put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Limb(SkinPartTypes.BIPPED_RIGHT_THIGH, SkinPartTypes.BIPPED_RIGHT_LEG, 6))
            .put(SkinPartTypes.BIPPED_LEFT_FOOT, new Simple(SkinPartTypes.BIPPED_LEFT_FOOT))
            .put(SkinPartTypes.BIPPED_RIGHT_FOOT, new Simple(SkinPartTypes.BIPPED_RIGHT_FOOT))
            .put(SkinPartTypes.BIPPED_LEFT_WING, new Simple(SkinPartTypes.BIPPED_LEFT_WING))
            .put(SkinPartTypes.BIPPED_RIGHT_WING, new Simple(SkinPartTypes.BIPPED_RIGHT_WING))
            .build();

    interface Partition {
        Collection<SearchResult> subdivide(Rectangle3i rect);
    }

    private static Partition getPartition(ISkinPartType partType) {
        if (ModConfig.Client.enablePartSubdivide) {
            var partition = PARTITIONS2.get(partType);
            if (partition != null) {
                return partition;
            }
        }
        return new Simple(partType);
    }

    public static Collection<SearchResult> cullFaces2(SkinCubes cubeData, Rectangle3i bounds, ISkinPartType partType) {
        // The texture cube does not support static cull,
        // the slices are designed to contain multiple cube types,
        // but the skin culler can't support it now.
        var cubeTypes = cubeData.getCubeTypes();
        if (cubeTypes != null && cubeTypes.contains(SkinCubeTypes.TEXTURE)) {
            return allFaces(cubeData, bounds, partType);
        }
        var partition = getPartition(partType);
        var indexedMap = new IndexedMap(cubeData, bounds);
        var results = partition.subdivide(bounds);
        for (var result : results) {
            result.cull(cubeData, indexedMap);
        }
        for (int i = 0; i < cubeData.getCubeTotal(); ++i) {
            SkinCube cube = null;
            for (var dir : Direction.values()) {
                for (var result : results) {
                    if (result.flags.get(i * DIRECTION_SIZE + dir.get3DDataValue())) {
                        if (cube == null) {
                            cube = cubeData.getCube(i);
                        }
                        var face = cube.getFace(dir);
                        if (face != null) {
                            result.add(face);
                        }
                    }
                }
            }
        }
        return results;
    }

    public static Collection<SearchResult> allFaces(SkinCubes cubeData, Rectangle3i bounds, ISkinPartType partType) {
        var result = new SearchResult(partType, bounds, Vector3i.ZERO);
        for (int i = 0; i < cubeData.getCubeTotal(); ++i) {
            var cube = cubeData.getCube(i);
            for (var dir : Direction.values()) {
                var face = cube.getFace(dir);
                if (face != null) {
                    result.add(face);
                }
            }
        }
        return Collections.singleton(result);
    }

    public static ArrayList<SkinCubeFace> cullFaces(SkinCubes cubeData, Rectangle3i bounds) {
        var indexedMap = new IndexedMap(cubeData, bounds);
        var rect = new Rectangle3i(0, 0, 0, bounds.getWidth(), bounds.getHeight(), bounds.getDepth());
        var flags = cullFaceFlags(cubeData, indexedMap, rect);
        var faces = new ArrayList<SkinCubeFace>();
        for (int i = 0; i < cubeData.getCubeTotal(); ++i) {
            SkinCube cube = null;
            for (var dir : Direction.values()) {
                if (flags.get(i * DIRECTION_SIZE + dir.get3DDataValue())) {
                    if (cube == null) {
                        cube = cubeData.getCube(i);
                    }
                    var face = cube.getFace(dir);
                    if (face != null) {
                        faces.add(face);
                    }
                }
            }
        }
        return faces;
    }

    private static BitSet cullFaceFlags(SkinCubes cubeData, IndexedMap map, Rectangle3i rect) {
        var flags = new BitSet(cubeData.getCubeTotal() * DIRECTION_SIZE);
        var searchArea = new Rectangle3i(rect.getX() - 1, rect.getY() - 1, rect.getZ() - 1, rect.getWidth() + 2, rect.getHeight() + 2, rect.getDepth() + 2);
        var closedSet = new HashSet<Vector3i>();
        var openList = new ArrayDeque<Vector3i>();
        var start = searchArea.getOrigin();
        openList.add(start);
        closedSet.add(start);
        map.limit(rect);
        while (!openList.isEmpty()) {
            var pendingList = new ArrayList<Vector3i>();
            var pos = openList.poll();
            for (var advance : Direction.values()) {
                var pos1 = pos.relative(advance, 1);
                int targetIndex = map.get(pos1);
                if (targetIndex == -1) {
                    pendingList.add(pos1);
                    continue;
                }
                var isBlank = false;
                var targetCube = cubeData.getCube(targetIndex).getType();
                if (targetCube.isGlass()) {
                    pendingList.add(pos1);
                    // when source cube and target cube is linked glass, ignore.
                    int sourceIndex = map.get(pos);
                    if (sourceIndex != -1) {
                        isBlank = cubeData.getCube(sourceIndex).getType().isGlass();
                    }
                }
                // first, when not any rotation of the cube, it's always facing north.
                // then advance direction always opposite to cube facing direction,
                // so theory we should always use `advance.getOpposite()` get cube facing direction.
                // but actually we reset the cube origin in the indexes, which causes the relationship
                // between the forward direction and the facing is changed.
                var facing = advance;
                if (advance.getAxis() == Direction.Axis.Z) {
                    facing = advance.getOpposite();
                }
                flags.set(targetIndex * DIRECTION_SIZE + facing.get3DDataValue(), !isBlank);
            }
            for (Vector3i pos1 : pendingList) {
                if (searchArea.contains(pos1) && !closedSet.contains(pos1)) {
                    closedSet.add(pos1);
                    openList.add(pos1);
                }
            }
        }
        return flags;
    }

    static class Simple implements Partition {

        final ISkinPartType partType;

        public Simple(ISkinPartType partType) {
            this.partType = partType;
        }

        @Override
        public Collection<SearchResult> subdivide(Rectangle3i rect) {
            var box = new Rectangle3i(0, 0, 0, rect.getWidth(), rect.getHeight(), rect.getDepth());
            return Collections.singleton(new SearchResult(partType, box, Vector3i.ZERO));
        }
    }

    static class Limb extends Simple {

        final ISkinPartType upperPartType;
        final ISkinPartType lowerPartType;

        final int yClip;

        public Limb(ISkinPartType upperPartType, ISkinPartType lowerPartType, int yClip) {
            super(upperPartType);
            this.upperPartType = upperPartType;
            this.lowerPartType = lowerPartType;
            this.yClip = yClip;
        }

        @Override
        public Collection<SearchResult> subdivide(Rectangle3i rect) {
            int upper = yClip - rect.getMinY();
            int lower = rect.getMaxY() - yClip;
            if (lower > 0 && upper > 0) {
                Rectangle3i upperBox = new Rectangle3i(0, 0, 0, rect.getWidth(), upper, rect.getDepth());
                Rectangle3i lowerBox = new Rectangle3i(0, upper, 0, rect.getWidth(), lower, rect.getDepth());
                // ..
                SearchResult upperResult = new SearchResult(upperPartType, upperBox, Vector3i.ZERO);
                SearchResult lowerResult = new SearchResult(lowerPartType, lowerBox, new Vector3i(0, -yClip, 0));
                return ObjectUtils.map(upperResult, lowerResult);
            }
            return super.subdivide(rect);
        }
    }

    public static class SearchResult {

        protected final ISkinPartType partType;
        protected final Rectangle3i bounds;
        protected final Vector3i origin;

        protected BitSet flags;
        protected ArrayList<SkinCubeFace> faces;

        public SearchResult(ISkinPartType partType, Rectangle3i bounds, Vector3i origin) {
            this.faces = new ArrayList<>();
            this.partType = partType;
            this.bounds = bounds;
            this.origin = origin;
        }

        public void add(SkinCubeFace face) {
            this.faces.add(face);
        }

        public void cull(SkinCubes cubeData, IndexedMap map) {
            this.flags = cullFaceFlags(cubeData, map, bounds);
        }

        public ISkinPartType getPartType() {
            return partType;
        }

        public ArrayList<SkinCubeFace> getFaces() {
            return faces;
        }

        public Vector3i getOrigin() {
            return origin;
        }

        public Rectangle3i getBounds() {
            return bounds;
        }
    }

    public static class IndexedMap {

        public final int size;

        public final int x;
        public final int y;
        public final int z;
        public final int width;
        public final int height;
        public final int depth;

        private final int[][][] indexes;

        private int minX;
        private int minY;
        private int minZ;
        private int maxX;
        private int maxY;
        private int maxZ;

        public IndexedMap(SkinCubes cubeData, Rectangle3i bounds) {
            this.size = cubeData.getCubeTotal();
            this.x = bounds.getX();
            this.y = bounds.getY();
            this.z = bounds.getZ();
            this.width = bounds.getWidth();
            this.height = bounds.getHeight();
            this.depth = bounds.getDepth();
            this.indexes = new int[this.depth][this.height][this.width];
            for (int i = 0; i < this.size; i++) {
                IVector3i pos = cubeData.getCube(i).getPosition();
                int x = pos.getX() - this.x;
                int y = pos.getY() - this.y;
                int z = pos.getZ() - this.z;
                this.indexes[z][y][x] = i + 1;
            }
            this.limit(new Rectangle3i(0, 0, 0, this.width, this.height, this.depth));
        }

        public void limit(Rectangle3i limit) {
            this.minX = Math.max(limit.getMinX(), 0);
            this.minY = Math.max(limit.getMinY(), 0);
            this.minZ = Math.max(limit.getMinZ(), 0);
            this.maxX = Math.min(limit.getMaxX(), width);
            this.maxY = Math.min(limit.getMaxY(), height);
            this.maxZ = Math.min(limit.getMaxZ(), depth);
        }

        public int get(Vector3i pos) {
            return get(pos.getX(), pos.getY(), pos.getZ());
        }

        public int get(int x, int y, int z) {
            if (x < minX || x >= maxX) {
                return -1;
            }
            if (y < minY || y >= maxY) {
                return -1;
            }
            if (z < minZ || z >= maxZ) {
                return -1;
            }
            return indexes[z][y][x] - 1;
        }
    }
}
