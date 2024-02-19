package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.JointShape;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.texture.TextureData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public abstract class AdvancedEntityGuideRenderer extends AbstractAdvancedGuideRenderer {

    protected final BakedArmature armature;
    protected final TextureData texture;

    protected final RenderType renderType;

    public AdvancedEntityGuideRenderer() {
        this.armature = getArmature();
        this.texture = getTexture();
        this.renderType = SkinRenderType.entityCutoutNoCull(new ResourceLocation(texture.getName()));
    }

    public abstract TextureData getTexture();

    public abstract BakedArmature getArmature();

    @Override
    public void render(SkinDocument document, IPoseStack poseStack, int light, int overlay, IBufferSource bufferSource) {
        poseStack.pushPose();
        IJointTransform[] transforms = armature.getTransforms();
        Armature armature1 = armature.getArmature();
        for (IJoint joint : armature1.allJoints()) {
            JointShape shape = armature1.getShape(joint.getId());
            IJointTransform transform = transforms[joint.getId()];
            if (shape != null && transform != null) {
                poseStack.pushPose();
                transform.apply(poseStack);
                renderShape(shape, ColorUtils.getPaletteColor(joint.getId()), poseStack, bufferSource);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    protected void renderShape(JointShape shape, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        poseStack.pushPose();
        Rectangle3f rect = shape.bounds();
        shape.transform().apply(poseStack);
        renderCube(shape, poseStack, bufferSource);
        renderOutline(rect, color, poseStack, bufferSource);
        poseStack.translate(rect.getX(), rect.getY(), rect.getZ());
        for (JointShape shape1 : shape.children()) {
            renderShape(shape1, color, poseStack, bufferSource);
        }
        poseStack.popPose();
    }

    protected void renderOutline(Rectangle3f rect, UIColor color, IPoseStack poseStack, IBufferSource bufferSource) {
        ShapeTesselator.stroke(rect, color, poseStack, bufferSource);
    }

    protected void renderCube(JointShape shape, IPoseStack poseStack, IBufferSource bufferSource) {
        for (Direction dir : Direction.values()) {
            renderCube(shape, dir, poseStack, bufferSource);
        }
    }

    private void renderCube(JointShape shape, Direction dir, IPoseStack poseStack, IBufferSource bufferSource) {
        Rectangle3f rect = shape.bounds();
        Rectangle2f uv = shape.getUV(dir);
        if (uv == null) {
            return;
        }

        auto entry = poseStack.last();
        auto builder = bufferSource.getBuffer(renderType);

        float x = rect.getX();
        float y = rect.getY();
        float z = rect.getZ();
        float w = rect.getWidth();
        float h = rect.getHeight();
        float d = rect.getDepth();

        float u = uv.getX();
        float v = uv.getY();
        float s = uv.getWidth();
        float t = uv.getHeight();
        float n = texture.getWidth();
        float m = texture.getHeight();

        float[][] uvs = SkinUtils.getRenderUVs(dir);
        float[][] vertexes = SkinUtils.getRenderVertexes(dir);
        for (int i = 0; i < 4; ++i) {
            builder.vertex(entry, x + w * vertexes[i][0], y + h * vertexes[i][1], z + d * vertexes[i][2])
                    .color(255, 255, 255, 255)
                    .uv((u + s * uvs[i][0]) / n, (v + t * uvs[i][1]) / m)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(0xf000f0)
                    .normal(entry, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
}
