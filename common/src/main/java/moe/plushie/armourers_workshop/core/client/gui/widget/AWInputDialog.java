package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

@SuppressWarnings("unused")
@Environment(value = EnvType.CLIENT)
public class AWInputDialog extends AWConfirmDialog {

    protected String value;
    protected Predicate<String> valueTester;
    protected boolean isValidText = true;

    protected AWTextField textField = buildTextField(0, 0, 100, 20);

    public AWInputDialog(Component title) {
        super(title);
        textField.setResponder(this::checkValue);
    }

    @Override
    protected void init() {
        super.init();
        addButton(textField);
        textField.x = leftPos + 10;
        textField.y = topPos + 30;
        textField.setWidth(imageWidth - 20);
        messageY = textField.y + textField.getHeight() + 8;
    }

    @Override
    public void removed() {
        super.removed();
        textField = null;
    }

    @Override
    public void onConfirm(Button sender) {
        if (textField != null && textField.getValue().isEmpty()) {
            return;
        }
        super.onConfirm(sender);
    }

    @Override
    public void renderContentLayer(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        if (!this.isValidText) {
            super.renderContentLayer(matrixStack, mouseX, mouseY, p_230430_4_);
        }
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        if (textField != null && textField.isFocused() && key == GLFW.GLFW_KEY_ENTER && isValidText) {
            onConfirm(confirmButton);
            return true;
        }
        return super.keyPressed(key, p_231046_2_, p_231046_3_);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener p_231035_1_) {
        super.setFocused(p_231035_1_);
    }

    public String getText() {
        if (textField != null) {
            return textField.getValue();
        }
        return null;
    }

    public void setText(String value) {
        this.value = value;
        if (textField != null) {
            textField.setValue(value);
        }
    }

    public Component getPlaceholderText() {
        return textField.getMessage();
    }

    public void setPlaceholderText(Component placeholderText) {
        this.textField.setMessage(placeholderText);
    }

    public Predicate<String> getValueTester() {
        return valueTester;
    }

    public void setValueTester(Predicate<String> valueTester) {
        this.valueTester = valueTester;
    }

    private void checkValue(String value) {
        if (valueTester != null) {
            isValidText = valueTester.test(value);
        }
        confirmButton.active = isValidText;
    }

    private AWTextField buildTextField(int x, int y, int width, int height) {
        AWTextField textBox = new AWTextField(Minecraft.getInstance().font, x, y, width, height, TextComponent.EMPTY);
        textBox.setMaxLength(20);
        if (this.value != null) {
            textBox.setValue(this.value);
            this.value = null;
        }
        return textBox;
    }
}
