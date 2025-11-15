package moe.plushie.armourers_workshop.compat.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.gui.element.TextGuiElement;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.Collection;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractTextGuiElement extends TextGuiElement implements AbstractTextGuiElementImpl {

    public static AbstractTextGuiElement newNormalInstance(float x, float y, Collection<NSString> lines, UIFont font, int textColor, boolean shadow) {
        return new NormalImpl(x, y, lines, font, textColor, shadow);
    }

    public static AbstractTextGuiElement newTooltipInstance(NSString text, UIFont font) {
        return new TooltipImpl(text, font);
    }

    protected static class NormalImpl extends TextGuiElement.NormalImpl {

        public NormalImpl(float x, float y, Collection<NSString> lines, UIFont font, int textColor, boolean shadow) {
            super(x, y, lines, font, textColor, shadow);
        }

        @Override
        public void render(IPoseStack poseStack, IBufferSource bufferSource) {
            var scale = font._scale();
            poseStack.pushPose();
            poseStack.translate(x, y, 0);
            poseStack.scale(scale, scale, scale);

            var impl = font.impl();
            var texts = Collections.compactMap(lines, NSString::characters);

            renderText(texts, x, y, textColor, shadow, impl, poseStack, bufferSource);

            poseStack.popPose();
        }
    }

    protected static class TooltipImpl extends TextGuiElement.TooltipImpl {

        private CGGraphicsContext context;

        public TooltipImpl(NSString text, UIFont font) {
            super(text, font);
        }

        @Override
        public void prepare(CGGraphicsContext context) {
            super.prepare(context);
            this.context = context;
        }

        @Override
        public void render(IPoseStack poseStack, IBufferSource bufferSource) {
            // there are some versions of tooltip that don't split normally,
            // and while we can't decide on the final tooltip size,
            // but we can to handle the break the newline.
            var impl = font.impl();
            var texts = impl.split(text.component(), 100000);
            renderTextTooltip(texts, mouseX, mouseY, impl, context);
        }

        @Override
        public boolean shouldOffscreenRender() {
            return false;
        }
    }
}
