package moe.plushie.armourers_workshop.core.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class AWReportDialog extends AWConfirmDialog {

    protected String content;

    protected AWComboBox comboBox = buildComboBox(0, 0, 100, 20);
    protected AWTextField textField = buildTextField(0, 0, 100, 80);

    public AWReportDialog(ITextComponent title) {
        super(title);
        this.imageWidth = 240;
        this.imageHeight = 200;
    }

    @Override
    protected void init() {
        super.init();

        this.addButton(textField);
        this.textField.setFrame(leftPos + 10, topPos + 45, imageWidth - 20, 80);

        this.addButton(comboBox);
        this.comboBox.setFrame(leftPos + 10, topPos + 25, imageWidth - 20, 16);

        this.messageY = topPos + 130;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (comboBox.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public String getText() {
        if (textField != null) {
            return textField.getValue();
        }
        return null;
    }

    public void setText(String value) {
        this.content = value;
        if (textField != null) {
            textField.setValue(value);
        }
    }

    public ITextComponent getPlaceholderText() {
        return textField.getMessage();
    }

    public void setPlaceholderText(ITextComponent placeholderText) {
        this.textField.setMessage(placeholderText);
    }

    public int getReportType() {
        return comboBox.getSelectedIndex();
    }

    public void setReportTypes(Collection<ITextComponent> types) {
        List<AWComboBox.ComboItem> items = types.stream().map(AWComboBox.ComboItem::new).collect(Collectors.toList());
        comboBox.setItems(items);
    }

    private AWComboBox buildComboBox(int x, int y, int width, int height) {
        int selectedIndex = 0;
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        AWComboBox comboBox = new AWComboBox(x, y, width, height, items, selectedIndex, Objects::hash);
        comboBox.setPopLevel(200);
        addButton(comboBox);
        return comboBox;
    }

    private AWTextField buildTextField(int x, int y, int width, int height) {
        AWTextField textBox = new AWTextField(Minecraft.getInstance().font, x, y, width, height, StringTextComponent.EMPTY);
        textBox.setMaxLength(255);
        textBox.setSingleLine(false);
        if (this.content != null) {
            textBox.setValue(this.content);
            this.content = null;
        }
        return textBox;
    }
}
