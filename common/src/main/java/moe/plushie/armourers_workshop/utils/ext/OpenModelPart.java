package moe.plushie.armourers_workshop.utils.ext;

import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class OpenModelPart {

    public float x;
    public float y;
    public float z;
    public float xRot;
    public float yRot;
    public float zRot;
    public float xScale = 1.0F;
    public float yScale = 1.0F;
    public float zScale = 1.0F;

    public boolean visible = true;
    public boolean skipDraw = false;

    private Pose initialPose;

    private final List<Cube> cubes;
    private final Map<String, OpenModelPart> children;

    public OpenModelPart(List<Cube> cubes, Map<String, OpenModelPart> children) {
        this.cubes = cubes;
        this.children = children;
        this.initialPose = Pose.ZERO;
    }

    public Pose getInitialPose() {
        return this.initialPose;
    }

    public void setInitialPose(Pose partPose) {
        this.initialPose = partPose;
    }

    public Pose storePose() {
        return Pose.offsetAndRotation(x, y, z, xRot, yRot, zRot);
    }

    public void resetPose() {
        loadPose(initialPose);
    }

    public void loadPose(Pose partPose) {
        this.x = partPose.x;
        this.y = partPose.y;
        this.z = partPose.z;
        this.xRot = partPose.xRot;
        this.yRot = partPose.yRot;
        this.zRot = partPose.zRot;
        this.xScale = 1.0f;
        this.yScale = 1.0f;
        this.zScale = 1.0f;
    }

    public void copyFrom(OpenModelPart modelPart) {
        this.xScale = modelPart.xScale;
        this.yScale = modelPart.yScale;
        this.zScale = modelPart.zScale;
        this.xRot = modelPart.xRot;
        this.yRot = modelPart.yRot;
        this.zRot = modelPart.zRot;
        this.x = modelPart.x;
        this.y = modelPart.y;
        this.z = modelPart.z;
    }

    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    public OpenModelPart getChild(String name) {
        OpenModelPart modelPart = children.get(name);
        if (modelPart != null) {
            return modelPart;
        }
        throw new NoSuchElementException("Can't find part " + name);
    }

    public void setPos(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public void setRotation(float f, float g, float h) {
        this.xRot = f;
        this.yRot = g;
        this.zRot = h;
    }

    public void render(IPoseStack poseStack, IVertexConsumer builder, int i, int j) {
        this.render(poseStack, builder, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void render(IPoseStack poseStack, IVertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        if (!visible) {
            return;
        }
        if (!cubes.isEmpty() || !children.isEmpty()) {
            poseStack.pushPose();
            translateAndRotate(poseStack);
            if (!skipDraw) {
                compile(poseStack.last(), vertexConsumer, i, j, f, g, h, k);
            }
            for (OpenModelPart modelPart : children.values()) {
                modelPart.render(poseStack, vertexConsumer, i, j, f, g, h, k);
            }
            poseStack.popPose();
        }
    }

    public void translateAndRotate(IPoseStack poseStack) {
        poseStack.translate(x / 16.0f, y / 16.0f, z / 16.0f);
        if (xRot != 0.0f || yRot != 0.0f || zRot != 0.0f) {
            poseStack.rotate(OpenQuaternionf.fromZYX(zRot, yRot, xRot));
        }
        if (xScale != 1.0F || yScale != 1.0F || zScale != 1.0F) {
            poseStack.scale(xScale, yScale, zScale);
        }
    }

    private void compile(IPoseStack.Pose pose, IVertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        for (Cube cube : cubes) {
            cube.compile(pose, vertexConsumer, i, j, f, g, h, k);
        }
    }

    public static class Pose {
        public static final Pose ZERO = offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        public final float x;
        public final float y;
        public final float z;
        public final float xRot;
        public final float yRot;
        public final float zRot;

        private Pose(float f, float g, float h, float i, float j, float k) {
            this.x = f;
            this.y = g;
            this.z = h;
            this.xRot = i;
            this.yRot = j;
            this.zRot = k;
        }

        public static Pose offset(float x, float y, float z) {
            return offsetAndRotation(x, y, z, 0.0f, 0.0f, 0.0f);
        }

        public static Pose rotation(float xRot, float yRot, float zRot) {
            return offsetAndRotation(0.0f, 0.0f, 0.0f, xRot, yRot, zRot);
        }

        public static Pose offsetAndRotation(float x, float y, float z, float xRot, float yRot, float zRot) {
            return new Pose(x, y, z, xRot, yRot, zRot);
        }
    }

    public static class Cube {
        private final Polygon[] polygons;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cube(int i, int j, float f, float g, float h, float width, float height, float depth, float growX, float growY, float growZ, boolean mirror, float texWidth, float texHeight, Set<Direction> visibleFaces) {
            this.minX = f;
            this.minY = g;
            this.minZ = h;
            this.maxX = f + width;
            this.maxY = g + height;
            this.maxZ = h + depth;
            this.polygons = new Polygon[visibleFaces.size()];
            float s = f + width;
            float t = g + height;
            float u = h + depth;
            f -= growX;
            g -= growY;
            h -= growZ;
            s += growX;
            t += growY;
            u += growZ;
            if (mirror) {
                float tmp = s;
                s = f;
                f = tmp;
            }

            Vertex vertex = new Vertex(f, g, h, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(s, g, h, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(s, t, h, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(f, t, h, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(f, g, u, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(s, g, u, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(s, t, u, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(f, t, u, 8.0f, 0.0f);
            float w = (float) i;
            float x = (float) i + depth;
            float y = (float) i + depth + width;
            float z = (float) i + depth + width + width;
            float aa = (float) i + depth + width + depth;
            float ab = (float) i + depth + width + depth + width;
            float ac = (float) j;
            float ad = (float) j + depth;
            float ae = (float) j + depth + height;
            int faceIndex = 0;
            if (visibleFaces.contains(Direction.DOWN)) {
                this.polygons[faceIndex++] = new Polygon(new Vertex[]{vertex6, vertex5, vertex, vertex2}, x, ac, y, ad, texWidth, texHeight, mirror, Direction.DOWN);
            }

            if (visibleFaces.contains(Direction.UP)) {
                this.polygons[faceIndex++] = new Polygon(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, y, ad, z, ac, texWidth, texHeight, mirror, Direction.UP);
            }

            if (visibleFaces.contains(Direction.WEST)) {
                this.polygons[faceIndex++] = new Polygon(new Vertex[]{vertex, vertex5, vertex8, vertex4}, w, ad, x, ae, texWidth, texHeight, mirror, Direction.WEST);
            }

            if (visibleFaces.contains(Direction.NORTH)) {
                this.polygons[faceIndex++] = new Polygon(new Vertex[]{vertex2, vertex, vertex4, vertex3}, x, ad, y, ae, texWidth, texHeight, mirror, Direction.NORTH);
            }

            if (visibleFaces.contains(Direction.EAST)) {
                this.polygons[faceIndex++] = new Polygon(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, y, ad, aa, ae, texWidth, texHeight, mirror, Direction.EAST);
            }

            if (visibleFaces.contains(Direction.SOUTH)) {
                this.polygons[faceIndex] = new Polygon(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, aa, ad, ab, ae, texWidth, texHeight, mirror, Direction.SOUTH);
            }

        }

        public void compile(IPoseStack.Pose entry, IVertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
            for (Polygon polygon : polygons) {
                float[] normal = {
                        polygon.normal.getX(),
                        polygon.normal.getY(),
                        polygon.normal.getZ()
                };
                entry.transformNormal(normal);
                for (Vertex vertex : polygon.vertices) {
                    float[] pose = {
                            vertex.pos.getX() / 16.0f,
                            vertex.pos.getY() / 16.0f,
                            vertex.pos.getZ() / 16.0f,
                            1.0f
                    };
                    entry.transformPose(pose);
                    vertexConsumer.vertex(pose[0], pose[1], pose[2], f, g, h, k, vertex.u, vertex.v, j, i, normal[0], normal[1], normal[2]);
                }
            }
        }
    }

    public static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float f, float g, float h, float i, float j) {
            this(new Vector3f(f, g, h), i, j);
        }

        public Vertex remap(float f, float g) {
            return new Vertex(this.pos, f, g);
        }

        public Vertex(Vector3f pos, float f, float g) {
            this.pos = pos;
            this.u = f;
            this.v = g;
        }
    }

    public static class Polygon {
        public final Vertex[] vertices;
        public final Vector3f normal;

        public Polygon(Vertex[] vertexes, float f, float g, float h, float i, float j, float k, boolean mirror, Direction direction) {
            this.vertices = vertexes;
            float l = 0.0F / j;
            float m = 0.0F / k;
            vertexes[0] = vertexes[0].remap(h / j - l, g / k + m);
            vertexes[1] = vertexes[1].remap(f / j + l, g / k + m);
            vertexes[2] = vertexes[2].remap(f / j + l, i / k - m);
            vertexes[3] = vertexes[3].remap(h / j - l, i / k - m);
            if (mirror) {
                int n = vertexes.length;
                for (int o = 0; o < n / 2; ++o) {
                    Vertex vertex = vertexes[o];
                    vertexes[o] = vertexes[n - 1 - o];
                    vertexes[n - 1 - o] = vertex;
                }
            }
            this.normal = new Vector3f(direction.getStepX(), direction.getStepY(), direction.getStepZ());
            if (mirror) {
                this.normal.scale(-1.0f, 1.0f, 1.0f);
            }
        }
    }
}
