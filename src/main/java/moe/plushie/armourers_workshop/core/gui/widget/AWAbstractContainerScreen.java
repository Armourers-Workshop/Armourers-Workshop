package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.library.gui.panels.GlobalLibraryAbstractPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public abstract class AWAbstractContainerScreen<T extends Container> extends ContainerScreen<T> {

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

    public void renderSuperLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderContentLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderSuperLayer(matrixStack, mouseX, mouseY, partialTicks);
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
        if (forwardToFocused(f -> f.mouseClicked(mouseX, mouseY, button))) {
            return true;
        }
        for (IGuiEventListener responder : nextResponder()) {
            if (responder.mouseClicked(mouseX, mouseY, button)) {
                if (responder instanceof FocusableGui) {
                    setFocusedWithResponder((FocusableGui) responder);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_231043_5_) {
        if (dialog != null && isPresenting()) {
            return dialog.mouseScrolled(mouseX, mouseY, p_231043_5_);
        }
        if (forwardToFocused(f -> f.mouseScrolled(mouseX, mouseY, p_231043_5_))) {
            return true;
        }
        for (IGuiEventListener responder : nextResponder()) {
            if (responder.mouseScrolled(mouseX, mouseY, p_231043_5_)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, p_231043_5_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
        if (dialog != null && isPresenting()) {
            return dialog.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
        }
        if (forwardToFocused(f -> f.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_))) {
            return true;
        }
        for (IGuiEventListener responder : nextResponder()) {
            if (responder.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dialog != null && isPresenting()) {
            return dialog.mouseReleased(mouseX, mouseY, button);
        }
        for (IGuiEventListener responder : nextResponder()) {
            responder.mouseScrolled(mouseX, mouseY, button);
        }
        boolean results = forwardToFocused(f -> f.mouseReleased(mouseX, mouseY, button));
        super.mouseReleased(mouseX, mouseY, button);
        return results;
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        if (dialog != null && isPresenting()) {
            return dialog.keyPressed(key, p_231046_2_, p_231046_3_);
        }
        IGuiEventListener focused = getFocused();
        if (focused != null) {
            boolean isTyped = focused.keyPressed(key, p_231046_2_, p_231046_3_);
            if (isTyped || (isEditing(focused) && isEditReceivedKey(key))) {
                return isTyped;
            }
        }
        for (IGuiEventListener responder : nextResponder()) {
            if (responder.keyPressed(key, p_231046_2_, p_231046_3_)) {
                return true;
            }
        }
        return super.keyPressed(key, p_231046_2_, p_231046_3_);
    }

    @Override
    public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
        if (dialog != null && isPresenting()) {
            return dialog.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
        }
        for (IGuiEventListener responder : nextResponder()) {
            if (responder.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_)) {
                return true;
            }
        }
        return super.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
    }

    @Override
    public boolean charTyped(char p_231042_1_, int p_231042_2_) {
        if (dialog != null && isPresenting()) {
            return dialog.charTyped(p_231042_1_, p_231042_2_);
        }
        if (super.charTyped(p_231042_1_, p_231042_2_)) {
            return true;
        }
        for (IGuiEventListener responder : nextResponder()) {
            if (responder.charTyped(p_231042_1_, p_231042_2_)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (dialog != null && isPresenting()) {
            return dialog.isMouseOver(mouseX, mouseY);
        }
        if (forwardToFocused(f -> f.isMouseOver(mouseX, mouseY))) {
            return true;
        }
        for (IGuiEventListener responder : nextResponder()) {
            if (responder.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isMouseOver(mouseX, mouseY);
    }

    public boolean isPresenting() {
        return dialog != null;
    }

    public Iterable<IGuiEventListener> nextResponder() {
        return Collections.emptyList();
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

    protected void setFocusedWithResponder(FocusableGui responder) {
        setFocused(responder.getFocused());
    }

    private boolean forwardToFocused(Predicate<IGuiEventListener> consumer) {
        IGuiEventListener focused = getFocused();
        if (focused instanceof Widget) {
            return ((Widget) focused).visible && consumer.test(focused);
        }
        if (focused != null) {
            return consumer.test(focused);
        }
        return false;
    }

    private boolean isEditing(IGuiEventListener listener) {
        if (listener instanceof FocusableGui) {
            IGuiEventListener listener1 = ((FocusableGui) listener).getFocused();
            if (isEditing(listener1)) {
                return true;
            }
        }
        return listener instanceof AWTextField || listener instanceof TextFieldWidget;
    }
    private boolean isEditReceivedKey(int key) {
        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE:
            case GLFW.GLFW_KEY_TAB:
                return false;

            default:
                return true;
        }
    }
}
