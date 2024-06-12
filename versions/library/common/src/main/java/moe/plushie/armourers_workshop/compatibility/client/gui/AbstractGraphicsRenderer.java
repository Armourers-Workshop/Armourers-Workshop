package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.EntityRendererImpl;
import com.apple.library.uikit.UIFont;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public class AbstractGraphicsRenderer implements CGGraphicsRenderer, CGGraphicsState {

    private final GuiGraphics graphics;
    private final CGPoint mousePos;
    private final IPoseStack poseStack;
    private final IBufferSource bufferSource;
    private final float partialTicks;

    public AbstractGraphicsRenderer(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.graphics = graphics;
        this.mousePos = new CGPoint(mouseX, mouseY);
        this.poseStack = AbstractPoseStack.wrap(graphics.pose());
        this.bufferSource = AbstractBufferSource.wrap(graphics.bufferSource());
        this.partialTicks = partialTicks;
    }

    public static GuiGraphics of(CGGraphicsContext context) {
        AbstractGraphicsRenderer impl = ObjectUtils.unsafeCast(context.state());
        return impl.graphics;
    }

    public static CGGraphicsContext of(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        AbstractGraphicsRenderer impl = new AbstractGraphicsRenderer(graphics, mouseX, mouseY, partialTicks);
        return new CGGraphicsContext(impl, impl);
    }

    @Override
    public void renderTooltip(NSString text, CGRect rect, UIFont font, CGGraphicsContext context) {
        // there are some versions of tooltip that don't split normally,
        // and while we can't decide on the final tooltip size,
        // but we can to handle the break the newline.
        var font1 = font.impl();
        var texts = font1.split(text.component(), 100000);
        graphics.renderTooltip(font1, texts, (int) mousePos.getX(), (int) mousePos.getY());
    }

    @Override
    public void renderTooltip(ItemStack itemStack, CGRect rect, UIFont font, CGGraphicsContext context) {
        var font1 = font.impl();
        graphics.renderTooltip(font1, itemStack, (int) mousePos.getX(), (int) mousePos.getY());
    }

    @Override
    public void renderEntity(Entity entity, CGPoint origin, int scale, CGPoint focus, CGGraphicsContext context) {
        EntityRendererImpl<Entity> renderer = AbstractGraphicsRendererImpl.getRenderer(entity);
        renderer.render(entity, origin, scale, focus, context);
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
    public IBufferSource bufferSource() {
        return bufferSource;
    }

    @Override
    public IPoseStack ctm() {
        return poseStack;
    }
}
