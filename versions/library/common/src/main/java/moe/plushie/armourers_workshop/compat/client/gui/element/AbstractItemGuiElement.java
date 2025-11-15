package moe.plushie.armourers_workshop.compat.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.gui.element.ItemGuiElement;
import net.minecraft.world.item.ItemStack;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractItemGuiElement extends ItemGuiElement implements AbstractItemGuiElementImpl {

    protected CGGraphicsContext context;

    @Override
    public void prepare(CGGraphicsContext context) {
        super.prepare(context);
        this.context = context;
    }

    public static AbstractItemGuiElement newIconInstance(ItemStack itemStack, int x, int y) {
        return new IconImpl(itemStack, x, y);
    }

    public static AbstractItemGuiElement newTooltipInstance(ItemStack itemStack, UIFont font) {
        return new TooltipImpl(itemStack, font);
    }

    protected static class IconImpl extends ItemGuiElement.IconImpl {

        public IconImpl(ItemStack itemStack, int x, int y) {
            super(itemStack, x, y);
        }

        @Override
        public void render(IPoseStack poseStack, IBufferSource bufferSource) {
            renderItem(itemStack, x, y, context);
        }

        @Override
        public boolean shouldOffscreenRender() {
            return false;
        }
    }

    protected static class TooltipImpl extends ItemGuiElement.TooltipImpl {

        public TooltipImpl(ItemStack itemStack, UIFont font) {
            super(itemStack, font);
        }

        @Override
        public void render(IPoseStack poseStack, IBufferSource bufferSource) {
            renderItemTooltip(itemStack, mouseX, mouseY, font.impl(), context);
        }

        @Override
        public boolean shouldOffscreenRender() {
            return false;
        }
    }
}
