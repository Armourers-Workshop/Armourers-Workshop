package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;

public class FileProviderSettingDialog extends ConfirmDialog {

    private final VerticalStackView stackView = new VerticalStackView(CGRect.ZERO);

    private final SkinProperties properties;

    public FileProviderSettingDialog(SkinProperties properties) {
        super();
        this.setFrame(new CGRect(0, 0, 200, 120));
        this.setTitle(NSString.localizedString("skin-library.dialog.fileProvider.setting"));
        this.properties = properties.copy();
        this.setup(bounds());
    }

    private void setup(CGRect rect) {
        stackView.setFrame(new CGRect(rect.insetBy(30, 10, 40, 10)));
        stackView.setSpacing(4);
        stackView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        addSubview(stackView);

        addCheckBox(SkinProperty.USE_ADAPT_MODE);
        addCheckBox(SkinProperty.USE_ITEM_TRANSFORMS);

        cancelButton.removeFromSuperview();
        confirmButton.setFrame(new CGRect((rect.width - 100) / 2, rect.height - 30, 100, 20));
        confirmButton.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleTopMargin);
    }

    protected void addCheckBox(SkinProperty<Boolean> property) {
        var oldValue = properties.get(property);
        var checkBox = new UICheckBox(new CGRect(0, 0, 80, 9));
        checkBox.setTitle(getDisplayText(property.key()));
        checkBox.setSelected(oldValue);
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, box) -> {
            var value = box.isSelected();
            self.properties.put(property, value);
        });
        stackView.addArrangedSubview(checkBox);
    }

    public SkinProperties properties() {
        return properties;
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return NSString.localizedString("skin-library.dialog.fileProvider.setting." + key, objects);
    }
}
