package moe.plushie.armourers_workshop.builder.gui.colourmixer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWTextField;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class ColourMixerInputDialog extends ColourMixerConfirmDialog {

    protected String value;
    protected ITextComponent suggestion;
    protected AWTextField textField;

    protected ColourMixerInputDialog(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.textField = addTextField(leftPos + 10, topPos + 30, imageWidth - 20, 20);
    }

    @Override
    public void onConfirm(Button sender) {
        if (textField != null && textField.getValue().isEmpty()) {
            return;
        }
        super.onConfirm(sender);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
        if (this.textField != null) {
            this.textField.render(matrixStack, mouseX, mouseY, p_230430_4_);
        }
        if (this.suggestion != null && this.textField != null && this.textField.getValue().isEmpty()) {
            font.draw(matrixStack, suggestion, textField.x + 4, textField.y + (textField.getHeight() - 8) / 2f, 0x404040);
        }
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        if (textField != null && textField.isFocused() && key == GLFW.GLFW_KEY_ENTER) {
            onConfirm(confirmButton);
            return true;
        }
        return super.keyPressed(key, p_231046_2_, p_231046_3_);
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener p_231035_1_) {
        super.setFocused(p_231035_1_);
    }

    private AWTextField addTextField(int x, int y, int width, int height) {
        AWTextField textBox = new AWTextField(font, x, y, width, height, StringTextComponent.EMPTY);
        textBox.setMaxLength(20);
        if (this.value != null) {
            textBox.setValue(this.value);
            this.value = null;
        }
        addWidget(textBox);
        return textBox;
    }


    public void setText(String value) {
        this.value = value;
        if (textField != null) {
            textField.setValue(value);
        }
    }

    public String getText() {
        if (textField != null) {
            return textField.getValue();
        }
        return null;
    }

    public ITextComponent getSuggestion() {
        return this.textField.getMessage();
    }

    public void setSuggestion(ITextComponent suggestion) {
        this.textField.setMessage(suggestion);
    }
}
