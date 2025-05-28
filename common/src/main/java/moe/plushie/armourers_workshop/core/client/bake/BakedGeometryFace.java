package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.ITransform3f;
import moe.plushie.armourers_workshop.api.skin.geometry.ISkinGeometryType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.client.texture.SmartTextureManager;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryFace;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryVertex;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeVertex;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
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
        this.priority = geometryFace.getPriority();
        this.transform = geometryFace.getTransform();
        this.renderType = resolveRenderType(geometryFace);
        this.renderTypeVariants = resolveRenderTypeVariants(geometryFace);
        this.vertices = triangulation(geometryFace.getVertices(), geometryFace.getType());
        this.defaultVertex = resolveDefaultVertex(vertices);
        this.defaultTexturePos = geometryFace.getTexturePos();
    }

    public void render(BakedSkinPart part, SkinPaintScheme scheme, int lightmap, int overlay, IPoseStack poseStack, IVertexConsumer builder) {
        // when not any vertices found, we will ignore the rendering of this face.
        if (defaultVertex == null) {
            return;
        }

        // we need to blend the vertex color.
        // NOTE: we assume that all vertices use the same color, but in fact every vertex needs to be blended.
        var vertexColor = defaultVertex.getColor();
        var resolvedColor = resolveColor(vertexColor, scheme, part.getColorInfo(), part.getType(), 0);
        if (resolvedColor.getPaintType() == SkinPaintTypes.NONE) {
            return;
        }

        if (transform != OpenTransform3f.IDENTITY) {
            poseStack.pushPose();
            transform.apply(poseStack);
        }

        var entry = poseStack.last();

        // for dye color, we need to relocation to final color by the offset(x, 0).
        var u = resolveTextureOffset(vertexColor.getPaintType(), resolvedColor.getPaintType());
        var v = 0.0f;

        var n = defaultTexturePos.getTotalWidth();
        var m = defaultTexturePos.getTotalHeight();

        var r = resolvedColor.getRed();
        var g = resolvedColor.getGreen();
        var b = resolvedColor.getBlue();
        var a = vertexColor.getAlpha();

        for (var vertex : vertices) {
            var position = vertex.getPosition();
            var normal = vertex.getNormal();
            var textureCoords = vertex.getTextureCoords();
            builder.vertex(entry, position.x(), position.y(), position.z())
                    .color(r, g, b, a)
                    .uv((u + textureCoords.x()) / n, (v + textureCoords.y()) / m)
                    .overlayCoords(overlay)
                    .uv2(lightmap)
                    .normal(entry, normal.x(), normal.y(), normal.z())
                    .endVertex();
        }

        if (transform != OpenTransform3f.IDENTITY) {
            poseStack.popPose();
        }
    }

    private SkinPaintColor dye(SkinPaintColor source, SkinPaintColor destination, SkinPaintColor average) {
        if (destination.getPaintType() == SkinPaintTypes.NONE) {
            return SkinPaintColor.CLEAR;
        }
        if (average == null) {
            return source;
        }
        int src = (source.getRed() + source.getGreen() + source.getBlue()) / 3;
        int avg = (average.getRed() + average.getGreen() + average.getBlue()) / 3;
        int r = OpenMath.clamp(destination.getRed() + src - avg, 0, 255);
        int g = OpenMath.clamp(destination.getGreen() + src - avg, 0, 255);
        int b = OpenMath.clamp(destination.getBlue() + src - avg, 0, 255);

        return destination.withColor(r, g, b);
    }


    private SkinPaintColor resolveTextureColor(OpenResourceLocation texture, SkinPartType partType) {
        var bakedTexture = PlayerTextureLoader.getInstance().getTextureModel(texture);
        if (bakedTexture != null && defaultVertex instanceof SkinCubeVertex cubeVertex) {
            var shape = cubeVertex.getBoundingBox();
            var direction = cubeVertex.getDirection();
            int x = (int) shape.x();
            int y = (int) shape.y();
            int z = (int) shape.z();
            return bakedTexture.getColor(x, y, z, direction, partType);
        }
        return null;
    }

    private float resolveTextureOffset(SkinPaintType from, SkinPaintType to) {
        var fromTexturePos = from.getTexturePos();
        var toTexturePos = to.getTexturePos();
        if (fromTexturePos != toTexturePos) {
            return toTexturePos.getU() - fromTexturePos.getU();
        }
        return 0;
    }

    private SkinPaintColor resolveColor(SkinPaintColor paintColor, SkinPaintScheme scheme, ColorDescriptor descriptor, SkinPartType partType, int deep) {
        var paintType = paintColor.getPaintType();
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
            var paintColor1 = resolveTextureColor(scheme.getTexture(), partType);
            if (paintColor1 != null) {
                return paintColor1;
            }
            return paintColor;
        }
        if (paintType.getDyeType() != null && deep < 2) {
            var paintColor1 = scheme.getResolvedColor(paintType);
            if (paintColor1 == null) {
                return paintColor;
            }
            paintColor = dye(paintColor, paintColor1, descriptor.getAverageColor(paintType));
            return resolveColor(paintColor, scheme, descriptor, partType, deep + 1);
        }
        return paintColor;
    }

    private List<? extends SkinGeometryVertex> triangulation(Iterable<? extends SkinGeometryVertex> verticesIn, ISkinGeometryType geometryType) {
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
        var texturePos = face.getTexturePos();
        if (texturePos != null && texturePos.getProvider() != null) {
            return SmartTextureManager.getInstance().register(texturePos.getProvider(), face.getType());
        }
        return SkinRenderType.by(face.getType());
    }

    private Collection<IRenderType> resolveRenderTypeVariants(SkinGeometryFace face) {
        var texture = face.getTexturePos();
        if (texture == null || texture.getProvider() == null) {
            return null;
        }
        var parent = texture.getProvider();
        var renderTypes = new ArrayList<IRenderType>();
        for (var variant : parent.getVariants()) {
            var properties = variant.getProperties();
            if (properties.isNormal() || properties.isSpecular()) {
                continue; // normal/specular map, only use from shader mod.
            }
            renderTypes.add(SmartTextureManager.getInstance().register(variant, face.getType()));
        }
        return renderTypes;
    }

    public float getPriority() {
        return priority;
    }

    public IRenderType getRenderType() {
        return renderType;
    }

    public Collection<IRenderType> getRenderTypeVariants() {
        return renderTypeVariants;
    }

    public SkinPaintColor getDefaultColor() {
        if (defaultVertex != null) {
            return defaultVertex.getColor();
        }
        return null;
    }
}
