package moe.plushie.armourers_workshop.utils.texture;

import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class SkyBox {

    protected final EnumMap<Direction, ToIntFunction<Vector3i>> validator = new EnumMap<>(Direction.class);
    protected final EnumMap<Direction, Function<Vector3i, TexturePos>> evaluator = new EnumMap<>(Direction.class);
    protected final Rectangle3i rect;
    protected final CGRect textureRect;
    protected final boolean mirror;

    public SkyBox(int x, int y, int z, int width, int height, int depth, int u, int v) {
        this(x, y, z, width, height, depth, u, v, false);
    }

    public SkyBox(int x, int y, int z, int width, int height, int depth, int u, int v, boolean mirror) {
        this.mirror = mirror;
        this.rect = new Rectangle3i(x, y, z, width, height, depth);
        this.textureRect = new CGRect(u, v, depth + width + depth + width, depth + height);
        // we are assuming front side always facing north.
        this.put(Direction.UP, positiveX(u + depth), negativeZ(v + depth - 1));
        this.put(Direction.DOWN, positiveX(u + depth + width), negativeZ(v + depth - 1));
        this.put(Direction.NORTH, positiveX(u + depth), positiveY(v + depth));
        this.put(Direction.SOUTH, negativeX(u + depth + width + depth + width - 1), positiveY(v + depth));
        this.put(Direction.WEST, positiveZ(u + depth + width), positiveY(v + depth));
        this.put(Direction.EAST, negativeZ(u + depth - 1), positiveY(v + depth));

        this.valid(Direction.UP, negativeY(0));
        this.valid(Direction.DOWN, negativeY(height - 1));
        this.valid(Direction.NORTH, negativeZ(0));
        this.valid(Direction.SOUTH, negativeZ(depth - 1));
        this.valid(Direction.WEST, negativeX(width - 1));
        this.valid(Direction.EAST, negativeX(0));
    }

    protected void valid(Direction dir, ToIntFunction<Vector3i> diff) {
        validator.put(getMirroredDirection(dir), diff);
    }

    protected void put(Direction dir, ToIntFunction<Vector3i> uf, ToIntFunction<Vector3i> vf) {
        evaluator.put(getMirroredDirection(dir), pos -> new TexturePos(uf.applyAsInt(pos), vf.applyAsInt(pos)));
    }

    public void forEach(IPixelConsumer consumer) {
        for (Direction dir : Direction.values()) {
            for (int ix = rect.getMinX(); ix < rect.getMaxX(); ++ix) {
                for (int iy = rect.getMinY(); iy < rect.getMaxY(); ++iy) {
                    for (int iz = rect.getMinZ(); iz < rect.getMaxZ(); ++iz) {
                        TexturePos texture = get(ix, iy, iz, dir);
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
        if (!(o instanceof SkyBox)) return false;
        SkyBox skyBox = (SkyBox) o;
        return mirror == skyBox.mirror && rect.equals(skyBox.rect) && textureRect.equals(skyBox.textureRect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rect, textureRect, mirror);
    }

    @Nullable
    public TexturePos get(int x, int y, int z, Direction dir) {
        x -= rect.getX();
        y -= rect.getY();
        z -= rect.getZ();
        if (isInside(x, y, z)) {
            ToIntFunction<Vector3i> predicate = validator.get(dir);
            Function<Vector3i, TexturePos> eval = evaluator.get(dir);
            Vector3i pos = new Vector3i(x, y, z);
            if (eval != null && predicate.applyAsInt(pos) == 0) {
                return eval.apply(pos);
            }
        }
        return null;
    }

    public Rectangle3i getBounds() {
        return rect;
    }

    private boolean isInside(int x, int y, int z) {
        return (x >= 0 && x < rect.getWidth()) && (y >= 0 && y < rect.getHeight()) && (z >= 0 && z < rect.getDepth());
    }

    private Direction getMirroredDirection(Direction direction) {
        // when mirroring occurs, the contents of the WEST and EAST sides will be swapped.
        if (mirror && direction.getAxis() == Direction.Axis.X) {
            return direction.getOpposite();
        }
        return direction;
    }

    private ToIntFunction<Vector3i> positiveX(int t) {
        if (mirror) {
            return pos -> t + (rect.getWidth() - pos.getX() - 1);
        }
        return pos -> t + pos.getX();
    }

    private ToIntFunction<Vector3i> positiveY(int t) {
        return pos -> t + pos.getY();
    }

    private ToIntFunction<Vector3i> positiveZ(int t) {
        return pos -> t + pos.getZ();
    }

    private ToIntFunction<Vector3i> negativeX(int t) {
        if (mirror) {
            return pos -> t - (rect.getWidth() - pos.getX() - 1);
        }
        return pos -> t - pos.getX();
    }

    private ToIntFunction<Vector3i> negativeY(int t) {
        return pos -> t - pos.getY();
    }

    private ToIntFunction<Vector3i> negativeZ(int t) {
        return pos -> t - pos.getZ();
    }

    public interface IPixelConsumer {
        void accept(TexturePos texture, int x, int y, int z, Direction dir);
    }
}
