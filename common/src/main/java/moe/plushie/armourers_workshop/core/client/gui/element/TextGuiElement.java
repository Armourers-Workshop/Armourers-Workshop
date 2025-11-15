package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.compat.client.gui.element.AbstractTextGuiElement;

import java.util.Collection;

@SuppressWarnings("unused")
public abstract class TextGuiElement implements CGGraphicsElement {

    public static TextGuiElement normal(float x, float y, Collection<NSString> lines, UIFont font, int textColor, boolean shadow) {
        return AbstractTextGuiElement.newNormalInstance(x, y, lines, font, textColor, shadow);
    }

    public static TextGuiElement tooltip(NSString text, UIFont font) {
        return AbstractTextGuiElement.newTooltipInstance(text, font);
    }

    protected static abstract class NormalImpl extends AbstractTextGuiElement {

        protected final float x;
        protected final float y;
        protected final int textColor;
        protected final boolean shadow;

        protected final UIFont font;
        protected final Collection<NSString> lines;

        public NormalImpl(float x, float y, Collection<NSString> lines, UIFont font, int textColor, boolean shadow) {
            this.x = x;
            this.y = y;
            this.lines = lines;
            this.font = font;
            this.textColor = textColor;
            this.shadow = shadow;
        }
    }

    protected static abstract class TooltipImpl extends AbstractTextGuiElement {

        protected float mouseX;
        protected float mouseY;

        protected final UIFont font;
        protected final NSString text;

        public TooltipImpl(NSString text, UIFont font) {
            this.text = text;
            this.font = font;
        }

        @Override
        public void prepare(CGGraphicsContext context) {
            this.mouseX = context.param().mouseX();
            this.mouseY = context.param().mouseY();
        }
    }
}
