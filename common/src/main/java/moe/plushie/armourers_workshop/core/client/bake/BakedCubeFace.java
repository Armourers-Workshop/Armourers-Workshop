package moe.plushie.armourers_workshop.core.client.bake;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinTextureManager;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.face.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.math.OpenRay;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class BakedCubeFace {

    private static final PaintColor RAINBOW_TARGET = PaintColor.of(0xff7f7f7f, SkinPaintTypes.RAINBOW);

    private final IPaintColor paintColor;
    private final int alpha;

    private final Direction direction;
    private final RenderType renderType;
    private final Collection<RenderType> renderTypeVariants;

    private final IRectangle3f shape;
    private final ITransformf transform;
    private final ITextureKey texture;

    public BakedCubeFace(SkinCubeFace face) {
        this.renderType = resolveRenderType(face);
        this.renderTypeVariants = resolveRenderTypeVariants(face);
        this.direction = face.getDirection();
        this.shape = face.getShape();
        this.transform = face.getTransform();
        this.paintColor = face.getColor();
        this.alpha = face.getAlpha();
        this.texture = face.getTexture();
    }

    public void render(BakedSkinPart part, ColorScheme scheme, int lightmap, int overlay, PoseStack poseStack, VertexConsumer builder) {
        auto resolvedColor = resolveColor(paintColor, scheme, part.getColorInfo(), part.getType(), 0);
        if (resolvedColor.getPaintType() == SkinPaintTypes.NONE) {
            return;
        }
        auto resolvedTexture = resolveTexture(resolvedColor);
        if (resolvedTexture == null) {
            return;
        }

        if (transform != SkinTransform.IDENTITY) {
            poseStack.pushPose();
            resolveTransform(poseStack);
        }

        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();

        // https://learnopengl.com/Getting-started/Coordinate-Systems
        float x = shape.getX();
        float y = shape.getY();
        float z = shape.getZ();
        float w = roundUp(shape.getWidth());
        float h = roundUp(shape.getHeight());
        float d = roundUp(shape.getDepth());

        float u = resolvedTexture.getU();
        float v = resolvedTexture.getV();
        float s = roundDown(resolvedTexture.getWidth());
        float t = roundDown(resolvedTexture.getHeight());
        float n = resolvedTexture.getTotalWidth();
        float m = resolvedTexture.getTotalHeight();

        int r = resolvedColor.getRed();
        int g = resolvedColor.getGreen();
        int b = resolvedColor.getBlue();
        int a = alpha & 0xff;

        // mixin overlay color.
        if (overlay != 0) {
            float i = (overlay >> 24 & 0xff) / 255f;
            r = ColorUtils.mix(r, overlay >> 16 & 0xff, i);
            g = ColorUtils.mix(g, overlay >> 8 & 0xff, i);
            b = ColorUtils.mix(b, overlay & 0xff, i);
        }

        float[][] uvs = SkinUtils.getRenderUVs(direction);
        float[][] vertexes = SkinUtils.getRenderVertexes(direction);
        for (int i = 0; i < 4; ++i) {
            builder.vertex(pose, x + w * vertexes[i][0], y + h * vertexes[i][1], z + d * vertexes[i][2])
                    .color(r, g, b, a)
                    .uv((u + s * uvs[i][0]) / n, (v + t * uvs[i][1]) / m)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(lightmap)
                    .normal(normal, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }

        if (transform != SkinTransform.IDENTITY) {
            poseStack.popPose();
        }
    }

    private IPaintColor resolveColor(IPaintColor paintColor, ColorScheme scheme, ColorDescriptor descriptor, ISkinPartType partType, int deep) {
        ISkinPaintType paintType = paintColor.getPaintType();
        if (paintType == SkinPaintTypes.NONE) {
            return PaintColor.CLEAR;
        }
        if (paintType == SkinPaintTypes.RAINBOW) {
            return dye(paintColor, RAINBOW_TARGET, descriptor.getAverageColor(paintType));
        }
        if (paintType == SkinPaintTypes.TEXTURE) {
            IPaintColor paintColor1 = resolveTextureColor(scheme.getTexture(), partType);
            if (paintColor1 != null) {
                return paintColor;
            }
            return paintColor;
        }
        if (paintType.getDyeType() != null && deep < 2) {
            IPaintColor paintColor1 = scheme.getResolvedColor(paintType);
            if (paintColor1 == null) {
                return paintColor;
            }
            paintColor = dye(paintColor, paintColor1, descriptor.getAverageColor(paintType));
            return resolveColor(paintColor, scheme, descriptor, partType, deep + 1);
        }
        return paintColor;
    }

    private IPaintColor resolveTextureColor(ResourceLocation texture, ISkinPartType partType) {
        BakedEntityTexture bakedTexture = PlayerTextureLoader.getInstance().getTextureModel(texture);
        if (bakedTexture != null) {
            int x = (int) shape.getX();
            int y = (int) shape.getY();
            int z = (int) shape.getZ();
            return bakedTexture.getColor(x, y, z, direction, partType);
        }
        return null;
    }

    private ITextureKey resolveTexture(IPaintColor paintColor) {
        if (texture != null) {
            return texture;
        }
        return paintColor.getPaintType().getTexture();
    }

    private RenderType resolveRenderType(SkinCubeFace face) {
        ITextureKey texture = face.getTexture();
        if (texture != null && texture.getProvider() != null) {
            return SkinTextureManager.getInstance().prepareTexture(texture.getProvider());
        }
        return SkinRenderType.by(face.getType());
    }

    private Collection<RenderType> resolveRenderTypeVariants(SkinCubeFace face) {
        ITextureKey texture = face.getTexture();
        if (texture != null && texture.getProvider() != null) {
            return SkinTextureManager.getInstance().prepareVariantTextures(texture.getProvider());
        }
        return null;
    }

    private void resolveTransform(PoseStack poseStack) {
        poseStack.pushPose();
        IVector3f rotation = transform.getRotation();
        if (rotation != Vector3f.ZERO) {
            IVector3f pivot = transform.getPivot();
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(rotation.getY()));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(rotation.getX()));
            if (pivot != Vector3f.ZERO) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        IVector3f translate = transform.getTranslate();
        if (translate != Vector3f.ZERO) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        IVector3f scale = transform.getScale();
        if (scale != Vector3f.ONE) {
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        }
    }

    private IPaintColor dye(IPaintColor source, IPaintColor destination, IPaintColor average) {
        if (destination.getPaintType() == SkinPaintTypes.NONE) {
            return PaintColor.CLEAR;
        }
        if (average == null) {
            return source;
        }
        int src = (source.getRed() + source.getGreen() + source.getBlue()) / 3;
        int avg = (average.getRed() + average.getGreen() + average.getBlue()) / 3;
        int r = MathUtils.clamp(destination.getRed() + src - avg, 0, 255);
        int g = MathUtils.clamp(destination.getGreen() + src - avg, 0, 255);
        int b = MathUtils.clamp(destination.getBlue() + src - avg, 0, 255);
        return PaintColor.of(r, g, b, destination.getPaintType());
    }

    private float roundUp(float edg) {
        if (edg == 0) {
            return 0.002f;
        }
        return edg;
    }

        // avoid out-of-bounds behavior caused by floating point precision.
    private float roundDown(float edg) {
        if (edg < 0) {
            return edg + 0.002f;
        } else {
            return edg - 0.002f;
        }
    }

    public boolean intersects(OpenRay ray) {
        float x0 = shape.getMinX();
        float y0 = shape.getMinY();
        float z0 = shape.getMinZ();
        float x1 = shape.getMaxX();
        float y1 = shape.getMaxY();
        float z1 = shape.getMaxZ();
        return ray.intersects(x0, y0, z0, x1, y1, z1);
    }

    public IRectangle3f getShape() {
        return shape;
    }

    public ITransformf getTransform() {
        return transform;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    public Collection<RenderType> getRenderTypeVariants() {
        return renderTypeVariants;
    }

    public Direction getDirection() {
        return direction;
    }
}
