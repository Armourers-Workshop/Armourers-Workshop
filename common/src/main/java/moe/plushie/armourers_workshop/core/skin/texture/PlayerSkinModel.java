package moe.plushie.armourers_workshop.core.skin.texture;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum PlayerSkinModel {
    /**
     * A 64x64 wide player model.
     */
    WIDE(Collections.immutableMap(it -> {
        it.put(SkinPartTypes.BIPPED_HAT, new Box(32, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_HEAD, new Box(0, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_CHEST, new Box(16, 16, 8, 12, 4));
        it.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(40, 16, 4, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(32, 48, 4, 12, 4));
        it.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(0, 16, 4, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(16, 48, 4, 12, 4));
    })),
    /**
     * A 64x64 slim player model.
     */
    SLIM(Collections.immutableMap(it -> {
        it.put(SkinPartTypes.BIPPED_HAT, new Box(32, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_HEAD, new Box(0, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_CHEST, new Box(16, 16, 8, 12, 4));
        it.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(40, 16, 3, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(32, 48, 3, 12, 4));
        it.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(0, 16, 4, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(16, 48, 4, 12, 4));
    })),
    /**
     * A 64x32 wide player model (legacy).
     */
    LEGACY_WIDE(Collections.immutableMap(it -> {
        it.put(SkinPartTypes.BIPPED_HAT, new Box(32, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_HEAD, new Box(0, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_CHEST, new Box(16, 16, 8, 12, 4));
        it.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(40, 16, 4, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(40, 16, 4, 12, 4, true)); // Mirror Right Arm
        it.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(0, 16, 4, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(0, 16, 4, 12, 4, true)); // Mirror Right Leg
    })),
    /**
     * A 64x32 slim player model (legacy).
     */
    LEGACY_SLIM(Collections.immutableMap(it -> {
        it.put(SkinPartTypes.BIPPED_HAT, new Box(32, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_HEAD, new Box(0, 0, 8, 8, 8));
        it.put(SkinPartTypes.BIPPED_CHEST, new Box(16, 16, 8, 12, 4));
        it.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(40, 16, 3, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(40, 16, 3, 12, 4, true)); // Mirror Right Arm
        it.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(0, 16, 4, 12, 4));
        it.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(0, 16, 4, 12, 4, true)); // Mirror Right Leg
    }));

    public static final IDataCodec<PlayerSkinModel> CODEC = IDataCodec.STRING.xmap(PlayerSkinModel::from, PlayerSkinModel::serializedName);

    private final Map<SkinPartType, Box> box;

    PlayerSkinModel(Map<SkinPartType, Box> box) {
        this.box = box;
    }

    public static PlayerSkinModel from(int width, int height, boolean slim) {
        if (slim) {
            if (height <= 32) {
                return LEGACY_SLIM;
            }
            return SLIM;
        } else {
            if (height <= 32) {
                return LEGACY_WIDE;
            }
            return WIDE;
        }
    }

    public static PlayerSkinModel from(String name) {
        for (var value : PlayerSkinModel.values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return PlayerSkinModel.WIDE;
    }

    public void forEach(BiConsumer<SkinPartType, Box> consumer) {
        box.forEach(consumer);
    }

    @Nullable
    public Box get(SkinPartType partType) {
        if (partType == SkinPartTypes.BIPPED_LEFT_FOOT) {
            return get(SkinPartTypes.BIPPED_LEFT_THIGH);
        }
        if (partType == SkinPartTypes.BIPPED_RIGHT_FOOT) {
            return get(SkinPartTypes.BIPPED_RIGHT_THIGH);
        }
        return box.get(partType);
    }

    @Nullable
    public OpenVector2i get(int x, int y, int z, OpenDirection dir, SkinPartType partType) {
        var box = get(partType);
        if (box != null) {
            return box.get(x, y, z, dir);
        }
        return null;
    }

    public boolean slim() {
        return this == SLIM || this == LEGACY_SLIM;
    }

    public String serializedName() {
        return name().toLowerCase();
    }

    /**
     * <pre>
     *        +-----+-----+
     *        |  U  |  D  |
     *  +-----+-----+-----+-----+
     *  |  R  |  F  |  L  |  B  |
     *  +-----+-----+-----+-----+
     * </pre>
     */
    public static class Box {

        protected final int width;
        protected final int height;
        protected final int depth;

        protected final boolean mirror;

        protected final Int2ObjectOpenHashMap<OpenVector2i> points = new Int2ObjectOpenHashMap<>();
        protected final EnumMap<OpenDirection, OpenRectangle2i> values = new EnumMap<>(OpenDirection.class);

        public Box(int u, int v, int width, int height, int depth) {
            this(u, v, width, height, depth, false, false);
        }

        public Box(int u, int v, int width, int height, int depth, boolean mirrorX) {
            this(u, v, width, height, depth, mirrorX, false);
        }

        public Box(int u, int v, int width, int height, int depth, boolean mirrorX, boolean mirrorY) {
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.mirror = mirrorX;
            // we are assuming front side always facing north.
            this.put(OpenDirection.UP, depth, 0, width, depth, mirrorX, !mirrorY, u, v, it -> new OpenVector3i(it.x, 0, it.y));
            this.put(OpenDirection.DOWN, depth + width, 0, width, depth, mirrorX, !mirrorY, u, v, it -> new OpenVector3i(it.x, height - 1, it.y));
            this.put(OpenDirection.NORTH, depth, depth, width, height, mirrorX, mirrorY, u, v, it -> new OpenVector3i(it.x, it.y, 0));
            this.put(OpenDirection.SOUTH, depth + width + depth, depth, width, height, !mirrorX, mirrorY, u, v, it -> new OpenVector3i(it.x, it.y, depth - 1));
            this.put(OpenDirection.WEST, depth + width, depth, depth, height, mirrorX, mirrorY, u, v, it -> new OpenVector3i(width - 1, it.y, it.x));
            this.put(OpenDirection.EAST, 0, depth, depth, height, !mirrorX, mirrorY, u, v, it -> new OpenVector3i(0, it.y, it.x));
        }

        public void forEach(IPixelConsumer consumer) {
            for (var entry : points.int2ObjectEntrySet()) {
                var key = entry.getIntKey();
                var value = entry.getValue();
                // key = [dddd dzzz][zzzz zzyy][yyyy yyyx][xxxx xxxx]
                var dir = OpenDirection.from3DDataValue((key >> 27) & 0x1f);
                var z = (key >> 18) & 0x1ff;
                var y = (key >> 9) & 0x1ff;
                var x = key & 0x1ff;
                consumer.accept(value, x, y, z, dir);
            }
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public int depth() {
            return depth;
        }

        public boolean isMirror() {
            return mirror;
        }

        public OpenRectangle2i get(OpenDirection dir) {
            return values.get(dir);
        }

        public OpenVector2i get(int x, int y, int z, OpenDirection dir) {
            // it is over size?
            if (x < 0 || y < 0 || z < 0 || x > 512 || y > 512 || z > 512) {
                return null;
            }
            return points.get(key(x, y, z, dir));
        }

        private void put(OpenDirection dir, int x, int y, int width, int height, boolean mirrorX, boolean mirrorY, int baseX, int baseY, Function<OpenVector2i, OpenVector3i> transformer) {
            // when mirroring occurs, the contents of the WEST and EAST sides will be swapped.
            if (mirror && dir.axis() == OpenDirection.Axis.X) {
                dir = dir.opposite();
            }
            // create a text rect.
            this.values.put(dir, new OpenRectangle2i(x, y, width, height));
            // fill the all point.
            for (int j = 0; j < height; ++j) {
                for (int i = 0; i < width; ++i) {
                    var tx = i;
                    if (mirrorX) {
                        tx = width - i - 1;
                    }
                    var ty = j;
                    if (mirrorY) {
                        ty = height - j - 1;
                    }
                    var pos = new OpenVector2i(baseX + x + tx, baseY + y + ty);
                    var location = transformer.apply(new OpenVector2i(i, j));
                    points.put(key(location.x, location.y, location.z, dir), pos);
                }
            }
        }

        private int key(int x, int y, int z, OpenDirection dir) {
            // key = [dddd dzzz][zzzz zzyy][yyyy yyyx][xxxx xxxx]
            return dir.get3DDataValue() << 27 | z << 18 | y << 9 | x;
        }
    }

    public interface IPixelConsumer {
        void accept(OpenVector2i texture, int x, int y, int z, OpenDirection dir);
    }
}
