package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UIView;
import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.api.data.IDataProperty;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentConnector;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.builder.data.properties.DataProperty;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentSettings;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

public class AdvancedLeftCardPanel extends UIView {

    //private final UIButton saveButton = new UIButton(new CGRect(146, 120, 20, 20));
    private DocumentConnector connector;

    private final UITextField nameTextField = new UITextField(new CGRect(10, 28, 180, 16));
    private final UITextField flavourTextField = new UITextField(new CGRect(10, 60, 180, 16));

    public AdvancedLeftCardPanel(DocumentEditor editor, CGRect frame) {
        super(frame);
        this.connector = editor.getConnector();
        this.setup();
    }

    private void setup() {

        setupLabel(10, 18, "armourer.main.label.itemName");
        setupLabel(10, 50, "armourer.main.label.flavour");

        setupTextField(nameTextField, "", "outfit-maker.skinName");
        setupTextField(flavourTextField, "", "outfit-maker.skinFlavour");

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

        float height = bounds().height;
        float width = bounds().width;
        setupCheckBox(10, height - 30, width - 20, "armourer.displaySettings.showOrigin", connector.registerSettings(DataProperty::new, SkinDocumentSettings::showsOrigin, SkinDocumentSettings::setShowsOrigin));
        setupCheckBox(10, height - 20, width - 20, "armourer.displaySettings.showHelper", connector.registerSettings(DataProperty::new, SkinDocumentSettings::showsHelperModel, SkinDocumentSettings::setShowsHelperModel));

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

    private void setupLabel(int x, int y, String key) {
        UILabel label = new UILabel(new CGRect(x, y, bounds().getWidth(), 9));
        label.setText(NSString.localizedString(key));
        label.setTextColor(UIColor.WHITE);
        addSubview(label);
    }

    private void setupTextField(UITextField textField, String value, String placeholderKey) {
        textField.setMaxLength(40);
        textField.setText(value);
        textField.setPlaceholder(NSString.localizedString(placeholderKey));
//        textField.addTarget(this, UIControl.Event.EDITING_DID_END, OutfitMakerWindow::saveSkinInfo);
        addSubview(textField);
    }

    private void setupCheckBox(float x, float y, float width, String key, IDataProperty<Boolean> property) {
        UICheckBox checkBox = new UICheckBox(new CGRect(x, y, width, 10));
        checkBox.setTitle(NSString.localizedString(key));
        checkBox.setTitleColor(UIColor.WHITE);
        checkBox.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleTopMargin);
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = ObjectUtils.unsafeCast(c);
            property.set(checkBox1.isSelected());
        });
        addSubview(checkBox);
        property.addObserver(checkBox::setSelected);
    }
}
