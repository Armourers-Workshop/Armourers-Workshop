package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.ClipManagerImpl;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.init.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class ClipGuiElement implements CGGraphicsElement {

    private static final ClipManagerImpl CLIP_MANAGER = new ClipManagerImpl();
    private static final CGGraphicsContext CLIP_CONTEXT = new CGGraphicsContext(element -> element.render(null, null));


    public static ClipGuiElement beginClipLayer(CGRect rect, float cornerRadius) {
        return new BeginClipLayer(ScreenRectangle.create(rect, cornerRadius));
    }

    public static ClipGuiElement endClipLayer() {
        return EndClipLayer.INSTANCE;
    }

    public static ClipGuiElement beginEntityClipLayer(int x, int y, int scale) {
        if (!ModConfig.Client.enableEntityClipInInventory) {
            return null;
        }
        int left, top, width, height;
        switch (scale) {
            case 20: // in creative container screen
                width = 32;
                height = 43;
                left = x - width / 2 + 1;
                top = y - height + 4;
                break;

            case 30: // in survival container screen
                width = 49;
                height = 70;
                left = x - width / 2 - 1;
                top = y - height + 3;
                break;

            default:
                return null;
        }
        var element = ClipGuiElement.beginClipLayer(new CGRect(left, top, width, height), 0);
        CLIP_CONTEXT.draw(element);
        return element;
    }

    public static ClipGuiElement endEntityClipLayer() {
        if (!ModConfig.Client.enableEntityClipInInventory) {
            return null;
        }
        var element = ClipGuiElement.endClipLayer();
        CLIP_CONTEXT.draw(element);
        return element;
    }

    protected abstract void apply(ClipManagerImpl clipRenderer);

    @Override
    public void prepare(CGGraphicsContext context) {
        // when the render state changed,
        // we need to flush current buffers immediately.
        context.flush();
    }

    @Override
    public void render(IPoseStack poseStack, IBufferSource bufferSource) {
        // apply the clip mask by the opengl.
        apply(CLIP_MANAGER);
    }

    public static class BeginClipLayer extends ClipGuiElement {

        private final ScreenRectangle clipBox;

        private BeginClipLayer(ScreenRectangle clipBox) {
            this.clipBox = clipBox;
        }

        @Override
        protected void apply(ClipManagerImpl manager) {
            manager.addClipPath(clipBox.rect, clipBox.offscreenPasses());
        }
    }

    public static class EndClipLayer extends ClipGuiElement {

        private static final EndClipLayer INSTANCE = new EndClipLayer();

        @Override
        protected void apply(ClipManagerImpl manager) {
            manager.removeClipPath();
        }
    }

    private static class ScreenRectangle {

        protected final CGRect rect;

        public ScreenRectangle(CGRect rect) {
            this.rect = rect;
        }

        public static ScreenRectangle create(CGRect rect, float cornerRadius) {
            if (cornerRadius > 0) {
                return new RoundScreenRectangle(rect, cornerRadius);
            }
            return new ScreenRectangle(rect);
        }

        @Nullable
        public List<CGRect> offscreenPasses() {
            return null;
        }
    }

    private static class RoundScreenRectangle extends ScreenRectangle {

        protected final float cornerRadius;

        public RoundScreenRectangle(CGRect rect, float cornerRadius) {
            super(rect);
            this.cornerRadius = cornerRadius;
        }

        @Override
        public List<CGRect> offscreenPasses() {
            var passes = new ArrayList<CGRect>();
            passes.add(new CGRect(rect.minX(), rect.minY(), cornerRadius, cornerRadius));
            passes.add(new CGRect(rect.maxX(), rect.minY(), -cornerRadius, cornerRadius));
            passes.add(new CGRect(rect.maxX(), rect.maxY(), -cornerRadius, -cornerRadius));
            passes.add(new CGRect(rect.minX(), rect.maxY(), cornerRadius, -cornerRadius));
            return passes;
        }
    }
}
