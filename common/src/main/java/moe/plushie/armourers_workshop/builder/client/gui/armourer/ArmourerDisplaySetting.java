package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class ArmourerDisplaySetting extends ArmourerBaseSetting implements UITextFieldDelegate {

    protected final ArmourerBlockEntity blockEntity;
    private final HashMap<PlayerTextureDescriptor.Source, String> defaultValues = new HashMap<>();

    private final UIComboBox comboList = new UIComboBox(new CGRect(10, 30, 80, 14));

    private final UITextField textBox = new UITextField(new CGRect(10, 65, 120, 16));
    private final UILabel inputType = new UILabel(new CGRect(10, 55, 160, 10));

    private final UICheckBox checkShowGuides = new UICheckBox(new CGRect(10, 115, 160, 9));
    private final UICheckBox checkShowModelGuides = new UICheckBox(new CGRect(10, 130, 160, 9));
    private final UICheckBox checkShowHelper = new UICheckBox(new CGRect(10, 145, 160, 9));

    private PlayerTextureDescriptor lastDescriptor = PlayerTextureDescriptor.EMPTY;
    private PlayerTextureDescriptor.Source lastSource = PlayerTextureDescriptor.Source.NONE;

    public ArmourerDisplaySetting(ArmourerMenu container) {
        super("armourer.displaySettings");
        this.blockEntity = container.getBlockEntity();
        this.reloadData();
    }

    @Override
    public void init() {
        super.init();

        checkShowGuides.setTitle(getDisplayText("showGuide"));
        checkShowGuides.addTarget(this, UIControl.Event.VALUE_CHANGED, ArmourerDisplaySetting::updateFlagValue);
        checkShowModelGuides.setTitle(getDisplayText("showModelGuide"));
        checkShowModelGuides.addTarget(this, UIControl.Event.VALUE_CHANGED, ArmourerDisplaySetting::updateFlagValue);
        checkShowHelper.setTitle(getDisplayText("showHelper"));
        checkShowHelper.addTarget(this, UIControl.Event.VALUE_CHANGED, ArmourerDisplaySetting::updateFlagValue);
        addSubview(checkShowGuides);
        addSubview(checkShowModelGuides);
        addSubview(checkShowHelper);

        UILabel label = new UILabel(new CGRect(10, 20, 160, 10));
        label.setText(getDisplayText("label.skinType"));
        addSubview(label);

        inputType.setText(getDisplayText("label.username"));
        addSubview(inputType);

        String defaultValue = defaultValues.get(lastSource);
        textBox.setMaxLength(1024);
        textBox.setDelegate(this);
        if (Strings.isNotBlank(defaultValue)) {
            textBox.setText(defaultValue);
        }
        addSubview(textBox);

        UIButton loadBtn = new UIButton(new CGRect(10, 90, 100, 20));
        loadBtn.setTitle(getDisplayText("set"), UIControl.State.ALL);
        loadBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        loadBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        loadBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerDisplaySetting::submit);
        addSubview(loadBtn);

        setupComboList(lastSource);

        reloadStatus();
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        submit(textField);
        return true;
    }

    @Override
    public void reloadData() {
        prepareDefaultValue();
        reloadStatus();
    }

    private void reloadStatus() {
        if (checkShowGuides == null) {
            return;
        }
        checkShowGuides.setSelected(blockEntity.isShowGuides());
        checkShowModelGuides.setSelected(blockEntity.isShowModelGuides());
        checkShowHelper.setSelected(blockEntity.isShowHelper());
        checkShowHelper.setHidden(!blockEntity.isUseHelper());
        // update input type
        if (lastSource == PlayerTextureDescriptor.Source.URL) {
            inputType.setText(getDisplayText("label.url"));
        } else {
            inputType.setText(getDisplayText("label.username"));
        }
    }

    private void prepareDefaultValue() {
        defaultValues.clear();
        if (blockEntity != null) {
            lastDescriptor = blockEntity.getTextureDescriptor();
        }
        lastSource = lastDescriptor.getSource();
        if (lastSource == PlayerTextureDescriptor.Source.USER) {
            defaultValues.put(lastSource, lastDescriptor.getName());
        }
        if (lastSource == PlayerTextureDescriptor.Source.URL) {
            defaultValues.put(lastSource, lastDescriptor.getURL());
        }
    }

    private void submit(Object button) {
        textBox.resignFirstResponder();
        int index = comboList.selectedIndex();
        PlayerTextureDescriptor.Source source = PlayerTextureDescriptor.Source.values()[index + 1];
        applyText(source, textBox.text());
    }

    private void changeSource(PlayerTextureDescriptor.Source newSource) {
        if (this.lastSource == newSource) {
            return;
        }
        defaultValues.put(lastSource, textBox.text());
        textBox.setText(defaultValues.getOrDefault(newSource, ""));
        textBox.resignFirstResponder();
        //textBox.moveCursorToStart();
        comboList.setSelectedIndex(newSource.ordinal() - 1);
        lastSource = newSource;
        reloadStatus();
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
            blockEntity.setTextureDescriptor(newValue);
            NetworkManager.sendToServer(UpdateArmourerPacket.Field.TEXTURE_DESCRIPTOR.buildPacket(blockEntity, newValue));
            // update to use
            defaultValues.put(newValue.getSource(), newValue.getValue());
            changeSource(newValue.getSource());
        });
    }

    private void updateFlagValue(UIControl sender) {
        int oldFlags = blockEntity.getFlags();
        blockEntity.setShowGuides(checkShowGuides.isSelected());
        blockEntity.setShowModelGuides(checkShowModelGuides.isSelected());
        blockEntity.setShowHelper(checkShowHelper.isSelected());
        int flags = blockEntity.getFlags();
        if (flags == oldFlags) {
            return;
        }
        blockEntity.setFlags(flags);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.FLAGS.buildPacket(blockEntity, flags));
    }

    private void setupComboList(PlayerTextureDescriptor.Source source) {
        int selectedIndex = 0;
        if (source != PlayerTextureDescriptor.Source.NONE) {
            selectedIndex = source.ordinal() - 1;
        }
        ArrayList<UIComboItem> items = new ArrayList<>();
        items.add(new UIComboItem(getDisplayText("dropdown.user")));
        items.add(new UIComboItem(getDisplayText("dropdown.url")));
        comboList.setSelectedIndex(selectedIndex);
        comboList.reloadData(items);
        comboList.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, ctr) -> {
            int index = ((UIComboBox) ctr).selectedIndex();
            changeSource(PlayerTextureDescriptor.Source.values()[index + 1]);
        });
        addSubview(comboList);
    }
}
