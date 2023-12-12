package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UIView;
import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentConnector;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.utils.TranslateUtils;

public class AdvancedLeftCardPanel extends UIView {

//    private final UIButton saveButton = new UIButton(new CGRect(146, 120, 20, 20));
    private DocumentConnector connector;

    private final UITextField nameTextField = new UITextField(new CGRect(10, 28, 180, 16));
    private final UITextField flavourTextField = new UITextField(new CGRect(10, 60, 180, 16));

    public AdvancedLeftCardPanel(DocumentEditor editor, CGRect frame) {
        super(frame);
        this.connector = editor.getConnector();
        this.setup();
    }

    private void setup() {

        setupLabel(10, 18, getDisplayText("label.itemName"));
        setupLabel(10, 50, getDisplayText("label.flavour"));

        setupTextField(nameTextField, "", "skinName");
        setupTextField(flavourTextField, "", "skinFlavour");

        nameTextField.addTarget(connector.itemName, UIControl.Event.EDITING_DID_BEGIN, (it, ctr) -> it.beginEditing());
        nameTextField.addTarget(connector.itemName, UIControl.Event.EDITING_DID_END, (it, ctr) -> it.endEditing());
        nameTextField.addTarget(connector.itemName, UIControl.Event.VALUE_CHANGED, (it, ctr) -> {
            UITextField textField = (UITextField) ctr;
            it.set(textField.text());
        });

        flavourTextField.addTarget(connector.itemFlavour, UIControl.Event.EDITING_DID_BEGIN, (it, ctr) -> it.beginEditing());
        flavourTextField.addTarget(connector.itemFlavour, UIControl.Event.EDITING_DID_END, (it, ctr) -> it.endEditing());
        flavourTextField.addTarget(connector.itemFlavour, UIControl.Event.VALUE_CHANGED, (it, ctr) -> {
            UITextField textField = (UITextField) ctr;
            it.set(textField.text());
        });

        connector.itemName.addObserver((newValue) -> {
            String oldValue = nameTextField.text();
            if (!Objects.equal(oldValue, newValue)) {
                nameTextField.setText(newValue);
            }
        });
        connector.itemFlavour.addObserver((newValue) -> {
            String oldValue = flavourTextField.text();
            if (!Objects.equal(oldValue, newValue)) {
                flavourTextField.setText(newValue);
            }
        });
    }

    private void setupLabel(int x, int y, NSString text) {
        UILabel label = new UILabel(new CGRect(x, y, bounds().getWidth(), 9));
        label.setText(text);
        label.setTextColor(UIColor.WHITE);
        addSubview(label);
    }

    private void setupTextField(UITextField textField, String value, String placeholderKey) {
        textField.setMaxLength(40);
        textField.setText(value);
        textField.setPlaceholder(getDisplayText(placeholderKey));
//        textField.addTarget(this, UIControl.Event.EDITING_DID_END, OutfitMakerWindow::saveSkinInfo);
        addSubview(textField);
    }

    private NSString getDisplayText(String key) {
        return new NSString(TranslateUtils.title("inventory.armourers_workshop.armourer.main" + "." + key));
    }
}
