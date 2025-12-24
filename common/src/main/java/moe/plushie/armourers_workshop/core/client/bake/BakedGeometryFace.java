package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinBakery;
import moe.plushie.armourers_workshop.core.client.texture.SmartTextureManager;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeVertex;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BakedGeometryFace {

    private static final SkinPaintColor RAINBOW_TARGET = SkinPaintColor.of(0xff7f7f7f, SkinPaintTypes.RAINBOW);

    private final IRenderType renderType;
    private final Collection<IRenderType> renderTypeVariants;

    private final float priority;
    private final ITransform3f transform;

    private final List<? extends SkinGeometryVertex> vertices;

    private final SkinGeometryVertex defaultVertex;
    private final SkinTexturePos defaultTexturePos;

    public BakedGeometryFace(SkinGeometryFace geometryFace) {
        this.priority = geometryFace.priority();
        this.transform = geometryFace.transform();
        this.renderType = resolveRenderType(geometryFace);
        this.renderTypeVariants = resolveRenderTypeVariants(geometryFace);
        this.vertices = triangulation(geometryFace.vertices(), geometryFace.type());
        this.defaultVertex = resolveDefaultVertex(vertices);
        this.defaultTexturePos = geometryFace.texturePos();
    }

    public void render(BakedSkinPart part, SkinPaintScheme scheme, int lightmap, int overlay, IPoseStack poseStack, IVertexConsumer builder) {
        // when not any vertices found, we will ignore the rendering of this face.
        if (defaultVertex == null) {
            return;
        }

        // we need to blend the vertex color.
        // NOTE: we assume that all vertices use the same color, but in fact every vertex needs to be blended.
        var vertexColor = defaultVertex.color();
        var resolvedColor = resolveColor(vertexColor, scheme, part.colorInfo(), part.type(), 0);
        if (resolvedColor.paintType() == SkinPaintTypes.NONE) {
            return;
        }

        if (transform != OpenTransform3f.IDENTITY) {
            poseStack.pushPose();
            transform.apply(poseStack);
        }

        var pose = poseStack.last();

        // for dye color, we need to relocation to final color by the offset(x, 0).
        var u = resolveTextureOffset(vertexColor.paintType(), resolvedColor.paintType());
        var v = 0.0f;

        var n = defaultTexturePos.totalWidth();
        var m = defaultTexturePos.totalHeight();

        var r = resolvedColor.red();
        var g = resolvedColor.green();
        var b = resolvedColor.blue();
        var a = vertexColor.alpha();

        for (var vertex : vertices) {
            var position = vertex.position();
            var normal = vertex.normal();
            var textureCoords = vertex.textureCoords();
            builder.vertex(pose, position.x(), position.y(), position.z())
                    .color(r, g, b, a)
                    .uv((u + textureCoords.x()) / n, (v + textureCoords.y()) / m)
                    .overlayCoords(overlay)
                    .uv2(lightmap)
                    .normal(pose, normal.x(), normal.y(), normal.z())
                    .endVertex();
        }

        if (transform != OpenTransform3f.IDENTITY) {
            poseStack.popPose();
        }
    }

    private SkinPaintColor dye(SkinPaintColor source, SkinPaintColor destination, SkinPaintColor average) {
        if (destination.paintType() == SkinPaintTypes.NONE) {
            return SkinPaintColor.CLEAR;
        }
        if (average == null) {
            return source;
        }
        int src = (source.red() + source.green() + source.blue()) / 3;
        int avg = (average.red() + average.green() + average.blue()) / 3;
        int r = OpenMath.clamp(destination.red() + src - avg, 0, 255);
        int g = OpenMath.clamp(destination.green() + src - avg, 0, 255);
        int b = OpenMath.clamp(destination.blue() + src - avg, 0, 255);

        return destination.withColor(r, g, b);
    }


    private SkinPaintColor resolveTextureColor(PlayerSkin texture, SkinPartType partType) {
        var skin = PlayerSkinBakery.getInstance().loadSkin(texture);
        if (skin != null && defaultVertex instanceof SkinCubeVertex cubeVertex) {
            var shape = cubeVertex.boundingBox();
            var direction = cubeVertex.direction();
            var x = (int) shape.x();
            var y = (int) shape.y();
            var z = (int) shape.z();
            var texturePos = texture.model().get(x, y, z, direction, partType);
            if (texturePos != null) {
                return skin.body().getColor(texturePos);
            }
        }
        return null;
    }

    private float resolveTextureOffset(SkinPaintType from, SkinPaintType to) {
        var fromTexturePos = from.texturePos();
        var toTexturePos = to.texturePos();
        if (fromTexturePos != toTexturePos) {
            return toTexturePos.u() - fromTexturePos.u();
        }
        return 0;
    }

    private SkinPaintColor resolveColor(SkinPaintColor paintColor, SkinPaintScheme scheme, ColorDescriptor descriptor, SkinPartType partType, int deep) {
        var paintType = paintColor.paintType();
        if (paintType == SkinPaintTypes.NONE) {
            return SkinPaintColor.CLEAR;
        }
        if (paintType == SkinPaintTypes.NORMAL) {
            return paintColor;
        }
        if (paintType == SkinPaintTypes.RAINBOW) {
            return dye(paintColor, RAINBOW_TARGET, descriptor.getAverageColor(paintType));
        }
        if (paintType == SkinPaintTypes.TEXTURE) {
            var paintColor1 = resolveTextureColor(scheme.entityTexture(), partType);
            if (paintColor1 != null) {
                return paintColor1;
            }
            return paintColor;
        }
        if (paintType.dyeType() != null && deep < 2) {
            var paintColor1 = scheme.getResolvedColor(paintType);
            if (paintColor1 == null) {
                return paintColor;
            }
            paintColor = dye(paintColor, paintColor1, descriptor.getAverageColor(paintType));
            return resolveColor(paintColor, scheme, descriptor, partType, deep + 1);
        }
        return paintColor;
    }

    private List<? extends SkinGeometryVertex> triangulation(Iterable<? extends SkinGeometryVertex> verticesIn, SkinGeometryType geometryType) {
        var vertices = Collections.newList(verticesIn);
        if (geometryType != SkinGeometryTypes.MESH && geometryType != SkinGeometryTypes.MESH_CULL) {
            return vertices;
        }
        return switch (vertices.size()) {
            case 3 -> vertices;
            case 4 -> {
                var newVertices = new ArrayList<SkinGeometryVertex>(6);
                newVertices.add(vertices.get(0)); // lt
                newVertices.add(vertices.get(1)); // lb
                newVertices.add(vertices.get(2)); // rb
                newVertices.add(vertices.get(2)); // rb
                newVertices.add(vertices.get(3)); // rt
                newVertices.add(vertices.get(0)); // lt
                yield newVertices;
            }
            default -> throw new RuntimeException("Can't convert polygon (" + vertices.size() + ") to triangle!");
        };
    }

    private SkinGeometryVertex resolveDefaultVertex(List<? extends SkinGeometryVertex> vertices) {
        if (!vertices.isEmpty()) {
            return vertices.get(0);
        }
        return null;
    }

    private IRenderType resolveRenderType(SkinGeometryFace face) {
        var parent = Objects.flatMap(face.texturePos(), SkinTexturePos::provider);
        if (parent != null) {
            return resolveRenderType(parent, face.type());
        }
        return SkinRenderType.geometry(face.type());
    }

    private Collection<IRenderType> resolveRenderTypeVariants(SkinGeometryFace face) {
        var parent = Objects.flatMap(face.texturePos(), SkinTexturePos::provider);
        if (parent == null) {
            return null;
        }
        var renderTypes = new ArrayList<IRenderType>();
        for (var variant : parent.variants()) {
            var properties = variant.properties();
            if (properties.isNormal() || properties.isSpecular()) {
                continue; // normal/specular map, only use from shader mod.
            }
            renderTypes.add(resolveRenderType(variant, face.type()));
        }
        return renderTypes;
    }

    private IRenderType resolveRenderType(SkinTextureData provider, SkinGeometryType geometryType) {
        return SmartTextureManager.getInstance().register(provider).create(it -> {
            var location = it.location();
            return SkinRenderType.geometry(geometryType, location, it.isTranslucent(), it.isEmissive());
        });
    }

    public float priority() {
        return priority;
    }

    public ITransform3f transform() {
        return transform;
    }

    public List<? extends SkinGeometryVertex> vertices() {
        return vertices;
    }

    public IRenderType renderType() {
        return renderType;
    }

    public Collection<IRenderType> renderTypeVariants() {
        return renderTypeVariants;
    }

    public SkinPaintColor defaultColor() {
        if (defaultVertex != null) {
            return defaultVertex.color();
        }
        return null;
    }
}
