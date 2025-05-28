package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.JointShape;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCubeFace;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;

@Environment(EnvType.CLIENT)
public abstract class AdvancedEntityGuideRenderer extends AdvancedAbstractGuideRenderer {

    protected final BakedArmature armature;
    protected final SkinTextureData texture;

    protected final IRenderType renderType;

    public AdvancedEntityGuideRenderer() {
        this.armature = getArmature();
        this.texture = getTexture();
        this.renderType = getRenderType(texture);
    }

    public abstract SkinTextureData getTexture();

    public abstract BakedArmature getArmature();

    public IRenderType getRenderType(SkinTextureData texture) {
        return SkinRenderType.entityCutoutNoCull(OpenResourceLocation.parse(texture.getName()));
    }

    public void applyOffset(SkinDocument document, IPoseStack poseStack) {

    }

    @Override
    public void render(SkinDocument document, IPoseStack poseStack, int light, int overlay, IBufferSource bufferSource) {
        poseStack.pushPose();
        applyOffset(document, poseStack);
        var transforms = armature.getTransforms();
        var armature1 = armature.getArmature();
        for (var joint : armature1.allJoints()) {
            var shape = armature1.getShape(joint.getId());
            var transform = transforms[joint.getId()];
            if (shape != null && transform != null) {
                poseStack.pushPose();
                transform.apply(poseStack);
                renderShape(shape, ColorUtils.getPaletteColor(joint.getId()), poseStack, bufferSource);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    protected void renderShape(JointShape shape, int color, IPoseStack poseStack, IBufferSource bufferSource) {
        poseStack.pushPose();
        var rect = shape.bounds();
        shape.transform().apply(poseStack);
        renderCube(shape, poseStack, 1, 1, 1, 1, bufferSource);
        renderOutline(rect, color, poseStack, bufferSource);
        poseStack.translate(rect.x(), rect.y(), rect.z());
        for (var shape1 : shape.children()) {
            renderShape(shape1, color, poseStack, bufferSource);
        }
        poseStack.popPose();
    }

    protected void renderOutline(OpenRectangle3f rect, int color, IPoseStack poseStack, IBufferSource bufferSource) {
        ShapeTesselator.stroke(rect, color, poseStack, bufferSource);
    }

    protected void renderCube(JointShape shape, IPoseStack poseStack, float r, float g, float b, float a, IBufferSource bufferSource) {
        for (var dir : OpenDirection.values()) {
            renderCube(shape, dir, poseStack, r, g, b, a, bufferSource);
        }
    }

    private void renderCube(JointShape shape, OpenDirection dir, IPoseStack poseStack, float r, float g, float b, float a, IBufferSource bufferSource) {
        var rect = shape.bounds();
        var uv = shape.getUV(dir);
        if (uv == null) {
            return;
        }

        var entry = poseStack.last();
        var builder = bufferSource.getBuffer(renderType);

        float x = rect.x();
        float y = rect.y();
        float z = rect.z();
        float w = rect.width();
        float h = rect.height();
        float d = rect.depth();

        float u = uv.x();
        float v = uv.y();
        float s = uv.width();
        float t = uv.height();
        float n = texture.getWidth();
        float m = texture.getHeight();

        var uvs = SkinCubeFace.getBaseUVs(dir, 0);
        var vertexes = SkinCubeFace.getBaseVertices(dir);
        for (int i = 0; i < 4; ++i) {
            builder.vertex(entry, x + w * vertexes[i][0], y + h * vertexes[i][1], z + d * vertexes[i][2])
                    .color(r, g, b, a)
                    .uv((u + s * uvs[i][0]) / n, (v + t * uvs[i][1]) / m)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(0xf000f0)
                    .normal(entry, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
}
