package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Available("[1.21, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractMenuScreenImpl<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public AbstractMenuScreenImpl(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        addWidget(new AbstractMenuTabDelegate(this));
    }

    public void renderInView(UIView view, int zLevel, int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        GuiGraphics graphics = AbstractGraphicsRenderer.of(context);
        super.render(graphics, mouseX, mouseY, partialTicks);
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    public void render(CGGraphicsContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(AbstractGraphicsRenderer.of(context), mouseX, mouseY, partialTicks);
    }

    public void renderLabels(CGGraphicsContext context, int mouseX, int mouseY) {
        super.renderLabels(AbstractGraphicsRenderer.of(context), mouseX, mouseY);
    }

    public void renderTooltip(CGGraphicsContext context, int mouseX, int mouseY) {
        super.renderTooltip(AbstractGraphicsRenderer.of(context), mouseX, mouseY);
    }

    public void renderBackground(CGGraphicsContext context) {
        super.renderTransparentBackground(AbstractGraphicsRenderer.of(context));
    }

    public void renderBackground(CGGraphicsContext context, Screen screen, int mouseX, int mouseY, float partialTicks) {
        screen.render(AbstractGraphicsRenderer.of(context), mouseX, mouseY, partialTicks);
    }

    @Override
    public final void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
        this.render(AbstractGraphicsRenderer.of(context, mouseX, mouseY, partialTicks), mouseX, mouseY, partialTicks);
    }

    @Override
    public final void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        this.renderLabels(AbstractGraphicsRenderer.of(context, mouseX, mouseY, 0), mouseX, mouseY);
    }

    @Override
    public final void renderTooltip(GuiGraphics context, int mouseX, int mouseY) {
        this.renderTooltip(AbstractGraphicsRenderer.of(context, mouseX, mouseY, 0), mouseX, mouseY);
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
