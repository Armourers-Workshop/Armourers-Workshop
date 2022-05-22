package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.model.PlayerTextureModel;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;

import java.awt.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BoundingBox extends Rectangle3i {

    private final ISkinPartType partType;

    public BoundingBox(ISkinPartType partType, Rectangle3i rect) {
        super(rect.getX(), rect.getY(), rect.getZ(), rect.getWidth(), rect.getHeight(), rect.getDepth());
        this.partType = partType;
    }

    public static void setColor(ISkinPartType partType, Vector3i offset, Direction dir, IPaintColor color, BiConsumer<Point, IPaintColor> applier) {
        Point texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            applier.accept(texturePos, color);
        }
    }

    public static IPaintColor getColor(ISkinPartType partType, Vector3i offset, Direction dir, Function<Point, IPaintColor> supplier) {
        Point texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            return supplier.apply(texturePos);
        }
        return PaintColor.CLEAR;
    }

    public static Point getTexturePos(ISkinPartType partType, Vector3i offset, Direction dir) {
        SkyBox box = PlayerTextureModel.STAVE_V2.get(partType);
        if (box == null) {
            return null;
        }
        Rectangle3i rect = box.getBounds();
        return box.get(rect.getX() + offset.getX(), rect.getY() + offset.getY(), rect.getZ() + offset.getZ(), dir);
    }

    public void forEach(IPixelConsumer consumer) {
        for (int ix = 0; ix < getWidth(); ix++) {
            for (int iy = 0; iy < getHeight(); iy++) {
                for (int iz = 0; iz < getDepth(); iz++) {
                    consumer.accept(ix, iy, iz);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoundingBox)) return false;
        if (!super.equals(o)) return false;
        BoundingBox that = (BoundingBox) o;
        return partType.equals(that.partType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), partType);
    }

    public ISkinPartType getPartType() {
        return partType;
    }


    public interface IPixelConsumer {

        void accept(int ix, int iy, int iz);
    }
}
