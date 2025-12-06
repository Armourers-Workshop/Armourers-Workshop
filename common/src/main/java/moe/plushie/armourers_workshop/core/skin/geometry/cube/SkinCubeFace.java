package moe.plushie.armourers_workshop.core.skin.geometry.cube;

import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryOptions;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.util.ArrayList;

public class SkinCubeFace extends SkinGeometryFace {

    private final int alpha;

    private final SkinGeometryType type;
    private final SkinGeometryOptions options;
    private final OpenDirection direction;
    private final SkinPaintColor paintColor;

    private final OpenRectangle3f boundingBox;

    public SkinCubeFace(int id, SkinGeometryType type, SkinGeometryOptions options, OpenTransform3f transform, SkinTexturePos texturePos, OpenRectangle3f boundingBox, OpenDirection direction, SkinPaintColor color, int alpha) {
        this.id = id;
        this.type = type;
        this.options = options;
        this.transform = transform;
        this.texturePos = texturePos;
        this.paintColor = color;
        this.alpha = alpha;
        this.direction = direction;
        this.boundingBox = boundingBox;
    }

    public static float[][] getBaseUVs(OpenDirection direction, int rot) {
        return switch (rot) {
            case 90 -> Helper.UVS_90[direction.get3DDataValue()];
            case 180 -> Helper.UVS_180[direction.get3DDataValue()];
            case 270 -> Helper.UVS_270[direction.get3DDataValue()];
            default -> Helper.UVS[direction.get3DDataValue()];
        };
    }

    public static float[][] getBaseVertices(OpenDirection direction) {
        return Helper.VERTICES[direction.get3DDataValue()];
    }

    public OpenRectangle3f boundingBox() {
        return boundingBox;
    }

    public SkinPaintColor color() {
        return paintColor;
    }

    public int alpha() {
        return alpha;
    }

    public OpenDirection direction() {
        return direction;
    }

    public SkinPaintType paintType() {
        return paintColor.paintType();
    }

    @Override
    public SkinGeometryType type() {
        return type;
    }

    @Override
    public SkinGeometryOptions options() {
        return options;
    }

    @Override
    public SkinTexturePos texturePos() {
        if (texturePos != null) {
            return texturePos;
        }
        return paintColor.paintType().texturePos();
    }

    @Override
    public float priority() {
        var priority = direction.get3DDataValue();
        return switch (options.renderOrder()) {
            case 1 -> priority - 1000; // behind
            case 2 -> priority + 1000; // in_front
            default -> priority;
        };
    }

    @Override
    public boolean isVisible() {
        return paintColor.paintType() != SkinPaintTypes.NONE;
    }

    @Override
    public Iterable<? extends SkinGeometryVertex> vertices() {
        var id = id();
        var texturePos = texturePos();
        var textureRotation = getTextureRotation(texturePos);

        // we need inflate bounding box, which will avoid the size is zero.
        // when size is zero, it will cause the z-flight problems.
        var inflate = 0.01f;

        // https://learnopengl.com/Getting-started/Coordinate-Systems
        var x = boundingBox.x() - inflate / 2;
        var y = boundingBox.y() - inflate / 2;
        var z = boundingBox.z() - inflate / 2;
        var w = boundingBox.width() + inflate;
        var h = boundingBox.height() + inflate;
        var d = boundingBox.depth() + inflate;

        var u = texturePos.u();
        var v = texturePos.v();
        var s = texturePos.width() * 0.98f;
        var t = texturePos.height() * 0.98f;

        var color = new SkinGeometryVertex.Color(paintColor, alpha);
        var vertices = new ArrayList<SkinGeometryVertex>();

        var vertexes = getBaseVertices(direction);
        var uvs = getBaseUVs(direction, textureRotation);

        for (int i = 0; i < 4; ++i) {
            var position = new OpenVector3f(x + w * vertexes[i][0], y + h * vertexes[i][1], z + d * vertexes[i][2]);
            var normal = new OpenVector3f(vertexes[4][0], vertexes[4][1], vertexes[4][2]);
            var textureCoords = new OpenVector2f(u + s * uvs[i][0], v + t * uvs[i][1]);
            vertices.add(new SkinCubeVertex(id * 4 + i, position, normal, textureCoords, color, this));
        }

        return vertices;
    }

    private int getTextureRotation(SkinTexturePos key) {
        var options = key.options();
        if (options != null) {
            return options.rotation();
        }
        return 0;
    }

    private static class Helper {

        public static final float[][][] VERTICES = new float[][][]{
                {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // -y <- down
                {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // +y <- up
                {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // -z <- north
                {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // +z <- south
                {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // -x <- west
                {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // +x <- east
        };

        public static final float[][][] UVS = new float[][][]{
                {{1, 0}, {1, 1}, {0, 1}, {0, 0}}, // -y <- down
                {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +y <- up
                {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // -z <- north
                {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +z <- south
                {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // -x <- west
                {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +x <- east
        };

        public static final float[][][] UVS_90 = new float[][][]{
                {{1, 1}, {0, 1}, {0, 0}, {1, 0}}, // -y <- down
                {{0, 1}, {1, 1}, {1, 0}, {0, 0}}, // +y <- up
                {{0, 1}, {1, 1}, {1, 0}, {0, 0}}, // -z <- north
                {{0, 1}, {1, 1}, {1, 0}, {0, 0}}, // +z <- south
                {{0, 1}, {1, 1}, {1, 0}, {0, 0}}, // -x <- west
                {{0, 1}, {1, 1}, {1, 0}, {0, 0}}, // +x <- east
        };

        public static final float[][][] UVS_180 = new float[][][]{
                {{0, 1}, {0, 0}, {1, 0}, {1, 1}}, // -y <- down
                {{1, 1}, {1, 0}, {0, 0}, {0, 1}}, // +y <- up
                {{1, 1}, {1, 0}, {0, 0}, {0, 1}}, // -z <- north
                {{1, 1}, {1, 0}, {0, 0}, {0, 1}}, // +z <- south
                {{1, 1}, {1, 0}, {0, 0}, {0, 1}}, // -x <- west
                {{1, 1}, {1, 0}, {0, 0}, {0, 1}}, // +x <- east
        };

        public static final float[][][] UVS_270 = new float[][][]{
                {{0, 0}, {1, 0}, {1, 1}, {0, 1}}, // -y <- down
                {{1, 0}, {0, 0}, {0, 1}, {1, 1}}, // +y <- up
                {{1, 0}, {0, 0}, {0, 1}, {1, 1}}, // -z <- north
                {{1, 0}, {0, 0}, {0, 1}, {1, 1}}, // +z <- south
                {{1, 0}, {0, 0}, {0, 1}, {1, 1}}, // -x <- west
                {{1, 0}, {0, 0}, {0, 1}, {1, 1}}, // +x <- east
        };
    }
}
