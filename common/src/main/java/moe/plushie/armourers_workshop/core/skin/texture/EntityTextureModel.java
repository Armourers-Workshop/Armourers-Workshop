package moe.plushie.armourers_workshop.core.skin.texture;

import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class EntityTextureModel {

    public static final int TEXTURE_OLD_WIDTH = 64;
    public static final int TEXTURE_OLD_HEIGHT = 32;
    public static final int TEXTURE_OLD_SIZE = TEXTURE_OLD_WIDTH * TEXTURE_OLD_HEIGHT;

    public static final EntityTextureModel STAVE_V1 = new EntityTextureModel(Collections.immutableMap(builder -> {
        builder.put(SkinPartTypes.BIPPED_HAT, new Box(-4, -8, -4, 8, 8, 8, 32, 0));
        builder.put(SkinPartTypes.BIPPED_HEAD, new Box(-4, -8, -4, 8, 8, 8, 0, 0));
        builder.put(SkinPartTypes.BIPPED_CHEST, new Box(-4, 0, -2, 8, 12, 4, 16, 16));
        builder.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 0, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 0, 16, true)); // Mirror Right Leg
        builder.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(-3, -2, -2, 4, 12, 4, 40, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(-1, -2, -2, 4, 12, 4, 40, 16, true)); // Mirror Right Arm
    }));

    public static final EntityTextureModel STAVE_V2 = new EntityTextureModel(Collections.immutableMap(builder -> {
        builder.put(SkinPartTypes.BIPPED_HAT, new Box(-4, -8, -4, 8, 8, 8, 32, 0));
        builder.put(SkinPartTypes.BIPPED_HEAD, new Box(-4, -8, -4, 8, 8, 8, 0, 0));
        builder.put(SkinPartTypes.BIPPED_CHEST, new Box(-4, 0, -2, 8, 12, 4, 16, 16));
        builder.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 0, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 16, 48));
        builder.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(-3, -2, -2, 4, 12, 4, 40, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(-1, -2, -2, 4, 12, 4, 32, 48));
    }));

    public static final EntityTextureModel ALEX_V1 = new EntityTextureModel(Collections.immutableMap(builder -> {
        builder.put(SkinPartTypes.BIPPED_HAT, new Box(-4, -8, -4, 8, 8, 8, 32, 0));
        builder.put(SkinPartTypes.BIPPED_HEAD, new Box(-4, -8, -4, 8, 8, 8, 0, 0));
        builder.put(SkinPartTypes.BIPPED_CHEST, new Box(-4, 0, -2, 8, 12, 4, 16, 16));
        builder.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 0, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 0, 16, true)); // Mirror Right Leg
        builder.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(-2, -2, -2, 3, 12, 4, 40, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(-1, -2, -2, 3, 12, 4, 40, 16, true)); // Mirror Right Arm
    }));

    public static final EntityTextureModel ALEX_V2 = new EntityTextureModel(Collections.immutableMap(builder -> {
        builder.put(SkinPartTypes.BIPPED_HAT, new Box(-4, -8, -4, 8, 8, 8, 32, 0));
        builder.put(SkinPartTypes.BIPPED_HEAD, new Box(-4, -8, -4, 8, 8, 8, 0, 0));
        builder.put(SkinPartTypes.BIPPED_CHEST, new Box(-4, 0, -2, 8, 12, 4, 16, 16));
        builder.put(SkinPartTypes.BIPPED_RIGHT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 0, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_THIGH, new Box(-2, 0, -2, 4, 12, 4, 16, 48));
        builder.put(SkinPartTypes.BIPPED_RIGHT_ARM, new Box(-2, -2, -2, 3, 12, 4, 40, 16));
        builder.put(SkinPartTypes.BIPPED_LEFT_ARM, new Box(-1, -2, -2, 3, 12, 4, 32, 48));
    }));
    protected final Map<SkinPartType, Box> skyBoxes;

    public EntityTextureModel(Map<SkinPartType, Box> skyBoxes) {
        this.skyBoxes = skyBoxes;
    }

    public static EntityTextureModel of(int width, int height, boolean slim) {
        if (slim) {
            if (height <= 32) {
                return ALEX_V1;
            }
            return ALEX_V2;
        } else {
            if (height <= 32) {
                return STAVE_V1;
            }
            return STAVE_V2;
        }
    }

    public Box get(SkinPartType partType) {
        if (partType == SkinPartTypes.BIPPED_LEFT_FOOT) {
            return get(SkinPartTypes.BIPPED_LEFT_THIGH);
        }
        if (partType == SkinPartTypes.BIPPED_RIGHT_FOOT) {
            return get(SkinPartTypes.BIPPED_RIGHT_THIGH);
        }
        return skyBoxes.get(partType);
    }

    public OpenVector2i get(SkinPartType partType, int x, int y, int z, OpenDirection dir) {
        var box = get(partType);
        if (box != null) {
            return box.get(x, y, z, dir);
        }
        return null;
    }

    public void forEach(BiConsumer<SkinPartType, Box> consumer) {
        skyBoxes.forEach(consumer);
    }

    public Set<Map.Entry<SkinPartType, Box>> entrySet() {
        return skyBoxes.entrySet();
    }

    public static class Box {

        protected final EnumMap<OpenDirection, ToIntFunction<OpenVector3i>> validator = new EnumMap<>(OpenDirection.class);
        protected final EnumMap<OpenDirection, Function<OpenVector3i, OpenVector2i>> evaluator = new EnumMap<>(OpenDirection.class);
        protected final OpenRectangle3i rect;
        protected final OpenRectangle2f textureRect;
        protected final boolean mirror;

        public Box(int x, int y, int z, int width, int height, int depth, int u, int v) {
            this(x, y, z, width, height, depth, u, v, false);
        }

        public Box(int x, int y, int z, int width, int height, int depth, int u, int v, boolean mirror) {
            this.mirror = mirror;
            this.rect = new OpenRectangle3i(x, y, z, width, height, depth);
            this.textureRect = new OpenRectangle2f(u, v, depth + width + depth + width, depth + height);
            // we are assuming front side always facing north.
            this.put(OpenDirection.UP, positiveX(u + depth), negativeZ(v + depth - 1));
            this.put(OpenDirection.DOWN, positiveX(u + depth + width), negativeZ(v + depth - 1));
            this.put(OpenDirection.NORTH, positiveX(u + depth), positiveY(v + depth));
            this.put(OpenDirection.SOUTH, negativeX(u + depth + width + depth + width - 1), positiveY(v + depth));
            this.put(OpenDirection.WEST, positiveZ(u + depth + width), positiveY(v + depth));
            this.put(OpenDirection.EAST, negativeZ(u + depth - 1), positiveY(v + depth));

            this.valid(OpenDirection.UP, negativeY(0));
            this.valid(OpenDirection.DOWN, negativeY(height - 1));
            this.valid(OpenDirection.NORTH, negativeZ(0));
            this.valid(OpenDirection.SOUTH, negativeZ(depth - 1));
            this.valid(OpenDirection.WEST, negativeX(width - 1));
            this.valid(OpenDirection.EAST, negativeX(0));
        }

        protected void valid(OpenDirection dir, ToIntFunction<OpenVector3i> diff) {
            validator.put(getMirroredDirection(dir), diff);
        }

        protected void put(OpenDirection dir, ToIntFunction<OpenVector3i> uf, ToIntFunction<OpenVector3i> vf) {
            evaluator.put(getMirroredDirection(dir), pos -> new OpenVector2i(uf.applyAsInt(pos), vf.applyAsInt(pos)));
        }

        public void forEach(IPixelConsumer consumer) {
            for (var dir : OpenDirection.values()) {
                for (int ix = rect.minX(); ix < rect.maxX(); ++ix) {
                    for (int iy = rect.minY(); iy < rect.maxY(); ++iy) {
                        for (int iz = rect.minZ(); iz < rect.maxZ(); ++iz) {
                            var texture = get(ix, iy, iz, dir);
                            if (texture != null) {
                                consumer.accept(texture, ix, iy, iz, dir);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Box that)) return false;
            return mirror == that.mirror && rect.equals(that.rect) && textureRect.equals(that.textureRect);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rect, textureRect, mirror);
        }

        @Nullable
        public OpenVector2i get(int x, int y, int z, OpenDirection dir) {
            x -= rect.x();
            y -= rect.y();
            z -= rect.z();
            if (isInside(x, y, z)) {
                var predicate = validator.get(dir);
                var eval = evaluator.get(dir);
                var pos = new OpenVector3i(x, y, z);
                if (eval != null && predicate.applyAsInt(pos) == 0) {
                    return eval.apply(pos);
                }
            }
            return null;
        }

        public OpenRectangle3i getBounds() {
            return rect;
        }

        private boolean isInside(int x, int y, int z) {
            return (x >= 0 && x < rect.width()) && (y >= 0 && y < rect.height()) && (z >= 0 && z < rect.depth());
        }

        private OpenDirection getMirroredDirection(OpenDirection direction) {
            // when mirroring occurs, the contents of the WEST and EAST sides will be swapped.
            if (mirror && direction.getAxis() == OpenDirection.Axis.X) {
                return direction.getOpposite();
            }
            return direction;
        }

        private ToIntFunction<OpenVector3i> positiveX(int t) {
            if (mirror) {
                return pos -> t + (rect.width() - pos.x() - 1);
            }
            return pos -> t + pos.x();
        }

        private ToIntFunction<OpenVector3i> positiveY(int t) {
            return pos -> t + pos.y();
        }

        private ToIntFunction<OpenVector3i> positiveZ(int t) {
            return pos -> t + pos.z();
        }

        private ToIntFunction<OpenVector3i> negativeX(int t) {
            if (mirror) {
                return pos -> t - (rect.width() - pos.x() - 1);
            }
            return pos -> t - pos.x();
        }

        private ToIntFunction<OpenVector3i> negativeY(int t) {
            return pos -> t - pos.y();
        }

        private ToIntFunction<OpenVector3i> negativeZ(int t) {
            return pos -> t - pos.z();
        }

        public interface IPixelConsumer {
            void accept(OpenVector2i texture, int x, int y, int z, OpenDirection dir);
        }
    }
}
