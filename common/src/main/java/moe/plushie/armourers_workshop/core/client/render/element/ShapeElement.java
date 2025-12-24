package moe.plushie.armourers_workshop.core.client.render.element;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.core.math.IRectangle3i;
import moe.plushie.armourers_workshop.api.core.math.IVector3f;
import moe.plushie.armourers_workshop.api.core.math.IVector3i;
import moe.plushie.armourers_workshop.api.core.math.IVoxelShape;
import moe.plushie.armourers_workshop.core.armature.JointShape;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.ObjectPool;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@SuppressWarnings("unsed")
public abstract class ShapeElement implements IGraphicsElement {

    public static ShapeElement arrow() {
        return arrow(0, 0, 0, 1, 1, 1);
    }

    public static ShapeElement arrow(float length) {
        return arrow(0, 0, 0, length, length, length);
    }

    public static ShapeElement arrow(IVector3i origin) {
        return arrow(origin.x(), origin.y(), origin.z(), 1, 1, 1);
    }

    public static ShapeElement arrow(IVector3f pos) {
        return arrow(pos.x(), pos.y(), pos.z(), 1, 1, 1);
    }

    public static ShapeElement arrow(IVector3i pos, float length) {
        return arrow(pos.x(), pos.y(), pos.z(), length, length, length);
    }

    public static ShapeElement arrow(IVector3f pos, float length) {
        return arrow(pos.x(), pos.y(), pos.z(), length, length, length);
    }

    public static ShapeElement arrow(float x, float y, float z, int length) {
        return arrow(x, y, z, length, length, length);
    }

    public static ShapeElement arrow(float x, float y, float z, float w, float h, float d) {
        return Arrow.newInstance(x, y, z, w, h, d);
    }

