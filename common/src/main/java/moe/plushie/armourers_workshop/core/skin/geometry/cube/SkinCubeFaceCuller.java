package moe.plushie.armourers_workshop.core.skin.geometry.cube;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometrySet;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.init.ModConfig;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class SkinCubeFaceCuller {

    private static final int DIRECTION_SIZE = OpenDirection.values().length;

    // joints array:
    private static final Map<SkinPartType, Partition> PARTITIONS2 = Collections.immutableMap(it -> {
        it.put(SkinPartTypes.BIPPED_HAT, new Simple(SkinPartTypes.BIPPED_HAT));
        it.put(SkinPartTypes.BIPPED_HEAD, new Simple(SkinPartTypes.BIPPED_HEAD));
        it.put(SkinPartTypes.BIPPED_CHEST, new Limb(SkinPartTypes.BIPPED_CHEST, SkinPartTypes.BIPPED_TORSO, 6));
        it.put(SkinPartTypes.BIPPED_LEFT_ARM, new Limb(SkinPartTypes.BIPPED_LEFT_ARM, SkinPartTypes.BIPPED_LEFT_HAND, 4));
        it.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Limb(SkinPartTypes.BIPPED_RIGHT_ARM, SkinPartTypes.BIPPED_RIGHT_HAND, 4));
        it.put(SkinPartTypes.BIPPED_SKIRT, new Simple(SkinPartTypes.BIPPED_SKIRT));
        it.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Limb(SkinPartTypes.BIPPED_LEFT_THIGH, SkinPartTypes.BIPPED_LEFT_LEG, 6));
        it.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Limb(SkinPartTypes.BIPPED_RIGHT_THIGH, SkinPartTypes.BIPPED_RIGHT_LEG, 6));
        it.put(SkinPartTypes.BIPPED_LEFT_FOOT, new Simple(SkinPartTypes.BIPPED_LEFT_FOOT));
        it.put(SkinPartTypes.BIPPED_RIGHT_FOOT, new Simple(SkinPartTypes.BIPPED_RIGHT_FOOT));
        it.put(SkinPartTypes.BIPPED_LEFT_WING, new Simple(SkinPartTypes.BIPPED_LEFT_WING));
        it.put(SkinPartTypes.BIPPED_RIGHT_WING, new Simple(SkinPartTypes.BIPPED_RIGHT_WING));
    });

    interface Partition {
        Collection<SearchResult> subdivide(OpenRectangle3i rect);
    }

    private static Partition getPartition(SkinPartType partType) {
        if (ModConfig.Client.enablePartSubdivide) {
            var partition = PARTITIONS2.get(partType);
            if (partition != null) {
                return partition;
            }
        }
        return new Simple(partType);
    }

    public static Collection<SearchResult> allFaces(SkinGeometrySet<?> geometries, OpenRectangle3i bounds, SkinPartType partType) {
        var result = new SearchResult(partType, bounds, OpenVector3i.ZERO);
        for (int i = 0; i < geometries.size(); ++i) {
            var geometry = geometries.get(i);
            result.addLog(geometry.type());
            for (var face : geometry.faces()) {
                result.addFace(face);
            }
        }
        return Collections.singleton(result);
    }

    public static Collection<SearchResult> cullFaces2(SkinGeometrySet<?> geometries, OpenRectangle3i bounds, SkinPartType partType) {
        // The texture cube does not support static cull,
        // the slices are designed to contain multiple cube types,
        // but the skin culler can't support it now.
        var supportedTypes = geometries.supportedTypes();
        if (supportedTypes != null && (supportedTypes.contains(SkinGeometryTypes.CUBE) || supportedTypes.contains(SkinGeometryTypes.CUBE_CULL) || supportedTypes.contains(SkinGeometryTypes.MESH) || supportedTypes.contains(SkinGeometryTypes.MESH_CULL))) {
            return allFaces(geometries, bounds, partType);
        }
        var partition = getPartition(partType);
        var indexedMap = new IndexedMap(geometries, bounds);
        var results = partition.subdivide(bounds);
        for (var result : results) {
            result.cull(geometries, indexedMap);
        }
        for (int i = 0; i < geometries.size(); ++i) {
            var geometry = (SkinCube) geometries.get(i);
            for (var result : results) {
                result.addLog(geometry.type());
                for (var dir : OpenDirection.values()) {
                    if (result.flags.get(i * DIRECTION_SIZE + dir.get3DDataValue())) {
                        var face = geometry.getFace(dir);
                        if (face != null) {
                            result.addFace(face);
                        }
                    }
                }
            }
        }
        return results;
    }

    public static ArrayList<SkinGeometryFace> cullFaces(SkinGeometrySet<?> geometries, OpenRectangle3i bounds) {
        var indexedMap = new IndexedMap(geometries, bounds);
        var rect = new OpenRectangle3i(0, 0, 0, bounds.width(), bounds.height(), bounds.depth());
        var flags = cullFaceFlags(geometries, indexedMap, rect);
        var result = new SearchResult(SkinPartTypes.UNKNOWN, rect, OpenVector3i.ZERO);
        for (int i = 0; i < geometries.size(); ++i) {
            SkinCube geometry = null;
            for (var dir : OpenDirection.values()) {
                if (flags.get(i * DIRECTION_SIZE + dir.get3DDataValue())) {
                    if (geometry == null) {
                        geometry = (SkinCube) geometries.get(i);
                    }
                    var face = geometry.getFace(dir);
                    if (face != null) {
                        result.addFace(face);
                    }
                }
            }
        }
        return result.faces();
    }

    private static BitSet cullFaceFlags(SkinGeometrySet<?> geometries, IndexedMap map, OpenRectangle3i rect) {
        var flags = new BitSet(geometries.size() * DIRECTION_SIZE);
        var searchArea = new OpenRectangle3i(rect.x() - 1, rect.y() - 1, rect.z() - 1, rect.width() + 2, rect.height() + 2, rect.depth() + 2);
        var closedSet = new HashSet<OpenVector3i>();
        var openList = new ArrayDeque<OpenVector3i>();
        var start = searchArea.origin();
        openList.add(start);
        closedSet.add(start);
        map.limit(rect);
        while (!openList.isEmpty()) {
            var pendingList = new ArrayList<OpenVector3i>();
            var pos = openList.poll();
            for (var advance : OpenDirection.values()) {
                var pos1 = pos.relative(advance, 1);
                int targetIndex = map.get(pos1);
                if (targetIndex == -1) {
                    pendingList.add(pos1);
                    continue;
                }
                var isBlank = false;
                var targetGeometryType = geometries.get(targetIndex).type();
                if (SkinGeometryTypes.isGlassBlock(targetGeometryType)) {
                    pendingList.add(pos1);
                    // when source cube and target cube is linked glass, ignore.
                    int sourceIndex = map.get(pos);
                    if (sourceIndex != -1) {
                        isBlank = SkinGeometryTypes.isGlassBlock(geometries.get(sourceIndex).type());
                    }
                }
                // first, when not any rotation of the cube, it's always facing north.
                // then advance direction always opposite to cube facing direction,
                // so theory we should always use `advance.getOpposite()` get cube facing direction.
                // but actually we reset the cube origin in the indexes, which causes the relationship
                // between the forward direction and the facing is changed.
                var facing = advance;
                if (advance.axis() == OpenDirection.Axis.Z) {
                    facing = advance.opposite();
                }
                flags.set(targetIndex * DIRECTION_SIZE + facing.get3DDataValue(), !isBlank);
            }
            for (var pos1 : pendingList) {
                if (searchArea.contains(pos1) && !closedSet.contains(pos1)) {
                    closedSet.add(pos1);
                    openList.add(pos1);
                }
            }
        }
        return flags;
    }

    static class Simple implements Partition {

        final SkinPartType partType;

        public Simple(SkinPartType partType) {
            this.partType = partType;
        }

        @Override
        public Collection<SearchResult> subdivide(OpenRectangle3i rect) {
            var box = new OpenRectangle3i(0, 0, 0, rect.width(), rect.height(), rect.depth());
            return Collections.singleton(new SearchResult(partType, box, OpenVector3i.ZERO));
        }
    }

    static class Limb extends Simple {

        final SkinPartType upperPartType;
        final SkinPartType lowerPartType;

        final int yClip;

        public Limb(SkinPartType upperPartType, SkinPartType lowerPartType, int yClip) {
            super(upperPartType);
            this.upperPartType = upperPartType;
            this.lowerPartType = lowerPartType;
            this.yClip = yClip;
        }

        @Override
        public Collection<SearchResult> subdivide(OpenRectangle3i rect) {
            int upper = yClip - rect.minY();
            int lower = rect.maxY() - yClip;
            if (lower > 0 && upper > 0) {
                var upperBox = new OpenRectangle3i(0, 0, 0, rect.width(), upper, rect.depth());
                var lowerBox = new OpenRectangle3i(0, upper, 0, rect.width(), lower, rect.depth());
                // ..
                var upperResult = new SearchResult(upperPartType, upperBox, OpenVector3i.ZERO);
                var lowerResult = new SearchResult(lowerPartType, lowerBox, new OpenVector3i(0, -yClip, 0));
                return Collections.newList(upperResult, lowerResult);
            }
            return super.subdivide(rect);
        }
    }

    public static class SearchResult {

        protected final SkinPartType partType;
        protected final OpenRectangle3i bounds;
        protected final OpenVector3i origin;

        protected BitSet flags;
        protected SkinUsedCounter usedCounter;
        protected ArrayList<SkinGeometryFace> faces;

        public SearchResult(SkinPartType partType, OpenRectangle3i bounds, OpenVector3i origin) {
            this.faces = new ArrayList<>();
            this.usedCounter = new SkinUsedCounter();
            this.partType = partType;
            this.bounds = bounds;
            this.origin = origin;
        }

        public void addFace(SkinGeometryFace face) {
            this.faces.add(face);
        }

        public void addLog(SkinGeometryType geometryType) {
            this.usedCounter.addGeometryType(geometryType);
        }

        public void cull(SkinGeometrySet<?> geometries, IndexedMap map) {
            this.flags = cullFaceFlags(geometries, map, bounds);
        }

        public SkinPartType partType() {
            return partType;
        }

        public ArrayList<SkinGeometryFace> faces() {
            return faces;
        }

        public SkinUsedCounter usedCounter() {
            return usedCounter;
        }

        public OpenVector3i origin() {
            return origin;
        }

        public OpenRectangle3i bounds() {
            return bounds;
        }
    }

    public static class IndexedMap {

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

        public IndexedMap(SkinGeometrySet<?> geometries, OpenRectangle3i bounds) {
            this.x = bounds.x();
            this.y = bounds.y();
            this.z = bounds.z();
            this.width = bounds.width();
            this.height = bounds.height();
            this.depth = bounds.depth();
            this.indexes = new int[this.depth][this.height][this.width];
            int size = geometries.size();
            for (int i = 0; i < size; i++) {
                var geometry = (SkinCube) geometries.get(i);
                var blockPos = geometry.blockPos();
                int x = blockPos.x() - this.x;
                int y = blockPos.y() - this.y;
                int z = blockPos.z() - this.z;
                this.indexes[z][y][x] = i + 1;
            }
            this.limit(new OpenRectangle3i(0, 0, 0, this.width, this.height, this.depth));
        }

        public void limit(OpenRectangle3i limit) {
            this.minX = Math.max(limit.minX(), 0);
            this.minY = Math.max(limit.minY(), 0);
            this.minZ = Math.max(limit.minZ(), 0);
            this.maxX = Math.min(limit.maxX(), width);
            this.maxY = Math.min(limit.maxY(), height);
            this.maxZ = Math.min(limit.maxZ(), depth);
        }

        public int get(OpenVector3i pos) {
            return get(pos.x(), pos.y(), pos.z());
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
