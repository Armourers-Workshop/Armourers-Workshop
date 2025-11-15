package moe.plushie.armourers_workshop.compat.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.gui.render.AbstractGuiGraphicsRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;

@Available("[1.20, 1.22)")
@OnlyIn(Dist.CLIENT)
public interface AbstractItemGuiElementImpl {

    default void renderItem(ItemStack itemStack, float tx, float ty, CGGraphicsContext context) {
        AbstractGuiGraphicsRenderer.unwrap(context, "content", graphics -> {
            graphics.renderFakeItem(itemStack, (int) tx, (int) ty);
        });
    }

    default void renderItemTooltip(ItemStack itemStack, float mouseX, float mouseY, Font font, CGGraphicsContext context) {
        AbstractGuiGraphicsRenderer.unwrap(context, "tooltip", graphics -> {
            graphics.renderTooltip(font, itemStack, (int) mouseX, (int) mouseY);
        });
    }
}
