package moe.plushie.armourers_workshop.compatibility.client.gui;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.uikit.UIView;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;

@Available("[1.18, 1.20)")
@Environment(EnvType.CLIENT)
public abstract class AbstractMenuScreenImpl<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public AbstractMenuScreenImpl(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public void renderInView(UIView view, int zLevel, int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        PoseStack poseStack = AbstractGraphicsRenderer.of(context);
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(0, 0, zLevel);
        RenderSystem.applyModelViewMatrix();
        super.render(poseStack, mouseX, mouseY, partialTicks);
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        super.renderTooltip(poseStack, mouseX, mouseY);
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
        super.renderBackground(AbstractGraphicsRenderer.of(context));
    }

    public void renderBackground(CGGraphicsContext context, Screen screen, int mouseX, int mouseY, float partialTicks) {
        screen.render(AbstractGraphicsRenderer.of(context), mouseX, mouseY, partialTicks);
    }

    @Override
    public final void render(PoseStack poseStack, int i, int j, float f) {
        this.render(AbstractGraphicsRenderer.of(this, font, poseStack, i, j, f), i, j, f);
    }

    @Override
    protected final void renderLabels(PoseStack poseStack, int i, int j) {
        this.renderLabels(AbstractGraphicsRenderer.of(this, font, poseStack, i, j, 0), i, j);
    }

    @Override
    protected final void renderTooltip(PoseStack poseStack, int i, int j) {
        this.renderTooltip(AbstractGraphicsRenderer.of(this, font, poseStack, i, j, 0), i, j);
    }

    @Override
    protected final void renderBg(PoseStack poseStack, float f, int i, int j) {
        // ignored
    }

    public void _renderTooltip(PoseStack poseStack, List<? extends FormattedCharSequence> texts, int mouseX, int mouseY) {
        this.renderTooltip(poseStack, texts, mouseX, mouseY);
    }
}