    public static ShapeElement stroke(IRectangle3i rect, int color) {
        return stroke(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), color, SkinRenderType.line());
    }

    public static ShapeElement stroke(IRectangle3f rect, int color) {
        return stroke(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), color, SkinRenderType.line());
    }

    public static ShapeElement stroke(float x, float y, float z, float w, float h, float d, int color) {
        return stroke(x, y, z, w, h, d, color, SkinRenderType.line());
    }

    public static ShapeElement stroke(double x, double y, double z, double w, double h, double d, int color) {
        return stroke((float) x, (float) y, (float) z, (float) w, (float) h, (float) d, color, SkinRenderType.line());
    }

    public static ShapeElement stroke(float x, float y, float z, float w, float h, float d, int color, IRenderType renderType) {
        return stroke(x, y, z, w, h, d, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, color, renderType);
    }

    public static ShapeElement stroke(float x, float y, float z, float w, float h, float d, int lightmap, int overlay, int color, IRenderType renderType) {
        var cube = Cube.newInstance(x, y, z, x + w, y + h, z + d, lightmap, overlay, renderType);
        cube.setTextureOffset(0, 0, 0);
        cube.setTextureSize(1, 1, 1);
        cube.setColor(color);
        return cube;
    }


    public static ShapeElement fill(IRectangle3i rect, int lightmap, int overlay, BlockPaintColor color, IRenderType renderType) {
        return fill(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), color, lightmap, overlay, renderType);
    }

    public static ShapeElement fill(IRectangle3f rect, int lightmap, int overlay, BlockPaintColor color, IRenderType renderType) {
        return fill(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), color, lightmap, overlay, renderType);
    }

    public static ShapeElement fill(float x, float y, float z, float w, float h, float d, BlockPaintColor color, int lightmap, int overlay, IRenderType renderType) {
        var cube = Cube.newInstance(x, y, z, x + w, y + h, z + d, lightmap, overlay, renderType);
        cube.setTextureOffset(0, 0, 0);
        cube.setTextureSize(1, 1, 1);
        for (var dir : OpenDirection.values()) {
            var paintColor = color.getOrDefault(dir, SkinPaintColor.WHITE);
            cube.setColor(dir, paintColor.argb());
        }
        return cube;
    }

    public static ShapeElement fill(IRectangle3i rect, int lightmap, int overlay, int color, IRenderType renderType) {
        return fill(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), lightmap, overlay, color, renderType);
    }

    public static ShapeElement fill(IRectangle3f rect, int lightmap, int overlay, int color, IRenderType renderType) {
        return fill(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), lightmap, overlay, color, renderType);
    }

    public static ShapeElement fill(float x, float y, float z, float w, float h, float d, int color, IRenderType renderType) {
        return fill(x, y, z, w, h, d, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, color, renderType);
    }

    public static ShapeElement fill(float x, float y, float z, float w, float h, float d, int lightmap, int overlay, int color, IRenderType renderType) {
        var cube = Cube.newInstance(x, y, z, x + w, y + h, z + d, lightmap, overlay, renderType);
        cube.setTextureOffset(0, 0, 0);
        cube.setTextureSize(1, 1, 1);
        cube.setColor(color);
        return cube;
    }

    public static ShapeElement line(float x0, float y0, float z0, float x1, float y1, float z1, int color) {
        return line(x0, y0, z0, x1, y1, z1, color, SkinRenderType.line());
    }

    public static ShapeElement line(float x0, float y0, float z0, float x1, float y1, float z1, int color, IRenderType renderType) {
        return Line.newInstance(x0, y0, z0, x1, y1, z1, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, color, renderType);
    }

    public static ShapeElement cone(float x0, float y0, float z0, float x1, float y1, float z1, float radius, int color) {
        return cone(x0, y0, z0, x1, y1, z1, radius, color, SkinRenderType.BLIT_COLOR);
    }

    public static ShapeElement cone(float x0, float y0, float z0, float x1, float y1, float z1, float radius, int color, IRenderType renderType) {
        return Cone.newInstance(x0, y0, z0, x1, y1, z1, radius, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, color, renderType);
    }


    public static ShapeElement marker(IRectangle3f rect, int lightmap, int overlay, BlockPaintColor color, float alpha) {
        return marker(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), lightmap, overlay, color, alpha);
    }

    public static ShapeElement marker(float x, float y, float z, float w, float h, float d, int lightmap, int overlay, BlockPaintColor color, float alpha) {
        var cube = Cube.newInstance(x, y, z, x + w, y + h, z + d, lightmap, overlay, SkinRenderType.IMAGE_MARKER);
        for (var dir : OpenDirection.values()) {
            var type = color.getOrDefault(dir, SkinPaintColor.CLEAR).paintType();
            if (type == SkinPaintTypes.NONE || type == SkinPaintTypes.NORMAL) {
                cube.setColor(dir, 0);
                continue; // ignore
            }
            var n = 1f / 8f;
            var m = 1f / 8f;
            var u = type.ordinal() % 8;
            var v = type.ordinal() / 8;
            cube.setColor(dir, Colors.toARGB(1.0f, 1.0f, 1.0f, alpha));
            cube.setTextureOffset(dir, u * n, v * m);
            cube.setTextureSize(dir, n, m);
        }
        return cube;
    }

    public static ShapeElement guide(IRectangle3i rect, int color) {
        return guide(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), color);
    }

    public static ShapeElement guide(IRectangle3f rect, int color) {
        return guide(rect.x(), rect.y(), rect.z(), rect.width(), rect.height(), rect.depth(), color);
    }

    public static ShapeElement guide(float x, float y, float z, float w, float h, float d, int color) {
        return guide(x, y, z, w, h, d, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, color, SkinRenderType.IMAGE_GUIDE);
    }

    public static ShapeElement guide(float x, float y, float z, float w, float h, float d, int lightmap, int overlay, int color, IRenderType renderType) {
        var cube = Cube.newInstance(x, y, z, x + w, y + h, z + d, lightmap, overlay, renderType);
        cube.setTextureOffset(0, 0, 0);
        cube.setTextureSize(w, h, d); // the texture will use tile mode.
        cube.setColor(color);
        return cube;
    }

    public static ShapeElement stroke(IVoxelShape shape, int color) {
        return stroke(shape.bounds(), color);
    }

    public static ShapeElement stroke(AABB boundingBox, int color) {
        var minX = boundingBox.minX;
        var minY = boundingBox.minY;
        var minZ = boundingBox.minZ;
        var maxX = boundingBox.maxX;
        var maxY = boundingBox.maxY;
        var maxZ = boundingBox.maxZ;
        return stroke(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ, color);
    }

    public static ShapeElement stroke(BakedArmature armature, boolean showOriginPoint) {
        return Armature.newInstance(armature, showOriginPoint, ModDebugger.defaultArmature);
    }

    public static ShapeElement fill(JointShape shape, SkinTextureData texture, int lightmap, int overlay, int color, IRenderType renderType) {
        var rect = shape.bounds();
        var cube = Cube.newInstance(rect.minX(), rect.minY(), rect.minZ(), rect.maxX(), rect.maxY(), rect.maxZ(), lightmap, overlay, renderType);
        for (var dir : OpenDirection.values()) {
            var uv = shape.getUV(dir);
            if (uv == null) {
                cube.setColor(dir, 0);
                continue;
            }
            var u = uv.x();
            var v = uv.y();
            var s = uv.width();
            var t = uv.height();
            var n = 1f / texture.width();
            var m = 1f / texture.height();
            cube.setColor(dir, color);
            cube.setTextureOffset(dir, u * n, v * m);
            cube.setTextureSize(dir, s * n, t * m);
        }
        return cube;
    }

    protected static abstract class Base extends ShapeElement implements IGraphicsRenderable {

        protected float u = 0.0f;
        protected float v = 0.0f;

        protected int color = -1;

        protected int lightmap = LightmapTexture.DEFAULT;
        protected int overlay = OverlayTexture.NO_OVERLAY;

        protected OpenVector3f normal = OpenVector3f.ZERO;

        protected IRenderType renderType;

        @Override
        public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
            var normal = new float[3];
            var position = new float[4];

            var u1 = OverlayTexture.getU(overlay);
            var v1 = OverlayTexture.getV(overlay);

            var u2 = LightmapTexture.getU(lightmap);
            var v2 = LightmapTexture.getV(lightmap);

            var r = Colors.getRed(color);
            var g = Colors.getGreen(color);
            var b = Colors.getBlue(color);
            var a = Colors.getAlpha(color);

            // apply pose transform of the normal.
            normal[0] = this.normal.x;
            normal[1] = this.normal.y;
            normal[2] = this.normal.z;
            pose.transformNormal(normal);

            for (var vertex : vertices()) {
                // apply pose transform of the pos.
                position[0] = vertex.x;
                position[1] = vertex.y;
                position[2] = vertex.z;
                position[3] = 1.0f;
                pose.transformPose(position);

                // submit into builder.
                builder.vertex(position[0], position[1], position[2])
                        .color(r, g, b, a)
                        .uv(u, v)
                        .overlayCoords(u1, v1)
                        .uv2(u2, v2)
                        .normal(normal[0], normal[1], normal[2])
                        .endVertex();
            }
        }

        @Override
        public IRenderType renderType() {
            return renderType;
        }

        protected abstract OpenVector3f[] vertices();
    }

    /**
     * A cube shape (RHS, CCW) of the render element.
     * <pre>
     *                  +y
     *                  |
     *               p3 +-------------------+ p2
     *                / |                 / |
     *              /   |     T         /   |
     *            /     |             /     |
     *       p7 +-------------------+ p6    |
     *          |       |           |       |
     *          |       |           |   L   |
     *          |    p0 +-----------|-------+ p1 -> +x
     *          |     /   B         |     /
     *          |   /               |   /
     *          | /                 | /
     *       p4 +-------------------+ p5
     *        /
     *      +z
     * </pre>
     * <a href="https://web..org/web/20250920105830/https://learnopengl.com/Getting-started/Coordinate-Systems">Coordinate-Systems</a>
     **/
    @SuppressWarnings("SuspiciousNameCombination")
    protected static class Cube extends ShapeElement implements IGraphicsRenderable {

        private static final ObjectPool<Cube> POOL = ObjectPool.create(Cube::new);

        private int overlay;
        private int lightmap;

        private IRenderType renderType;

        private final OpenVector3f p0 = new OpenVector3f(); // minX, minY, minZ
        private final OpenVector3f p1 = new OpenVector3f(); // maxX, minY, minZ
        private final OpenVector3f p2 = new OpenVector3f(); // maxX, maxY, minZ
        private final OpenVector3f p3 = new OpenVector3f(); // minX, maxY, minZ
        private final OpenVector3f p4 = new OpenVector3f(); // minX, minY, maxZ
        private final OpenVector3f p5 = new OpenVector3f(); // maxX, minY, maxZ
        private final OpenVector3f p6 = new OpenVector3f(); // maxX, maxY, maxZ
        private final OpenVector3f p7 = new OpenVector3f(); // minX, maxY, maxZ

        private final Polygon front = new Polygon(p2, p1, p0, p3, OpenDirection.NORTH); // -z, front, north
        private final Polygon back = new Polygon(p7, p4, p5, p6, OpenDirection.SOUTH);  // +z, back, south
        private final Polygon left = new Polygon(p6, p5, p1, p2, OpenDirection.WEST);   // -x, left, west
        private final Polygon right = new Polygon(p3, p0, p4, p7, OpenDirection.EAST);  // +x, right, east
        private final Polygon top = new Polygon(p3, p7, p6, p2, OpenDirection.UP); // -y, top, up
        private final Polygon bottom = new Polygon(p1, p5, p4, p0, OpenDirection.DOWN); // +y, bottom, down

        private final Polygon[] polygons = {top, bottom, front, back, left, right}; // down, up, north, south, west, east

        public static Cube newInstance(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int lightmap, int overlay, IRenderType renderType) {
            var that = POOL.alloc();
            that.p0.set(minX, minY, minZ);
            that.p1.set(maxX, minY, minZ);
            that.p2.set(maxX, maxY, minZ);
            that.p3.set(minX, maxY, minZ);
            that.p4.set(minX, minY, maxZ);
            that.p5.set(maxX, minY, maxZ);
            that.p6.set(maxX, maxY, maxZ);
            that.p7.set(minX, maxY, maxZ);
            that.lightmap = lightmap;
            that.overlay = overlay;
            that.renderType = renderType;
            return that;
        }

        @Override
        public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
            var mode = renderType.mode();
            var normal = new float[3];
            var position = new float[4];

            var u1 = OverlayTexture.getU(overlay);
            var v1 = OverlayTexture.getV(overlay);

            var u2 = LightmapTexture.getU(lightmap);
            var v2 = LightmapTexture.getV(lightmap);

            for (var polygon : polygons) {
                // compute the texture pos and color channel
                var u = polygon.textureX;
                var v = polygon.textureY;
                var s = polygon.textureWidth;
                var t = polygon.textureHeight;
                var r = Colors.getRed(polygon.color);
                var g = Colors.getGreen(polygon.color);
                var b = Colors.getBlue(polygon.color);
                var a = Colors.getAlpha(polygon.color);

                // skip the transparent polygon.
                if (a == 0) {
                    continue;
                }

                // apply pose transform of the normal.
                normal[0] = polygon.normalX;
                normal[1] = polygon.normalY;
                normal[2] = polygon.normalZ;
                pose.transformNormal(normal);

                for (var vertex : polygon.vertices(mode)) {
                    var u0 = u + s * vertex.uScale;
                    var v0 = v + t * vertex.vScale;

                    // apply pose transform of the pos.
                    position[0] = vertex.pos.x;
                    position[1] = vertex.pos.y;
                    position[2] = vertex.pos.z;
                    position[3] = 1.0f;
                    pose.transformPose(position);

                    // submit into builder.
                    builder.vertex(position[0], position[1], position[2])
                            .color(r, g, b, a)
                            .uv(u0, v0)
                            .overlayCoords(u1, v1)
                            .uv2(u2, v2)
                            .normal(normal[0], normal[1], normal[2])
                            .endVertex();
                }
            }
        }

        public void setColor(int color) {
            front.setColor(color);
            back.setColor(color);
            left.setColor(color);
            right.setColor(color);
            top.setColor(color);
            bottom.setColor(color);
        }

        public void setColor(int color0, int color1, int color2, int color3, int color4, int color5) {
            front.setColor(color2);
            back.setColor(color3);
            left.setColor(color4);
            right.setColor(color5);
            top.setColor(color1);
            bottom.setColor(color0);
        }

        public void setTextureOffset(float x, float y, float z) {
            front.setTextureOffset(x, y);
            back.setTextureOffset(x, y);
            left.setTextureOffset(y, z);
            right.setTextureOffset(y, z);
            top.setTextureOffset(x, z);
            bottom.setTextureOffset(x, z);
        }

        public void setTextureSize(float width, float height, float depth) {
            front.setTextureSize(width, height);
            back.setTextureSize(width, height);
            left.setTextureSize(height, depth);
            right.setTextureSize(height, depth);
            top.setTextureSize(width, depth);
            bottom.setTextureSize(width, depth);
        }

        public void setColor(OpenDirection dir, int color) {
            polygons[dir.get3DDataValue()].setColor(color);
        }

        public void setTextureOffset(OpenDirection dir, float x, float y) {
            polygons[dir.get3DDataValue()].setTextureOffset(x, y);
        }

        public void setTextureSize(OpenDirection dir, float width, float height) {
            polygons[dir.get3DDataValue()].setTextureSize(width, height);
        }

        @Override
        public IRenderType renderType() {
            return renderType;
        }

        protected static class Polygon {

            private int color = -1;

            private float textureX;
            private float textureY;
            private float textureWidth;
            private float textureHeight;

            private final float normalX;
            private final float normalY;
            private final float normalZ;

            private final Vertex[] vertices = new Vertex[4];

            public Polygon(OpenVector3f lt, OpenVector3f lb, OpenVector3f rb, OpenVector3f rt, OpenDirection direction) {
                this.normalX = direction.stepX();
                this.normalY = direction.stepY();
                this.normalZ = direction.stepZ();
                this.vertices[0] = new Vertex(lt, 1, 1);
                this.vertices[1] = new Vertex(lb, 1, 0);
                this.vertices[2] = new Vertex(rb, 0, 0);
                this.vertices[3] = new Vertex(rt, 0, 1);
                // apply the mirror x-axis when up side.
                if (direction == OpenDirection.UP) {
                    Collections.compactMap(vertices, it -> it.uScale = 1 - it.uScale);
                }
            }

            public Iterable<Vertex> vertices(IVertexFormat.Mode mode) {
                return new Adapter(vertices, 0, mode);
            }

            public void setColor(int color) {
                this.color = color;
            }

            public void setTextureOffset(float x, float y) {
                this.textureX = x;
                this.textureY = y;
            }

            public void setTextureSize(float width, float height) {
                this.textureWidth = width;
                this.textureHeight = height;
            }
        }

        protected static class Vertex {

            private float uScale;
            private float vScale;

            private final OpenVector3f pos;

            public Vertex(OpenVector3f pos, float uScale, float vScale) {
                this.pos = pos;
                this.uScale = uScale;
                this.vScale = vScale;
            }
        }

        protected static class Adapter implements Iterable<Vertex>, Iterator<Vertex> {

            private static final int[] QUAD_TO_QUAD = {0, 1, 2, 3}; // lt,lb,rb,rt
            private static final int[] QUAD_TO_TRIANGLE = {0, 1, 2, 2, 3, 0}; // lt,lb,rb,rb,rt,lt
            private static final int[] QUAD_TO_LINE = {0, 1, 1, 2, 2, 3, 3, 0}; // lt,lb,lb,rb,rb,rt,rt,lt

            private int index;
            private final int[] indexes;
            private final Vertex[] vertices;

            protected Adapter(Vertex[] vertices, int index, IVertexFormat.Mode mode) {
                this.index = index;
                this.vertices = vertices;
                this.indexes = switch (mode) {
                    case DEBUG_LINES, DEBUG_LINE_STRIP -> QUAD_TO_LINE;
                    case QUADS, LINES -> QUAD_TO_QUAD;
                    default -> QUAD_TO_TRIANGLE;
                };
            }

            @Override
            public Vertex next() {
                return vertices[indexes[index++]];
            }

            @Override
            public boolean hasNext() {
                return index < indexes.length;
            }

            @NotNull
            @Override
            public Iterator<Vertex> iterator() {
                return this;
            }
        }
    }

    /**
     * A cone shape (RHS, CCW) of the render element.
     * <pre>
     *                  (apex)
     *                   //\\
     *                 / /  \ \
     *               /  /    \  \
     *             /   /      \   \
     *           /    /        \    \
     *       p1 +----/----------\----+ p2
     *          |   /            \   |
     *          |  /    center    \  |
     *          | /                \ |
     *          |/                  \|
     *       p0 +--------------------+ p3
     * </pre>
     **/
    protected static class Cone extends Base {

        private static final ObjectPool<Cone> POOL = ObjectPool.create(Cone::new);

        private float radius;

        private final OpenVector3f center = new OpenVector3f(); // center
        private final OpenVector3f p0 = new OpenVector3f(); // left top
        private final OpenVector3f p1 = new OpenVector3f(); // left bottom
        private final OpenVector3f p2 = new OpenVector3f(); // right bottom
        private final OpenVector3f p3 = new OpenVector3f(); // right top
        private final OpenVector3f apex = new OpenVector3f(); // apex

        private final OpenVector3f normal = new OpenVector3f();
        private final OpenVector3f[] vertices = new OpenVector3f[]{
                // sides
                apex, p0, p1,
                apex, p1, p2,
                apex, p2, p3,
                apex, p3, p0,
                // bottom
                p0, p1, p2,
                p2, p3, p0,
        };

        public static Cone newInstance(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float radius, int lightmap, int overlay, int color, IRenderType renderType) {
            var that = POOL.alloc();
            that.color = color;
            that.lightmap = lightmap;
            that.overlay = overlay;
            that.renderType = renderType;
            that.radius = radius;
            that.resize(minX, minY, minZ, maxX, maxY, maxZ);
            return that;
        }

        protected void resize(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            // normal = normalized(p5 - p0)
            normal.set(maxX - minX, maxY - minY, maxZ - minZ);
            normal.normalize();

            // tmp = (0,1,0) or (1,0,0)
            var tmp = new OpenVector3f(0, 1, 0);
            if (Math.abs(normal.dot(tmp)) > 0.999f) {
                tmp.set(1, 0, 0);
            }

            // right = normalized(tmp x normal)
            // up = normalized(normal x right)
            var right = tmp.crossing(normal).normalizing();
            var up = normal.crossing(right).normalizing();
            right.scale(radius);
            up.scale(radius);

            //          p_u (3)
            //            ↑
            //   p_nr ← center → p_r
            //   (2)     (0)      (1)
            //            ↓
            //         p_nu (4)
            center.set(minX, minY, minZ);
            p0.set(minX - right.x + up.x, minY - right.y + up.y, minZ - right.z + up.z); // left top = center - right + up
            p1.set(minX - right.x - up.x, minY - right.y - up.y, minZ - right.z - up.z); // left bottom = center - right - up
            p2.set(minX + right.x - up.x, minY + right.y - up.y, minZ + right.z - up.z); // right bottom = center + right - up
            p3.set(minX + right.x + up.x, minY + right.y + up.y, minZ + right.z + up.z); // right top = center + right + up
            apex.set(maxX, maxY, maxZ);
        }

        @Override
        protected OpenVector3f[] vertices() {
            return vertices;
        }
    }

    protected static class Line extends Base {

        private static final ObjectPool<Line> POOL = ObjectPool.create(Line::new);

        private final OpenVector3f p0 = new OpenVector3f(); // start
        private final OpenVector3f p1 = new OpenVector3f(); // end

        private final OpenVector3f[] vertices = new OpenVector3f[]{
                p0, p1
        };

        public static Line newInstance(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, int lightmap, int overlay, int color, IRenderType renderType) {
            var that = POOL.alloc();
            that.color = color;
            that.lightmap = lightmap;
            that.overlay = overlay;
            that.renderType = renderType;
            that.p0.set(minX, minY, minZ);
            that.p1.set(maxX, maxY, maxZ);
            return that;
        }

        @Override
        protected OpenVector3f[] vertices() {
            return vertices;
        }
    }

    protected static class Arrow extends ShapeElement {

        private static final ObjectPool<Arrow> POOL = ObjectPool.create(Arrow::new);

        private float x;
        private float y;
        private float z;
        private float width;
        private float height;
        private float depth;

        public static Arrow newInstance(float x, float y, float z, float width, float height, float depth) {
            var that = POOL.alloc();
            that.x = x;
            that.y = y;
            that.z = z;
            that.width = width;
            that.height = height;
            that.depth = depth;
            return that;
        }

        @Override
        public void prepare(IGraphicsContext context) {
            var minX = x - width * 0.5f;
            var minY = y - height * 0.5f;
            var minZ = z - depth * 0.5f;
            var midX = x + 0;
            var midY = y + 0;
            var midZ = z + 0;
            var maxX = x + width * 0.5f;
            var maxY = y + height * 0.5f;
            var maxZ = z + depth * 0.5f;

            var n = width * 0.03f;
            var m = height * 0.10f;

            context.draw(ShapeElement.line(minX, midY, midZ, maxX, midY, midZ, Colors.RED)); // x-axis
            context.draw(ShapeElement.line(midX, minY, midZ, midX, maxY, midZ, Colors.GREEN)); // y-axis
            context.draw(ShapeElement.line(midX, midY, minZ, midX, midY, maxZ, Colors.BLUE)); // z-axis

            context.draw(ShapeElement.cone(maxX - m, midY, midZ, maxX, midY, midZ, n, Colors.RED)); // x-arrow
            context.draw(ShapeElement.cone(midX, maxY - m, midZ, midX, maxY, midZ, n, Colors.GREEN)); // y-arrow
            context.draw(ShapeElement.cone(midX, midY, maxZ - m, midX, midY, maxZ, n, Colors.BLUE)); // z-arrow
        }
    }

    protected static class Armature extends ShapeElement {

        private static final ObjectPool<Armature> POOL = ObjectPool.create(Armature::new);

        private boolean showOriginPoint;
        private boolean showDefaultArmature;

        private BakedArmature armature;

        public static Armature newInstance(BakedArmature armature, boolean showOriginPoint, boolean showDefaultArmature) {
            var that = POOL.alloc();
            that.armature = armature;
            that.showOriginPoint = showOriginPoint;
            that.showDefaultArmature = showDefaultArmature;
            return that;
        }

        @Override
        public void prepare(IGraphicsContext context) {
            var jointTransforms = armature.transforms();
            var armature1 = armature.armature();
            for (var joint : armature1.allJoints()) {
                var shape = armature1.shapeById(joint.id());
                var jointTransform = jointTransforms[joint.id()];
                if (showDefaultArmature) {
                    jointTransform = armature1.globalTransformById(joint.id());
                }
                if (shape != null && jointTransform != null) {
                    context.saveGraphicsState();
                    jointTransform.apply(context.ctm());
                    if (showOriginPoint) {
                        context.draw(ShapeElement.arrow(0, 0, 0, 4, 4, 4));
                    }
                    submit(shape, Colors.getPaletteColor(joint.id()), context);
                    context.restoreGraphicsState();
                }
            }
        }

        protected void submit(JointShape shape, int color, IGraphicsContext context) {
            var rect = shape.bounds();
            context.saveGraphicsState();
            shape.transform().apply(context.ctm());
            context.draw(ShapeElement.stroke(rect, color));
            context.translateCTM(rect.x(), rect.y(), rect.z());
            for (var shape1 : shape.children()) {
                submit(shape1, color, context);
            }
            context.restoreGraphicsState();
        }
    }
}
