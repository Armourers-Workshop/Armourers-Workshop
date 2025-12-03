package moe.plushie.armourers_workshop.compat.client.event;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.gui.render.AbstractGuiGraphicsRenderer;
import moe.plushie.armourers_workshop.init.event.client.ItemTooltipEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Available("[1.20, )")
@OnlyIn(Dist.CLIENT)
public class AbstractRenderItemTooltipEvent {

    public static ItemTooltipEvent.Render create(ItemStack itemStack, float x, float y, float width, float height, float screenWidth, float screenHeight, float mouseX, float mouseY, float partialTicks, GuiGraphics graphics) {
        return create(itemStack, new CGRect(x, y, width, height), screenWidth, screenHeight, mouseX, mouseY, partialTicks, graphics);
    }

    public static ItemTooltipEvent.Render create(ItemStack itemStack, CGRect frame, float screenWidth, float screenHeight, float mouseX, float mouseY, float partialTicks, GuiGraphics graphics) {
        return new ItemTooltipEvent.Render() {

            @Override
            public void draw(Consumer<CGGraphicsContext> action) {
                AbstractGuiGraphicsRenderer.wrap(graphics, screenWidth, screenHeight, mouseX, mouseY, partialTicks, action);
            }

            @Override
            public ItemStack itemStack() {
                return itemStack;
            }

            @Override
            public CGRect frame() {
                return frame;
            }

            @Override
            public float screenWidth() {
                return screenWidth;
            }

            @Override
            public float screenHeight() {
                return screenHeight;
            }
        };
    }
}
