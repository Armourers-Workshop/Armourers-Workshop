package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.auto;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public class AbstractGraphicsRenderer implements CGGraphicsRenderer, CGGraphicsState {

    private final GuiGraphics graphics;
    private final CGPoint mousePos;
    private final float partialTicks;

    private final Font font;
    private UIFont uifont;

    public AbstractGraphicsRenderer(Font font, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.font = font;
        this.graphics = graphics;
        this.mousePos = new CGPoint(mouseX, mouseY);
        this.partialTicks = partialTicks;
    }

    public static GuiGraphics of(CGGraphicsContext context) {
        AbstractGraphicsRenderer impl = ObjectUtils.unsafeCast(context.state());
        return impl.graphics;
    }

    public static CGGraphicsContext of(Font font, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        AbstractGraphicsRenderer impl = new AbstractGraphicsRenderer(font, graphics, mouseX, mouseY, partialTicks);
        return new CGGraphicsContext(impl, impl);
    }

    @Override
    public void renderTooltip(NSString text, CGRect rect, @Nullable UIFont font, CGGraphicsContext context) {
        if (font == null) {
            font = context.state().font();
        }
        // there are some versions of tooltip that don't split normally,
        // and while we can't decide on the final tooltip size,
        // but we can to handle the break the newline
        auto font1 = font.impl();
        auto texts = font1.split(text.component(), 100000);
        graphics.renderTooltip(font1, texts, (int) mousePos.getX(), (int) mousePos.getY());
    }

    @Override
    public void renderEntity(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY, CGGraphicsContext context) {
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x, y, scale, mouseX, mouseY, entity);
    }

    @Override
    public void renderItem(ItemStack itemStack, int x, int y, CGGraphicsContext context) {
        graphics.renderFakeItem(itemStack, x, y);
    }

    @Override
    public void flush() {
        graphics.flush();
    }

    @Override
    public CGPoint mousePos() {
        return mousePos;
    }

    @Override
    public float partialTicks() {
        return partialTicks;
    }

    @Override
    public UIFont font() {
        if (uifont == null) {
            uifont = new UIFont(font, 9);
        }
        return uifont;
    }

    @Override
    public MultiBufferSource buffers() {
        return graphics.bufferSource();
    }

    @Override
    public PoseStack ctm() {
        return graphics.pose();
    }
}
