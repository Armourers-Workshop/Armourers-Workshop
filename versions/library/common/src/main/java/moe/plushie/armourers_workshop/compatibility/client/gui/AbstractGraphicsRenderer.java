package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGradient;
import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public class AbstractGraphicsRenderer implements CGGraphicsRenderer, CGGraphicsState {

    private final GuiGraphics graphics;
    private final int mouseX;
    private final int mouseY;
    private final float partialTicks;

    private final Font font;
    private UIFont uifont;

    public AbstractGraphicsRenderer(Font font, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.font = font;
        this.graphics = graphics;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
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
    public void renderText(FormattedCharSequence text, float x, float y, int textColor, boolean shadow, boolean bl2, int j, int k, UIFont font, CGGraphicsContext context) {
        if (font == null) {
            font = context.state().font();
        }
        PoseStack poseStack = graphics.pose();
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        font.font().drawInBatch(text, x, y, textColor, shadow, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, j, k);
        bufferSource.endBatch();
    }

    @Override
    public void renderTooltip(NSString text, CGRect rect, @Nullable UIFont font, CGGraphicsContext context) {
        if (font == null) {
            font = context.state().font();
        }
        // there are some versions of tooltip that don't split normally,
        // and while we can't decide on the final tooltip size,
        // but we can to handle the break the newline
        Font font1 = font.font();
        List<? extends FormattedCharSequence> texts = font1.split(text.component(), 100000);
        graphics.renderTooltip(font1, texts, mouseX, mouseY);
    }

    @Override
    public void renderImage(ResourceLocation texture, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight, CGGraphicsContext context) {
        graphics.blit(texture, x, y, 0, u, v, width, height, texWidth, texHeight);
    }

    @Override
    public void renderColor(int x1, int y1, int x2, int y2, int color, CGGraphicsContext context) {
        graphics.fill(x1, y1, x2, y2, color);
    }

    @Override
    public void renderGradient(CGGradient gradient, CGRect rect, CGGraphicsContext context) {
        int i = rect.x;
        int j = rect.y;
        int k = rect.getMaxX();
        int l = rect.getMaxY();
        int m = gradient.startColor.getRGB();
        int n = gradient.endColor.getRGB();
        graphics.fillGradient(i, j, k, l, m, n);
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
    public int mouseX() {
        return mouseX;
    }

    @Override
    public int mouseY() {
        return mouseY;
    }

    @Override
    public float partialTicks() {
        return partialTicks;
    }

    @Override
    public UIFont font() {
        if (uifont == null) {
            uifont = new UIFont(font);
        }
        return uifont;
    }

    @Override
    public PoseStack ctm() {
        return graphics.pose();
    }
}
