package moe.plushie.armourers_workshop.core.skin.geometry;

import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryVertex;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.Objects;

public class SkinGeometryVertex implements ISkinGeometryVertex {

    protected int id;

    protected OpenVector3f position = OpenVector3f.ZERO;
    protected OpenVector3f normal = OpenVector3f.ZERO;
    protected OpenVector2f textureCoords = OpenVector2f.ZERO;

    protected Color color = Color.WHITE;

    public SkinGeometryVertex() {
    }

    public SkinGeometryVertex(int id, OpenVector3f position, OpenVector3f normal, OpenVector2f textureCoords) {
        this.id = id;
        this.position = position;
        this.normal = normal;
        this.textureCoords = textureCoords;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public OpenVector3f position() {
        return position;
    }

    @Override
    public OpenVector3f normal() {
        return normal;
    }

    @Override
    public OpenVector2f textureCoords() {
        return textureCoords;
    }

    @Override
    public Color color() {
        return color;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", id(), "position", position(), "normal", normal(), "uv", textureCoords(), "color", color());
    }

    public static class Color extends SkinPaintColor {

        public static final Color WHITE = new Color(SkinPaintColor.WHITE, 255);

        protected final int alpha;

        public Color(SkinPaintColor paintColor, int alpha) {
            super(paintColor.rawValue(), paintColor.argb(), paintColor.paintType());
            this.alpha = alpha;
        }

        public int alpha() {
            return alpha;
        }

        @Override
        public String toString() {
            var alpha = alpha();
            if (alpha < 255) {
                return String.format("%s * %f", super.toString(), alpha() / 255f);
            }
            return super.toString();
        }
    }
}
