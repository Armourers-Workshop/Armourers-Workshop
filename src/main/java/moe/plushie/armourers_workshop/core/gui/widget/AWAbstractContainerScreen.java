package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public abstract class AWAbstractContainerScreen<T extends Container> extends ContainerScreen<T> {

    protected boolean enabledOnClose = true;

    protected AWAbstractDialog dialog;

    public AWAbstractContainerScreen(T container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        if (this.isPresenting()) {
            this.dialog.init(minecraft, width, height);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = this.imageWidth / 2 - this.font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
    }

    @Override
    public void removed() {
        super.removed();
        this.children.clear();
        this.buttons.clear();
        this.dialog = null;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0x404040);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.inventoryLabelX, (float) this.inventoryLabelY, 0x404040);
    }

    public void renderContentLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.pushPose();
        matrixStack.translate(0, 0, 400);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        matrixStack.popPose();
    }

    public void renderPresentLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.dialog != null) {
            this.dialog.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.isPresenting()) {
            this.renderContentLayer(matrixStack, 0, 0, partialTicks);
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 400);
            this.renderPresentLayer(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.popPose();
        } else {
            this.renderContentLayer(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (dialog != null && isPresenting()) {
            return dialog.mouseClicked(mouseX, mouseY, button);
        }
        if (getFocused() != null && getFocused().mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_231043_5_) {
        if (dialog != null && isPresenting()) {
            return dialog.mouseScrolled(mouseX, mouseY, p_231043_5_);
        }
        if (getFocused() != null && getFocused().mouseScrolled(mouseX, mouseY, p_231043_5_)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, p_231043_5_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
        if (dialog != null && isPresenting()) {
            return dialog.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
        }
        if (getFocused() != null && getFocused().mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dialog != null && isPresenting()) {
            return dialog.mouseReleased(mouseX, mouseY, button);
        }
        boolean results = getFocused() != null && getFocused().mouseReleased(mouseX, mouseY, button);
        super.mouseReleased(mouseX, mouseY, button);
        return results;
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        if (dialog != null && isPresenting()) {
            return dialog.keyPressed(key, p_231046_2_, p_231046_3_);
        }
        if (getFocused() != null && getFocused().keyPressed(key, p_231046_2_, p_231046_3_)) {
            return true;
        }
        enabledOnClose = closeOnNonEsc() || (key == GLFW.GLFW_KEY_ESCAPE);
        boolean results = super.keyPressed(key, p_231046_2_, p_231046_3_);
        enabledOnClose = true;
        return results;
    }

    @Override
    public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
        if (dialog != null && isPresenting()) {
            return dialog.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
        }
        return super.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
    }

    @Override
    public boolean charTyped(char p_231042_1_, int p_231042_2_) {
        if (dialog != null && isPresenting()) {
            return dialog.charTyped(p_231042_1_, p_231042_2_);
        }
        return super.charTyped(p_231042_1_, p_231042_2_);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (dialog != null && isPresenting()) {
            return dialog.isMouseOver(mouseX, mouseY);
        }
        if (getFocused() != null && getFocused().isMouseOver(mouseX, mouseY)) {
            return true;
        }
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void onClose() {
        if (enabledOnClose) {
            super.onClose();
        }
    }

    public boolean closeOnNonEsc() {
        return false;
    }

    public boolean isPresenting() {
        return dialog != null;
    }

    public <T extends AWAbstractDialog> void present(T dialog, Consumer<T> complete) {
        if (this.dialog != null) {
            this.dialog.removed();
        }
        this.dialog = dialog;
        if (this.dialog != null) {
            this.dialog.init(Minecraft.getInstance(), width, height);
            this.dialog.whenOnClose(d -> {
                if (complete != null) {
                    complete.accept(dialog);
                }
                this.dialog = null;
            });
        }
    }
}
