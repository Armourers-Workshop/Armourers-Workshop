package moe.plushie.armourers_workshop.builder.data;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.texture.PlayerTextureModel;
import net.minecraft.core.Direction;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BoundingBox extends Rectangle3i {

    public static final PlayerTextureModel MODEL = PlayerTextureModel.STAVE_V2;

    private final ISkinPartType partType;

    public BoundingBox(ISkinPartType partType, Rectangle3i rect) {
        super(rect.getX(), rect.getY(), rect.getZ(), rect.getWidth(), rect.getHeight(), rect.getDepth());
        this.partType = partType;
    }

    public static void setColor(ISkinPartType partType, Vector3i offset, Direction dir, IPaintColor color, BiConsumer<TexturePos, IPaintColor> applier) {
        var texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            applier.accept(texturePos, color);
        }
    }

    public static IPaintColor getColor(ISkinPartType partType, Vector3i offset, Direction dir, Function<TexturePos, IPaintColor> supplier) {
        var texturePos = getTexturePos(partType, offset, dir);
        if (texturePos != null) {
            return supplier.apply(texturePos);
        }
        return PaintColor.CLEAR;
    }

    public static TexturePos getTexturePos(ISkinPartType partType, Vector3i offset, Direction dir) {
        var box = MODEL.get(partType);
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
        if (!(o instanceof BoundingBox that)) return false;
        if (!super.equals(o)) return false;
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
