package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class InputDialog extends ConfirmDialog implements UITextFieldDelegate {

    protected final UITextField textField = new UITextField(new CGRect(10, 30, 100, 20));

    private Predicate<String> verifier;
    private boolean isValidText = true;

    public InputDialog() {
        super();
        this.textField.setMaxLength(48);
        this.textField.setDelegate(this);
        this.textField.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> self.checkValue(self.value()));
        this.addSubview(textField);
        this.setValue("");
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        var rect = bounds();
        textField.setFrame(new CGRect(10, 30, rect.width - 20, 20));
        messageLabel.setFrame(new CGRect(10, 58, rect.width - 20, 20));
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        checkValue(textField.text());
        confirmAction(confirmButton);
        return true;
    }

    public NSString placeholder() {
        return textField.placeholder();
    }

    public void setPlaceholder(NSString placeholder) {
        textField.setPlaceholder(placeholder);
    }

    public String value() {
        return textField.text();
    }

    public void setValue(String value) {
        textField.setText(value);
        checkValue(value);
    }

    public Predicate<String> verifier() {
        return verifier;
    }

    public void setVerifier(Predicate<String> verifier) {
        this.verifier = verifier;
    }

    @Override
    public void confirmAction(UIControl sender) {
        if (textField.text().isEmpty()) {
            return;
        }
        super.confirmAction(sender);
    }

    private void checkValue(String value) {
        if (verifier != null) {
            isValidText = verifier.test(value);
        }
        messageLabel.setHidden(isValidText);
        confirmButton.setEnabled(isValidText);
    }
}
