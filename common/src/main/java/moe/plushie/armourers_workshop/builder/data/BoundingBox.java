package moe.plushie.armourers_workshop.builder.data;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;

import java.util.Objects;

public class BoundingBox extends OpenRectangle3i {

    public static final EntityTextureModel MODEL = EntityTextureModel.STAVE_V2;
    public static final EntityTextureModel SLIM_MODEL = EntityTextureModel.ALEX_V2;

    private final SkinPartType partType;

    public BoundingBox(SkinPartType partType, OpenRectangle3i rect) {
        super(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth());
        this.partType = partType;
    }

    public void forEach(IPixelConsumer consumer) {
        for (int ix = 0; ix < width(); ix++) {
            for (int iy = 0; iy < height(); iy++) {
                for (int iz = 0; iz < depth(); iz++) {
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

    public SkinPartType getPartType() {
        return partType;
    }

    public interface IPixelConsumer {
        void accept(int ix, int iy, int iz);
    }
}
