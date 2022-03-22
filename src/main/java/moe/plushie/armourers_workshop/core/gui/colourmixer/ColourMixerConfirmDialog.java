package moe.plushie.armourers_workshop.core.gui.colourmixer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWAbstractDialog;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class ColourMixerConfirmDialog extends AWAbstractDialog {

    protected int selectedIndex = 0;

    protected Button confirmButton;
    protected Button cancelButton;

    protected ITextComponent message;

    protected ColourMixerConfirmDialog(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        int sp = (imageWidth - 100 * 2) / 3;
        this.confirmButton = this.addButton(leftPos + sp, topPos + imageHeight - 30, 100, 20, "button.ok", this::onConfirm);
        this.cancelButton = this.addButton(leftPos + imageWidth - 100 - sp, topPos + imageHeight - 30, 100, 20, "button.cancel", this::onCancel);
    }

    @Override
    public void removed() {
        super.removed();
        this.confirmButton = null;
        this.cancelButton = null;
    }

    public void renderContentLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (message != null) {
            font.drawWordWrap(message, leftPos + 10, topPos + 30, imageWidth - 20, 0xff404040);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
        this.renderContentLayer(matrixStack, mouseX, mouseY);
    }

    public ITextComponent getMessage() {
        return message;
    }

    public void setMessage(ITextComponent message) {
        this.message = message;
    }

    public void onConfirm(Button sender) {
        this.selectedIndex = 1;
        this.onClose();
    }

    public void onCancel(Button sender) {
        this.selectedIndex = 0;
        this.onClose();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private Button addButton(int x, int y, int width, int height, String key, Button.IPressable pressable) {
        TextComponent title = TranslateUtils.title("inventory.armourers_workshop.common." + key);
        Button button = new ExtendedButton(x, y, width, height, title, pressable);
        addButton(button);
        return button;
    }
}
