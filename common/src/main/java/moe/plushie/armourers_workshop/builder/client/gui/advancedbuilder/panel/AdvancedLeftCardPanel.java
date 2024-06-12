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
import moe.plushie.armourers_workshop.utils.ObjectUtils;

public class AdvancedLeftCardPanel extends UIView {

    //private final UIButton saveButton = new UIButton(new CGRect(146, 120, 20, 20));
    private final DocumentConnector connector;

    public AdvancedLeftCardPanel(DocumentEditor editor, CGRect frame) {
        super(frame);
        this.connector = editor.getConnector();
        this.setup(bounds());
    }

    private void setup(CGRect rect) {
        float width = rect.width;
        float height = rect.height;

        setupLabel(10, 18, "armourer.main.label.itemName");
        setupLabel(10, 50, "armourer.main.label.flavour");

        setupTextField(10, 28, width - 20, "outfit-maker.skinName", connector.itemName);
        setupTextField(10, 60, width - 20, "outfit-maker.skinFlavour", connector.itemFlavour);

        setupCheckBox(10, height - 30, width - 20, "armourer.displaySettings.showOrigin", connector.showOrigin);
        setupCheckBox(10, height - 20, width - 20, "armourer.displaySettings.showHelper", connector.showHelperModel);
    }

    private void setupLabel(int x, int y, String key) {
        UILabel label = new UILabel(new CGRect(x, y, bounds().getWidth(), 9));
        label.setText(NSString.localizedString(key));
        label.setTextColor(UIColor.WHITE);
        addSubview(label);
    }

    private void setupTextField(float x, float y, float width, String placeholderKey, DataProperty<String> property) {
        UITextField textField = new UITextField(new CGRect(x, y, width, 16));
        textField.setMaxLength(40);
        textField.setText("");
        textField.setPlaceholder(NSString.localizedString(placeholderKey));
        textField.addTarget(property, UIControl.Event.EDITING_DID_BEGIN, DataProperty::beginEditing);
        textField.addTarget(property, UIControl.Event.EDITING_DID_END, DataProperty::endEditing);
        textField.addTarget(property, UIControl.Event.VALUE_CHANGED, (it, ctr) -> {
            UITextField textField1 = (UITextField) ctr;
            it.set(textField1.text());
        });
        property.addObserver((newValue) -> {
            String oldValue = textField.text();
            if (!Objects.equal(oldValue, newValue)) {
                textField.setText(newValue);
            }
        });
        addSubview(textField);
    }

    private void setupCheckBox(float x, float y, float width, String key, IDataProperty<Boolean> property) {
        UICheckBox checkBox = new UICheckBox(new CGRect(x, y, width, 10));
        checkBox.setTitle(NSString.localizedString(key));
        checkBox.setTitleColor(UIColor.WHITE);
        checkBox.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleTopMargin);
        checkBox.setSelected(property.getOrDefault(false));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = ObjectUtils.unsafeCast(c);
            property.set(checkBox1.isSelected());
        });
        addSubview(checkBox);
        property.addObserver(checkBox::setSelected);
    }
}
