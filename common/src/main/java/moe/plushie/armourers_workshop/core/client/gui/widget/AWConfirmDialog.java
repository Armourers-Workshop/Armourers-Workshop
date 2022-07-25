package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
@Environment(value = EnvType.CLIENT)
public class AWConfirmDialog extends AWAbstractDialog {

    protected int selectedIndex = 0;

    protected int messageX = 0;
    protected int messageY = 0;
    protected int messageWidth = 0;
    protected int messageHeight = 0;

    protected Button confirmButton = buildButton(0, 0, 100, 20, "button.ok", this::onConfirm);
    protected Button cancelButton = buildButton(0, 0, 100, 20, "button.cancel", this::onCancel);

    protected Component message;
    protected int messageColor = 0xff404040;

    public AWConfirmDialog(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        int sp = (imageWidth - 100 * 2) / 3;
        this.addButton(confirmButton);
        this.addButton(cancelButton);
        this.confirmButton.x = leftPos + sp;
        this.confirmButton.y = topPos + imageHeight - 30;
        this.cancelButton.x = leftPos + imageWidth - 100 - sp;
        this.cancelButton.y = topPos + imageHeight - 30;
        this.messageX = leftPos + 10;
        this.messageY = topPos + 30;
        this.messageWidth = imageWidth - 20;
        this.messageHeight = 20;
    }

    @Override
    public void removed() {
        super.removed();
        this.confirmButton = null;
        this.cancelButton = null;
    }

    public void renderContentLayer(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        if (message != null) {
            RenderUtils.drawText(matrixStack, font, message, messageX, messageY, messageWidth, 0, messageColor);
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
        this.renderContentLayer(matrixStack, mouseX, mouseY, p_230430_4_);
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public int getMessageColor() {
        return messageColor;
    }

    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }

    public Component getConfirmText() {
        return confirmButton.getMessage();
    }

    public void setConfirmText(Component message) {
        confirmButton.setMessage(message);
    }

    public Component getCancelText() {
        return cancelButton.getMessage();
    }

    public void setCancelText(Component message) {
        cancelButton.setMessage(message);
    }

    public void onConfirm(Button sender) {
        this.selectedIndex = 1;
        this.onClose();
    }

    public void onCancel(Button sender) {
        this.selectedIndex = 0;
        this.onClose();
    }

    public boolean isCancelled() {
        return selectedIndex == 0;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    protected AWLabel addLabel(int x, int y, int width, int height, Component message) {
        AWLabel label = new AWLabel(x, y, width, height, message);
        label.setTextColor(4210752);
        label.active = false;
        addButton(label);
        return label;
    }

    protected Button buildButton(int x, int y, int width, int height, String key, Button.OnPress pressable) {
        Component title = TranslateUtils.title("inventory.armourers_workshop.common." + key);
        return new AWExtendedButton(x, y, width, height, title, pressable);
    }
}
