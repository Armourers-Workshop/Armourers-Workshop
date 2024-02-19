package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSTextRange;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class SkinWardrobeTextureSetting extends SkinWardrobeBaseSetting implements UITextFieldDelegate {

    private final SkinWardrobe wardrobe;
    private final HashMap<PlayerTextureDescriptor.Source, String> defaultValues = new HashMap<>();

    private final UIComboBox comboView = new UIComboBox(new CGRect(83, 27, 80, 14));
    private final UITextField textField = new UITextField(new CGRect(83, 70, 165, 18));

    private PlayerTextureDescriptor lastDescriptor = PlayerTextureDescriptor.EMPTY;
    private PlayerTextureDescriptor.Source lastSource = PlayerTextureDescriptor.Source.NONE;

    public SkinWardrobeTextureSetting(SkinWardrobe wardrobe) {
        super("wardrobe.man_texture");
        this.wardrobe = wardrobe;
        this.prepareDefaultValue();
        this.setup();
    }

    private void setup() {
        setupTextField();
        UIButton button = new UIButton(new CGRect(83, 90, 100, 20));
        button.setTitle(getDisplayText("set"), UIControl.State.ALL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinWardrobeTextureSetting::submit);
        addSubview(button);
        setupComboView();
    }

    private void setupComboView() {
        int selectedIndex = 0;
        if (lastSource != PlayerTextureDescriptor.Source.NONE) {
            selectedIndex = lastSource.ordinal() - 1;
        }
        ArrayList<UIComboItem> items = new ArrayList<>();
        items.add(new UIComboItem(getDisplayText("dropdown.user")));
        items.add(new UIComboItem(getDisplayText("dropdown.url")));
        comboView.setSelectedIndex(selectedIndex);
        comboView.reloadData(items);
        comboView.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, e) -> {
            int index = self.comboView.selectedIndex();
            self.changeSource(PlayerTextureDescriptor.Source.values()[index + 1]);
        });
        addSubview(comboView);
    }

    public void setupTextField() {
        String defaultValue = defaultValues.get(lastSource);
        textField.setDelegate(this);
        textField.setMaxLength(1024);
        if (Strings.isNotBlank(defaultValue)) {
            textField.setText(defaultValue);
        }
        addSubview(textField);
    }

    private void prepareDefaultValue() {
        MannequinEntity entity = ObjectUtils.safeCast(wardrobe.getEntity(), MannequinEntity.class);
        if (entity == null) {
            return;
        }
        defaultValues.clear();
        lastDescriptor = entity.getEntityData().get(MannequinEntity.DATA_TEXTURE);
        lastSource = lastDescriptor.getSource();
        if (lastSource == PlayerTextureDescriptor.Source.USER) {
            defaultValues.put(lastSource, lastDescriptor.getName());
        }
        if (lastSource == PlayerTextureDescriptor.Source.URL) {
            defaultValues.put(lastSource, lastDescriptor.getURL());
        }
    }

    private void submit(Object button) {
        textField.resignFirstResponder();
        int index = comboView.selectedIndex();
        PlayerTextureDescriptor.Source source = PlayerTextureDescriptor.Source.values()[index + 1];
        applyText(source, textField.text());
    }

    private void changeSource(PlayerTextureDescriptor.Source newSource) {
        if (this.lastSource == newSource) {
            return;
        }
        defaultValues.put(lastSource, textField.text());
        textField.setText(defaultValues.getOrDefault(newSource, ""));
        textField.resignFirstResponder();
        textField.setSelectedTextRange(new NSTextRange(textField.beginOfDocument()));
        comboView.setSelectedIndex(newSource.ordinal() - 1);
        lastSource = newSource;
    }

    private void applyText(PlayerTextureDescriptor.Source source, String value) {
        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.EMPTY;
        if (Strings.isNotEmpty(value)) {
            if (source == PlayerTextureDescriptor.Source.URL) {
                descriptor = PlayerTextureDescriptor.fromURL(value);
            }
            if (source == PlayerTextureDescriptor.Source.USER) {
                descriptor = PlayerTextureDescriptor.fromName(value);
            }
        }
        PlayerTextureLoader.getInstance().loadTextureDescriptor(descriptor, resolvedDescriptor -> {
            PlayerTextureDescriptor newValue = resolvedDescriptor.orElse(PlayerTextureDescriptor.EMPTY);
            if (lastDescriptor.equals(newValue)) {
                return; // no changes
            }
            lastSource = PlayerTextureDescriptor.Source.NONE;
            lastDescriptor = newValue;
            UpdateWardrobePacket packet = UpdateWardrobePacket.field(wardrobe, UpdateWardrobePacket.Field.MANNEQUIN_TEXTURE, newValue);
            NetworkManager.sendToServer(packet);
            // update to use
            defaultValues.put(newValue.getSource(), newValue.getValue());
            changeSource(newValue.getSource());
        });
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        submit(textField.text());
        return true;
    }
}
