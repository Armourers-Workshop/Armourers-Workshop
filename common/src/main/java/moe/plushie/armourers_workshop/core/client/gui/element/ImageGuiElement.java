package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIEdgeInsets;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTypes;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

@SuppressWarnings("unused")
public abstract class ImageGuiElement implements CGGraphicsElement {

    protected UIColor tintColor;

    protected final float texWidth;
    protected final float texHeight;

    protected final OpenResourceKey texture;

    protected ImageGuiElement(OpenResourceKey texture, float texWidth, float texHeight) {
        this.texture = texture;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public static ImageGuiElement tilable(float x, float y, float width, float height, OpenResourceKey texture, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight, UIEdgeInsets border) {
        return new TilableImpl(x, y, width, height, texture, u, v, sourceWidth, sourceHeight, texWidth, texHeight, border);
    }

    public static ImageGuiElement resizable(float x, float y, float width, float height, OpenResourceKey texture, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight) {
        return new ResizableImpl(x, y, width, height, texture, u, v, sourceWidth, sourceHeight, texWidth, texHeight);
    }

    protected abstract void buildVertexes(Tesselator tesselator);

    @Override
    public void prepare(CGGraphicsContext context) {
        this.tintColor = context.blendColor();
    }

    @Override
    public void render(IPoseStack poseStack, IBufferSource bufferSource) {
        var builder = bufferSource.getBuffer(SkinRenderTypes.image(texture));
        buildVertexes(new Tesselator(tintColor, texWidth, texHeight, poseStack, builder));
    }

    protected static class Tesselator {

        private static final float[][] VERTEXES = new float[][]{{0, 1}, {1, 1}, {1, 0}, {0, 0}};

        private final IVertexConsumer builder;
        private final IPoseStack.Pose pose;

        private final int red;
        private final int green;
        private final int blue;
        private final int alpha = 255;

        private final float uScale;
        private final float vScale;

        public Tesselator(UIColor tintColor, float texWidth, float texHeight, IPoseStack poseStack, IVertexConsumer builder) {
            this.pose = poseStack.last();
            this.builder = builder;
            this.red = tintColor.red();
            this.green = tintColor.green();
            this.blue = tintColor.blue();
            this.uScale = 1 / texWidth;
            this.vScale = 1 / texHeight;
        }

        public void blit(float x, float y, float width, float height, float u, float v) {
            blit(x, y, width, height, u, v, width, height);
        }

        public void blit(float x, float y, float width, float height, float u, float v, float texWidth, float texHeight) {
            for (int i = 0; i < 4; ++i) {
                builder.vertex(pose, x + width * VERTEXES[i][0], y + height * VERTEXES[i][1], 0)
                        .color(red, green, blue, alpha)
                        .uv((u + texWidth * VERTEXES[i][0]) * uScale, (v + texHeight * VERTEXES[i][1]) * vScale)
                        .overlayCoords(0x0a0000)
                        .uv2(0xf000f0)
                        .normal(0, 0, 0)
                        .endVertex();
            }
        }

        public void tile(float x, float y, float width, float height, float u, float v, float texWidth, float texHeight) {
            float x1 = x + width;
            float y1 = y + height;
            for (float y0 = y; y0 < y1; y0 += texHeight) {
                for (float x0 = x; x0 < x1; x0 += texWidth) {
                    float w0 = Math.min(x1 - x0, texWidth);
                    float h0 = Math.min(y1 - y0, texHeight);
                    blit(x0, y0, w0, h0, u, v, w0, h0);
                }
            }
        }
    }

    protected static class ResizableImpl extends ImageGuiElement {

        private final float x;
        private final float y;
        private final float width;
        private final float height;
        private final float u;
        private final float v;
        private final float sourceWidth;
        private final float sourceHeight;

        public ResizableImpl(float x, float y, float width, float height, OpenResourceKey texture, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight) {
            super(texture, texWidth, texHeight);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.u = u;
            this.v = v;
            this.sourceWidth = sourceWidth;
            this.sourceHeight = sourceHeight;
        }

        @Override
        protected void buildVertexes(Tesselator tesselator) {
            tesselator.blit(x, y, width, height, u, v, sourceWidth, sourceHeight);
        }
    }

    public static class TilableImpl extends ImageGuiElement {

        private final float x;
        private final float y;
        private final float width;
        private final float height;
        private final float u;
        private final float v;
        private final float sourceWidth;
        private final float sourceHeight;

        private final UIEdgeInsets border;

        public TilableImpl(float x, float y, float width, float height, OpenResourceKey texture, float u, float v, float sourceWidth, float sourceHeight, float texWidth, float texHeight, UIEdgeInsets border) {
            super(texture, texWidth, texHeight);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.u = u;
            this.v = v;
            this.sourceWidth = sourceWidth;
            this.sourceHeight = sourceHeight;
            this.border = border;
        }

        @Override
        protected void buildVertexes(Tesselator tesselator) {
            var x0 = x + 0f;
            var y0 = y + 0f;
            var x3 = x0 + width;
            var y3 = y0 + height;
            var x1 = x0 + border.left;
            var y1 = y0 + border.top;
            var x2 = x3 - border.right;
            var y2 = y3 - border.bottom;

            var u0 = u + 0f;
            var v0 = v + 0f;
            var u3 = u0 + sourceWidth;
            var v3 = v0 + sourceHeight;
            var u1 = u0 + border.left;
            var v1 = v0 + border.top;
            var u2 = u3 - border.right;
            var v2 = v3 - border.bottom;

            tesselator.blit(x0, y0, x1 - x0, y1 - y0, u0, v0); // tl
            tesselator.blit(x2, y0, x3 - x2, y1 - y0, u2, v0); // tr
            tesselator.blit(x0, y2, x1 - x0, y3 - y2, u0, v2); // bl
            tesselator.blit(x2, y2, x3 - x2, y3 - y2, u2, v2); // br

            tesselator.tile(x1, y0, x2 - x1, y1 - y0, u1, v0, u2 - u1, v1 - v0); // tc
            tesselator.tile(x1, y2, x2 - x1, y3 - y2, u1, v2, u2 - u1, v3 - v2); // bc
            tesselator.tile(x0, y1, x1 - x0, y2 - y1, u0, v1, u1 - u0, v2 - v1); // lc
            tesselator.tile(x2, y1, x3 - x2, y2 - y1, u2, v1, u3 - u2, v2 - v1); // rc

            tesselator.tile(x1, y1, x2 - x1, y2 - y1, u1, v1, u2 - u1, v2 - v1); // cc
        }
    }
}
