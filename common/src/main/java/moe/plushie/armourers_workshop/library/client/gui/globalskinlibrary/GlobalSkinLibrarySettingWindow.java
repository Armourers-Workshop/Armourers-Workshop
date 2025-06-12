package moe.plushie.armourers_workshop.library.client.gui.globalskinlibrary;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.init.ModMenuOptions;

public class GlobalSkinLibrarySettingWindow extends ConfirmDialog {

    private static SkinProperties OPTIONS;


    private int contentHeight = 24; // 24 + n + 8

    private final SkinProperties properties;

    public GlobalSkinLibrarySettingWindow() {
        super();
        this.properties = changes().copy();
        this.setup();
        this.contentHeight += 6;
    }

    public static boolean hasChanges() {
        return !changes().isEmpty();
    }

    public static void setChanges(SkinProperties properties) {
        var newProperties = properties.copy();
        ModMenuOptions.getInstance().putTag("library.uploadFileOptions", newProperties.serializeNBT());
        OPTIONS = newProperties;
    }

    public static SkinProperties changes() {
        if (OPTIONS == null) {
            var tag = ModMenuOptions.getInstance().getTag("library.uploadFileOptions");
            if (tag != null) {
                OPTIONS = new SkinProperties(tag);
            } else {
                OPTIONS = new SkinProperties();
            }
        }
        return OPTIONS;
    }

    public static SkinFileOptions fileOptions() {
        var values = changes().copy();
        var options = new SkinFileOptions();
        options.setEditable(values.get(Option.IS_EDITABLE));
        options.setSavable(values.get(Option.IS_SAVABLE));
        options.setExportable(values.get(Option.IS_EXPORTABLE));
        if (!values.isEmpty()) {
            options.setFileVersion(SkinSerializer.Versions.LATEST);
        }
        return options;
    }


    private void setup() {
        messageLabel.removeFromSuperview();

        // [x] Use 3.0 file format?
        // [x] Use readonly mode of the skin?
        // [x] Use encrypt of the skin?
        //   [ Password ] [              ]
        addOptionView(Option.NEW_FORMAT_MODE);
        addOptionView(Option.IS_EDITABLE);
        addOptionView(Option.IS_SAVABLE);
        addOptionView(Option.IS_EXPORTABLE);
    }

    private UICheckBox addOptionView(Option<Boolean> property) {
        var checkBox = new UICheckBox(new CGRect(8, contentHeight, bounds().width() - 16, 9));
        checkBox.setTitle(NSString.localizedString("skin-library.setting." + property.key()));
        checkBox.setSelected(properties.get(property));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
            properties.put(property, sender.isSelected());
        });
        checkBox.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        addSubview(checkBox);
        contentHeight += 9 + 4;
        return checkBox;
    }

    @Override
    public void sizeToFit() {
        setBounds(new CGRect(0, 0, bounds().width(), contentHeight + 30));
    }

    public SkinProperties properties() {
        return properties;
    }

    public static class Option<T> extends SkinProperty<T> {

        public static final Option<Boolean> NEW_FORMAT_MODE = normal("newFormatMode", false);

        public static final Option<Boolean> IS_EDITABLE = normal("isEditable", true);
        public static final Option<Boolean> IS_SAVABLE = normal("isSavable", true);
        public static final Option<Boolean> IS_EXPORTABLE = normal("isExportable", true);

        public Option(String key, T defaultValue) {
            super(key, defaultValue, false);
        }

        public static <T> Option<T> normal(String key, T defaultValue) {
            return new Option<>(key, defaultValue);
        }
    }
}
