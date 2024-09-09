package moe.plushie.armourers_workshop.library.client.gui.skinlibrary;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UITextField;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.init.ModMenuOptions;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

public class SkinLibrarySettingWindow extends ConfirmDialog {

    private static SkinProperties OPTIONS;


    private int contentHeight = 24; // 24 + n + 8

    private final SkinProperties properties;

    public SkinLibrarySettingWindow() {
        super();
        this.properties = getChanges().copy();
        this.setup();
        this.contentHeight += 6;
    }

    public static boolean hasChanges() {
        return !getChanges().isEmpty();
    }

    public static void setChanges(SkinProperties properties) {
        var newProperties = properties.copy();
        ModMenuOptions.getInstance().putTag("library.fileOptions", newProperties.serializeNBT());
        OPTIONS = newProperties;
    }

    public static SkinProperties getChanges() {
        if (OPTIONS == null) {
            var tag = ModMenuOptions.getInstance().getTag("library.fileOptions");
            if (tag != null) {
                OPTIONS = new SkinProperties(tag);
            } else {
                OPTIONS = new SkinProperties();
            }
        }
        return OPTIONS;
    }

    public static SkinFileOptions getFileOptions() {
        var values = getChanges().copy();
        var options = new SkinFileOptions();
        options.setEditable(values.get(Option.IS_EDITABLE));
        options.setSavable(values.get(Option.IS_SAVABLE));
        options.setExportable(values.get(Option.IS_EXPORTABLE));
        options.setCompressed(values.get(Option.IS_COMPRESSED));
        if (values.get(Option.IS_ENCRYPTED)) {
            var selectedIndex = values.get(Option.ENCRYPTED_MODE);
            var algorithms = SkinLibraryKeychainWindow.Algorithm.values();
            var algorithm = algorithms[selectedIndex % algorithms.length];
            var key = algorithm.key(values.get(Option.ENCRYPTED_KEY));
            options.setSecurityData(algorithm.signature(key));
            options.setSecurityKey(key);
        } else {
            values.remove(Option.ENCRYPTED_MODE);
            values.remove(Option.ENCRYPTED_KEY);
        }
        if (!values.isEmpty()) {
            options.setFileVersion(SkinSerializer.Versions.V20);
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
        addOptionView(Option.IS_COMPRESSED);
        addOptionView(Option.IS_ENCRYPTED);
        addEncryptView();
    }

    private UICheckBox addOptionView(Option<Boolean> property) {
        var checkBox = new UICheckBox(new CGRect(8, contentHeight, bounds().getWidth() - 16, 9));
        checkBox.setTitle(NSString.localizedString("skin-library.setting." + property.getKey()));
        checkBox.setSelected(properties.get(property));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
            properties.put(property, sender.isSelected());
        });
        checkBox.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        addSubview(checkBox);
        contentHeight += 9 + 4;
        return checkBox;
    }

    private void addEncryptView() {
        var modes = ObjectUtils.map(SkinLibraryKeychainWindow.Algorithm.values(), it -> it.method() + "Mode");
        int defaultSelectedIndex = properties.get(Option.ENCRYPTED_MODE);
        if (defaultSelectedIndex >= modes.size()) {
            defaultSelectedIndex = 0;
        }
        float width = bounds().getWidth();
        var comboBox = new UIComboBox(new CGRect(16, contentHeight, 80, 20));
        var textBox = new UITextField(new CGRect(104, contentHeight, width - 104 - 16, 20));
        comboBox.setSelectedIndex(defaultSelectedIndex);
        comboBox.reloadData(ObjectUtils.map(modes, it -> new UIComboItem(NSString.localizedString("skin-library.setting." + it))));
        comboBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
            var selectedIndex1 = comboBox.selectedIndex();
            properties.put(Option.ENCRYPTED_MODE, selectedIndex1);
            textBox.setText("");
            textBox.setPlaceholder(NSString.localizedString("skin-library.setting." + modes[selectedIndex1] + ".enterText"));
        });
        addSubview(comboBox);
        textBox.setText(properties.get(Option.ENCRYPTED_KEY));
        textBox.setPlaceholder(NSString.localizedString("skin-library.setting." + modes[defaultSelectedIndex] + ".enterText"));
        textBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
            properties.put(Option.ENCRYPTED_KEY, textBox.text());
        });
        addSubview(textBox);
        contentHeight += 20 + 4;
    }

    @Override
    public void sizeToFit() {
        setBounds(new CGRect(0, 0, bounds().getWidth(), contentHeight + 30));
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public static class Option<T> extends SkinProperty<T> {

        public static final Option<Boolean> NEW_FORMAT_MODE = normal("newFormatMode", false);

        public static final Option<Boolean> IS_EDITABLE = normal("isEditable", true);
        public static final Option<Boolean> IS_SAVABLE = normal("isSavable", true);
        public static final Option<Boolean> IS_EXPORTABLE = normal("isExportable", true);

        public static final Option<Boolean> IS_COMPRESSED = normal("isCompressed", false);
        public static final Option<Boolean> IS_ENCRYPTED = normal("isEncrypted", false);

        public static final Option<Integer> ENCRYPTED_MODE = normal("encryptedMode", 0);
        public static final Option<String> ENCRYPTED_KEY = normal("encryptedKey", "");

        public Option(String key, T defaultValue) {
            super(key, defaultValue, false);
        }

        public static <T> Option<T> normal(String key, T defaultValue) {
            return new Option<>(key, defaultValue);
        }
    }
}
