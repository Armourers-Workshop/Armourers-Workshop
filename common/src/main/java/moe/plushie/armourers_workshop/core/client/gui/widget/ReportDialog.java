package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UITextView;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Collection;

@Environment(EnvType.CLIENT)
public class ReportDialog extends ConfirmDialog {

    protected String content;

    protected UIComboBox comboBox = buildComboBox(0, 0, 100, 20);
    protected UITextView textView = buildTextField(0, 0, 100, 80);

    public ReportDialog() {
        this.setFrame(new CGRect(0, 0, 240, 200));
        this.setup();
    }

    private void setup() {
        float width = bounds().width;

        textView.setFrame(new CGRect(10, 45, width - 20, 80));
        addSubview(textView);

        comboBox.setFrame(new CGRect(10, 25, width - 20, 16));
        addSubview(comboBox);

        messageLabel.setFrame(new CGRect(10, 130, width - 20, 40));
        messageLabel.setTextVerticalAlignment(NSTextAlignment.Vertical.TOP);
    }

    public String getText() {
        if (textView != null) {
            return textView.text();
        }
        return null;
    }

    public void setText(String value) {
        this.content = value;
        if (textView != null) {
            textView.setText(value);
        }
    }

    public NSString placeholder() {
        return textView.placeholder();
    }

    public void setPlaceholder(NSString placeholderText) {
        textView.setPlaceholder(placeholderText);
    }

    public int getReportType() {
        return comboBox.selectedIndex();
    }

    public void setReportTypes(Collection<NSString> types) {
        comboBox.reloadData(ObjectUtils.map(types, UIComboItem::new));
    }

    private UIComboBox buildComboBox(int x, int y, int width, int height) {
        int selectedIndex = 0;
        UIComboBox comboBox = new UIComboBox(new CGRect(x, y, width, height));
        comboBox.setSelectedIndex(selectedIndex);
        addSubview(comboBox);
        return comboBox;
    }

    private UITextView buildTextField(int x, int y, int width, int height) {
        UITextView textBox = new UITextView(new CGRect(x, y, width, height));
        textBox.setMaxLength(255);
        if (this.content != null) {
            textBox.setText(content);
            this.content = null;
        }
        return textBox;
    }
}
