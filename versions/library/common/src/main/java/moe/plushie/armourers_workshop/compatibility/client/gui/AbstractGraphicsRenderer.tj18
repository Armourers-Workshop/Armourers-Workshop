package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Available("[1.18, 1.20)")
@Environment(EnvType.CLIENT)
public class AbstractGraphicsRenderer implements CGGraphicsRenderer, CGGraphicsState {

    private static final MultiBufferSource.BufferSource SHARED_BUFFERS = MultiBufferSource.immediate(new BufferBuilder(256));

    private final PoseStack poseStack;
    private final CGPoint mousePos;
    private final float partialTicks;

    private final AbstractMenuScreenImpl<?> screen;
    private final Font font;
    private UIFont uifont;

    public AbstractGraphicsRenderer(AbstractMenuScreenImpl<?> screen, Font font, PoseStack poseStack, float mouseX, float mouseY, float partialTicks) {
        this.screen = screen;
        this.font = font;
        this.poseStack = poseStack;
        this.mousePos = new CGPoint(mouseX, mouseY);
        this.partialTicks = partialTicks;
    }

    public static PoseStack of(CGGraphicsContext context) {
        AbstractGraphicsRenderer impl = ObjectUtils.unsafeCast(context.state());
        return impl.poseStack;
    }

    public static CGGraphicsContext of(AbstractMenuScreenImpl<?> screen, Font font, PoseStack poseStack, float mouseX, float mouseY, float partialTicks) {
        AbstractGraphicsRenderer impl = new AbstractGraphicsRenderer(screen, font, poseStack, mouseX, mouseY, partialTicks);
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
        List<? extends FormattedCharSequence> texts = font.impl().split(text.component(), 100000);
        screen._renderTooltip(poseStack, texts, (int) mousePos.getX(), (int) mousePos.getY());
    }

    @Override
    public void renderEntity(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY, CGGraphicsContext context) {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        InventoryScreen.renderEntityInInventory(x, y, scale, mouseX, mouseY, entity);
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public void renderItem(ItemStack itemStack, int x, int y, CGGraphicsContext context) {
        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(itemStack, x, y);
    }

    @Override
    public void flush() {
        SHARED_BUFFERS.endBatch();
    }

    @Override
    public UIFont font() {
        if (uifont == null) {
            uifont = new UIFont(font, 9);
        }
        return uifont;
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
    public MultiBufferSource buffers() {
        return SHARED_BUFFERS;
    }

    @Override
    public PoseStack ctm() {
        return poseStack;
    }
}
