package moe.plushie.armourers_workshop.compat.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.gui.render.AbstractGuiGraphicsRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Available("[1.21, )")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractMenuScreenImpl<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public AbstractMenuScreenImpl(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        addWidget(new AbstractMenuTabDelegate(this));
    }

    public void renderInView(UIView view, int zLevel, int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        AbstractGuiGraphicsRenderer.unwrap(context, "overlay", graphics -> {
            super.render(graphics, mouseX, mouseY, partialTicks);
            super.renderTooltip(graphics, mouseX, mouseY);
        });
    }

    public void render(CGGraphicsContext context, int mouseX, int mouseY, float partialTicks) {
        AbstractGuiGraphicsRenderer.unwrap(context, "container", graphics -> {
            super.render(graphics, mouseX, mouseY, partialTicks);
        });
    }

    public void renderLabels(CGGraphicsContext context, int mouseX, int mouseY) {
        AbstractGuiGraphicsRenderer.unwrap(context, "content", graphics -> {
            super.renderLabels(graphics, mouseX, mouseY);
        });
    }

    public void renderTooltip(CGGraphicsContext context, int mouseX, int mouseY) {
        AbstractGuiGraphicsRenderer.unwrap(context, "tooltip", graphics -> {
            super.renderTooltip(graphics, mouseX, mouseY);
        });
    }

    public void renderBackground(CGGraphicsContext context) {
        AbstractGuiGraphicsRenderer.unwrap(context, "background", graphics -> {
            super.renderTransparentBackground(graphics);
        });
    }

    @Override
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        AbstractGuiGraphicsRenderer.wrap(graphics, width, height, mouseX, mouseY, partialTicks, context -> {
            this.render(context, mouseX, mouseY, partialTicks);
        });
    }

    @Override
    public final void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        AbstractGuiGraphicsRenderer.wrap(graphics, width, height, mouseX, mouseY, 0, context -> {
            this.renderLabels(context, mouseX, mouseY);
        });
    }

    @Override
    public final void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        AbstractGuiGraphicsRenderer.wrap(graphics, width, height, mouseX, mouseY, 0, context -> {
            this.renderTooltip(context, mouseX, mouseY);
        });
    }

    @Override
    public final void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        // ignored
    }

    @Override
    protected final void renderBg(GuiGraphics context, float f, int i, int j) {
        // ignored
    }

    public boolean changeFocus(boolean bl) {
        return false;
    }
}
