package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextRange;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinLoader;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings({"SameParameterValue"})
public class SkinWardrobeTextureSetting extends SkinWardrobeBaseSetting implements UITextFieldDelegate {

    private final SkinWardrobe wardrobe;
    private final HashMap<PlayerSkinDescriptor.Source, String> defaultValues = new HashMap<>();

    private final UITextField textField = new UITextField(new CGRect(83, 70, 165, 18));

    private UIButton confirmView;
    private UIComboBox sourceComboView;
    private UIComboBox modelComboView;

    private PlayerSkinDescriptor lastDescriptor = PlayerSkinDescriptor.DEFAULT;
    private PlayerSkinModel lastTextureModel = PlayerSkinModel.WIDE;
    private PlayerSkinDescriptor.Source lastTextureSource = PlayerSkinDescriptor.Source.USER;

    public SkinWardrobeTextureSetting(SkinWardrobe wardrobe) {
        super("wardrobe.man_texture");
        this.wardrobe = wardrobe;
        this.prepareDefaultValue();
        this.setup();
    }

    private void setup() {
        setupTextField();
        confirmView = addCommonButton(83, 90, 100, 20, "set", this::submit);
        sourceComboView = addComboBox(83, 27, 80, 14, "textureSource", lastTextureSource, this::applyTextureSource);
        modelComboView = addComboBox(168, 27, 80, 14, "textureModel", lastTextureModel, this::applyTextureModel);
    }

    public void setupTextField() {
        textField.setDelegate(this);
        textField.setMaxLength(1024);
        textField.setText(Objects.compactMap(defaultValues.get(lastTextureSource), ""));
        addSubview(textField);
    }

    private void prepareDefaultValue() {
        if (!(wardrobe.entity() instanceof MannequinEntity entity)) {
            return;
        }
        lastDescriptor = entity.getTextureDescriptor();
        lastTextureModel = entity.getTextureDescriptor().model();
        lastTextureSource = lastDescriptor.source().orElse(PlayerSkinDescriptor.Source.USER);
        // reset the default value.
        defaultValues.clear();
        defaultValues.put(lastTextureSource, lastDescriptor.value().orElse(""));
    }

    private void applyTextureSource(PlayerSkinDescriptor.Source newValue) {
        if (lastTextureSource == newValue) {
            return;
        }
        defaultValues.put(lastTextureSource, textField.text());
        textField.setText(Objects.compactMap(defaultValues.get(newValue), ""));
        textField.resignFirstResponder();
        textField.setSelectedTextRange(new NSTextRange(textField.beginOfDocument()));
        sourceComboView.setSelectedIndex(newValue.ordinal());
        lastTextureSource = newValue;
    }

    private void applyTextureModel(PlayerSkinModel newValue) {
        if (lastTextureModel == newValue) {
            return;
        }
        var newDescriptor = lastDescriptor.withModel(newValue);
        modelComboView.setSelectedIndex(newValue.ordinal());
        NetworkManager.sendToServer(UpdateWardrobePacket.Field.MANNEQUIN_TEXTURE.buildPacket(wardrobe, newDescriptor));
        lastDescriptor = newDescriptor;
        lastTextureModel = newValue;
    }

    private void submit(Object button) {
        textField.resignFirstResponder();
        confirmView.setEnabled(false);
        // load texture info and then update to entity.
        var newDescriptor = createTextureDescriptor().withModel(lastTextureModel);
        PlayerSkinLoader.getInstance().loadSkin(newDescriptor, (skin, exception) -> {
            confirmView.setEnabled(true);
            if (skin == null) {
                UserNotificationCenter.showToast(exception, NSString.localizedString("common.text.error"), null);
                return;
            }
            var newValue = skin.descriptor();
            if (lastDescriptor.equals(newValue)) {
                return; // no changes
            }
            lastDescriptor = newValue;
            lastTextureSource = null; // set to null, and the immediately update it.
            NetworkManager.sendToServer(UpdateWardrobePacket.Field.MANNEQUIN_TEXTURE.buildPacket(wardrobe, newValue));
            // update to use
            var newSource = newValue.source().orElse(PlayerSkinDescriptor.Source.USER);
            defaultValues.put(newSource, newValue.value().orElse(""));
            applyTextureSource(newSource);
        });
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        submit(textField.text());
        return true;
    }

    private <T extends Enum<T>> UIComboBox addComboBox(float x, float y, float width, float height, String key, T defaultValue, Consumer<T> applier) {
        var values = Collections.newList(defaultValue.getClass().getEnumConstants());
        var comboView = new UIComboBox(new CGRect(x, y, width, height));
        comboView.setSelectedIndex(values.indexOf(defaultValue));
        comboView.reloadData(Collections.compactMap(values, value -> {
            var name = value.name().toLowerCase();
            return new UIComboItem(getDisplayText(key + "." + name));
        }));
        comboView.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, e) -> {
            var newValue = ((UIComboBox) e).selectedIndex();
            applier.accept(Objects.unsafeCast(values.get(newValue)));
        });
        addSubview(comboView);
        return comboView;
    }

    private UIButton addCommonButton(float x, float y, float width, float height, String key, Consumer<UIControl> handler) {
        var button = new UIButton(new CGRect(x, y, width, height));
        button.setTitle(getDisplayText(key), UIControl.State.ALL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, (self, e) -> handler.accept(e));
        addSubview(button);
        return button;
    }

    private PlayerSkinDescriptor createTextureDescriptor() {
        var value = Objects.compactMap(textField.text(), "");
        var userType = PlayerSkinDescriptor.Source.values()[sourceComboView.selectedIndex()];
        if (userType == PlayerSkinDescriptor.Source.URL) {
            return PlayerSkinDescriptor.fromURL(value);
        }
        if (userType == PlayerSkinDescriptor.Source.USER) {
            return PlayerSkinDescriptor.fromName(value);
        }
        return PlayerSkinDescriptor.DEFAULT;
    }
}
