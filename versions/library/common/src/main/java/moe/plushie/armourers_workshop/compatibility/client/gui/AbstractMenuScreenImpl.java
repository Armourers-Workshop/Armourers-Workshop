package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGradient;
import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import com.apple.library.coregraphics.CGGraphicsState;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Available("[1.20, )")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractMenuScreenImpl<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public AbstractMenuScreenImpl(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        addWidget(new TabEventProxy());
    }

    public void renderInView(UIView view, int zLevel, int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        GuiGraphics graphics = from(context);
        super.render(graphics, mouseX, mouseY, partialTicks);
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    public void render(CGGraphicsContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(from(context), mouseX, mouseY, partialTicks);
    }

    public void renderLabels(CGGraphicsContext context, int mouseX, int mouseY) {
        super.renderLabels(from(context), mouseX, mouseY);
    }

    public void renderTooltip(CGGraphicsContext context, int mouseX, int mouseY) {
        super.renderTooltip(from(context), mouseX, mouseY);
    }

    public void renderBackground(CGGraphicsContext context) {
        super.renderBackground(from(context));
    }

    public void renderBackground(CGGraphicsContext context, Screen screen, int mouseX, int mouseY, float partialTicks) {
        screen.render(from(context), mouseX, mouseY, partialTicks);
    }

    @Override
    public final void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        this.render(from(context, mouseX, mouseY, partialTicks), mouseX, mouseY, partialTicks);
    }

    @Override
    public final void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        this.renderLabels(from(context, mouseX, mouseY, 0), mouseX, mouseY);
    }

    @Override
    public final void renderTooltip(GuiGraphics context, int mouseX, int mouseY) {
        this.renderTooltip(from(context, mouseX, mouseY, 0), mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float f, int i, int j) {
        // ignored
    }

    public boolean changeFocus(boolean bl) {
        return false;
    }

    private GuiGraphics from(CGGraphicsContext context) {
        RendererImpl impl = ObjectUtils.unsafeCast(context.state());
        return impl.graphics;
    }

    private CGGraphicsContext from(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RendererImpl impl = new RendererImpl(graphics, mouseX, mouseY, partialTicks);
        return new CGGraphicsContext(impl, impl);
    }

    /**
     * se use own GUI rendering system,
     * but vanilla events are different.
     * so we need a proxy to forward the tab event.
     */
    public class TabEventProxy implements GuiEventListener, NarratableEntry {

        @Override
        public void setFocused(boolean bl) {
        }

        @Override
        public boolean isFocused() {
            return false;
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
            if (focusNavigationEvent instanceof FocusNavigationEvent.TabNavigation) {
                boolean value = ((FocusNavigationEvent.TabNavigation) focusNavigationEvent).forward();
                if (changeFocus(value)) {
                    return ComponentPath.leaf(this);
                }
            }
            return null;
        }
    }

    public class RendererImpl implements CGGraphicsRenderer, CGGraphicsState {

        private final GuiGraphics graphics;
        private final int mouseX;
        private final int mouseY;
        private final float partialTicks;

        private UIFont uifont;

        RendererImpl(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            this.graphics = graphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTicks = partialTicks;
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
        public void renderEntity(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, x, y, scale, mouseX, mouseY, entity);
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
}
