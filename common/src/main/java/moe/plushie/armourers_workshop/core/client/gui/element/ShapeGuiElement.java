package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.utils.Colors;

@SuppressWarnings("unused")
public abstract class ShapeGuiElement implements CGGraphicsElement {

    public static ShapeGuiElement fill(float minX, float minY, float maxX, float maxY, int startColor, int endColor) {
        return new FillRectImpl(minX, minY, maxX, maxY, startColor, endColor);
    }

    public static ShapeGuiElement stroke(float minX, float minY, float maxX, float maxY, float height, int color) {
        return new StrokeRectImpl(minX, minY, maxX, maxY, height, color);
    }

    protected static class FillRectImpl extends ShapeGuiElement {

        private IRenderType renderType;

        private final float minX;
        private final float minY;
        private final float maxX;
        private final float maxY;
        private final int startColor;
        private final int endColor;

        private final int lightmap = LightmapTexture.DEFAULT;
        private final int overlay = OverlayTexture.NO_OVERLAY;

        public FillRectImpl(float minX, float minY, float maxX, float maxY, int startColor, int endColor) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        public void prepare(CGGraphicsContext context) {
            this.renderType = switch (context.blendMode()) {
                case DIFFERENCE -> SkinRenderType.GUI_REVERSED_COLOR;
                default -> SkinRenderType.GUI_COLOR;
            };
        }

        @Override
        public void render(IPoseStack poseStack, IBufferSource bufferSource) {
            var entry = poseStack.last();
            var builder = bufferSource.getBuffer(renderType);

            var r1 = Colors.getRed(startColor);
            var g1 = Colors.getGreen(startColor);
            var b1 = Colors.getBlue(startColor);
            var a1 = Colors.getAlpha(startColor);
            var r2 = Colors.getRed(endColor);
            var g2 = Colors.getGreen(endColor);
            var b2 = Colors.getBlue(endColor);
            var a2 = Colors.getAlpha(endColor);

            var u1 = OverlayTexture.getU(overlay);
            var v1 = OverlayTexture.getV(overlay);

            var u2 = LightmapTexture.getU(lightmap);
            var v2 = LightmapTexture.getV(lightmap);

            builder.vertex(entry, minX, minY, 0).color(r1, g1, b1, a1).uv(0, 0).overlayCoords(u1, v1).uv2(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, minX, maxY, 0).color(r2, g2, b2, a2).uv(0, 0).overlayCoords(u1, v1).uv2(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, maxX, maxY, 0).color(r2, g2, b2, a2).uv(0, 0).overlayCoords(u1, v1).uv2(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, maxX, minY, 0).color(r1, g1, b1, a1).uv(0, 0).overlayCoords(u1, v1).uv2(u2, v2).normal(0, 0, 0).endVertex();
        }
    }

    protected static class StrokeRectImpl extends ShapeGuiElement {

        private final float minX;
        private final float minY;
        private final float maxX;
        private final float maxY;
        private final float height;
        private final int color;

        private final int lightmap = LightmapTexture.DEFAULT;
        private final int overlay = OverlayTexture.NO_OVERLAY;

        public StrokeRectImpl(float minX, float minY, float maxX, float maxY, float height, int color) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
            this.height = height;
            this.color = color;
        }

        @Override
        public void render(IPoseStack poseStack, IBufferSource bufferSource) {
            if (height > 0.1f) {
                renderSolidLine(height * 0.5f, poseStack, bufferSource);
            } else {
                renderDebugLine(poseStack, bufferSource);
            }
        }

        private void renderSolidLine(float hl, IPoseStack poseStack, IBufferSource bufferSource) {
            var entry = poseStack.last();
            var buffer = bufferSource.getBuffer(SkinRenderType.GUI_COLOR);

            var r = Colors.getRed(color);
            var g = Colors.getGreen(color);
            var b = Colors.getBlue(color);
            var a = Colors.getAlpha(color);

            var u1 = OverlayTexture.getU(overlay);
            var v1 = OverlayTexture.getV(overlay);

            var u2 = LightmapTexture.getU(lightmap);
            var v2 = LightmapTexture.getV(lightmap);

            buffer.vertex(entry, minX - hl, minY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, minX - hl, maxY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, minX + hl, maxY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, minX + hl, minY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();

            buffer.vertex(entry, maxX - hl, minY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, maxX - hl, maxY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, maxX + hl, maxY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, maxX + hl, minY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();

            buffer.vertex(entry, minX + hl, minY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, minX + hl, minY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, maxX - hl, minY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, maxX - hl, minY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();

            buffer.vertex(entry, minX + hl, maxY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, minX + hl, maxY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, maxX - hl, maxY + hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            buffer.vertex(entry, maxX - hl, maxY - hl, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
        }

        private void renderDebugLine(IPoseStack poseStack, IBufferSource bufferSource) {
            var entry = poseStack.last();
            var builder = bufferSource.getBuffer(SkinRenderType.line());

            var r = Colors.getRed(color);
            var g = Colors.getGreen(color);
            var b = Colors.getBlue(color);
            var a = Colors.getAlpha(color);

            var u1 = OverlayTexture.getU(overlay);
            var v1 = OverlayTexture.getV(overlay);

            var u2 = LightmapTexture.getU(lightmap);
            var v2 = LightmapTexture.getV(lightmap);

            builder.vertex(entry, minX, minY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, minX, maxY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, minX, maxY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, maxX, maxY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, maxX, maxY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, maxX, minY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, maxX, minY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
            builder.vertex(entry, minX, minY, 0).color(r, g, b, a).uv(0, 0).uv(u1, v1).uv(u2, v2).normal(0, 0, 0).endVertex();
        }
    }
}
