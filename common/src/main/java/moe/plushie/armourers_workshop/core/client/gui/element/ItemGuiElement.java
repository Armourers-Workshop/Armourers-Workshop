package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.compat.client.gui.element.AbstractItemGuiElement;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public abstract class ItemGuiElement implements CGGraphicsElement {

    public static ItemGuiElement icon(ItemStack itemStack, int x, int y) {
        return AbstractItemGuiElement.newIconInstance(itemStack, x, y);
    }

    public static ItemGuiElement tooltip(ItemStack itemStack, UIFont font) {
        return AbstractItemGuiElement.newTooltipInstance(itemStack, font);
    }

    protected static abstract class IconImpl extends AbstractItemGuiElement {

        protected final int x;
        protected final int y;

        protected final ItemStack itemStack;

        public IconImpl(ItemStack itemStack, int x, int y) {
            this.itemStack = itemStack;
            this.x = x;
            this.y = y;
        }

        @Override
        public void prepare(CGGraphicsContext context) {
            super.prepare(context);

            // the rendering item will change the model view stack,
            // which may cause the pending draw to be executed in the wrong context,
            // so we need to flush first.
            context.flush();
        }
    }

    protected static abstract class TooltipImpl extends AbstractItemGuiElement {

        protected float mouseX;
        protected float mouseY;

        protected final UIFont font;
        protected final ItemStack itemStack;

        public TooltipImpl(ItemStack itemStack, UIFont font) {
            this.itemStack = itemStack;
            this.font = font;
        }

        @Override
        public void prepare(CGGraphicsContext context) {
            super.prepare(context);

            this.mouseX = context.param().mouseX();
            this.mouseY = context.param().mouseY();
        }
    }
}
