package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.JointShape;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.texture.TextureData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
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
    public void render(PoseStack poseStack, int light, int overlay, float r, float g, float b, float alpha, MultiBufferSource buffers) {
        poseStack.pushPose();
        IJointTransform[] transforms = armature.getTransforms();
        Armature armature1 = armature.getArmature();
        for (IJoint joint : armature1.allJoints()) {
            JointShape shape = armature1.getShape(joint.getId());
            IJointTransform transform = transforms[joint.getId()];
            if (shape != null && transform != null) {
                poseStack.pushPose();
                poseStack.applyTransform(transform);
                renderShape(shape, ColorUtils.getPaletteColor(joint.getId()), poseStack, buffers);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    protected void renderShape(JointShape shape, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        poseStack.pushPose();
        Rectangle3f rect = shape.bounds();
        poseStack.applyTransform(shape.transform());
        renderCube(shape, poseStack, buffers);
        renderOutline(rect, color, poseStack, buffers);
        poseStack.translate(rect.getX(), rect.getY(), rect.getZ());
        for (JointShape shape1 : shape.children()) {
            renderShape(shape1, color, poseStack, buffers);
        }
        poseStack.popPose();
    }

    protected void renderOutline(Rectangle3f rect, UIColor color, PoseStack poseStack, MultiBufferSource buffers) {
        ShapeTesselator.stroke(rect, color, poseStack, buffers);
    }

    protected void renderCube(JointShape shape, PoseStack poseStack, MultiBufferSource buffers) {
        for (Direction dir : Direction.values()) {
            renderCube(shape, dir, poseStack, buffers);
        }
    }

    private void renderCube(JointShape shape, Direction dir, PoseStack poseStack, MultiBufferSource buffers) {
        Rectangle3f rect = shape.bounds();
        Rectangle2f uv = shape.getUV(dir);
        if (uv == null) {
            return;
        }

        auto pose = poseStack.last().pose();
        auto normal = poseStack.last().normal();
        auto builder = buffers.getBuffer(renderType);

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
            builder.vertex(pose, x + w * vertexes[i][0], y + h * vertexes[i][1], z + d * vertexes[i][2])
                    .color(255, 255, 255, 255)
                    .uv((u + s * uvs[i][0]) / n, (v + t * uvs[i][1]) / m)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(0xf000f0)
                    .normal(normal, vertexes[4][0], vertexes[4][1], vertexes[4][2])
                    .endVertex();
        }
    }
}
