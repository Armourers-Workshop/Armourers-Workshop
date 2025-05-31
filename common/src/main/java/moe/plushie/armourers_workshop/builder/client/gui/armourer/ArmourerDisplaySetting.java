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
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ArmourerDisplaySetting extends ArmourerBaseSetting implements UITextFieldDelegate {

    protected final ArmourerBlockEntity blockEntity;
    private final HashMap<EntityTextureDescriptor.Source, String> defaultValues = new HashMap<>();

    private final UILabel inputType = new UILabel(new CGRect(10, 85, 160, 10));
    private final UITextField textField = new UITextField(new CGRect(10, 95, 120, 16));

    private final UICheckBox checkShowGuides = new UICheckBox(new CGRect(10, 145, 160, 9));
    private final UICheckBox checkShowModelGuides = new UICheckBox(new CGRect(10, 160, 160, 9));
    private final UICheckBox checkShowHelper = new UICheckBox(new CGRect(10, 145, 190, 9));

    private UIComboBox sourceComboView;
    private UIComboBox modelComboView;

    private EntityTextureDescriptor lastDescriptor = EntityTextureDescriptor.EMPTY;
    private EntityTextureDescriptor.Model lastTextureModel = EntityTextureDescriptor.Model.STEVE;
    private EntityTextureDescriptor.Source lastTextureSource = null;

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

        inputType.setText(getDisplayText("label.username"));
        addSubview(inputType);


        var defaultValue = defaultValues.get(lastTextureSource);
        textField.setMaxLength(1024);
        textField.setDelegate(this);
        if (Strings.isNotBlank(defaultValue)) {
            textField.setText(defaultValue);
        }
        addSubview(textField);

        addCommonButton(10, 120, 100, 20, "set", this::submit);

        var label2 = new UILabel(new CGRect(10, 50, 160, 10));
        label2.setText(getDisplayText("label.textureSource"));
        addSubview(label2);
        sourceComboView = addComboBox(10, 60, 80, 14, "textureSource", lastTextureSource, this::applyTextureSource);

        var label1 = new UILabel(new CGRect(10, 20, 160, 10));
        label1.setText(getDisplayText("label.textureModel"));
        addSubview(label1);
        modelComboView = addComboBox(10, 30, 80, 14, "textureModel", lastTextureModel, this::applyTextureModel);

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
        if (lastTextureSource == EntityTextureDescriptor.Source.URL) {
            inputType.setText(getDisplayText("label.url"));
        } else {
            inputType.setText(getDisplayText("label.username"));
        }
    }

    private void prepareDefaultValue() {
        defaultValues.clear();
        if (blockEntity != null) {
            lastDescriptor = blockEntity.getTextureDescriptor();
            lastTextureModel = blockEntity.getTextureModel();
        }
        lastTextureSource = lastDescriptor.getSource();
        defaultValues.put(lastTextureSource, lastDescriptor.getValue());
    }

    private void submit(Object button) {
        textField.resignFirstResponder();
        // load texture info and then update to entity.
        PlayerTextureLoader.getInstance().loadTextureDescriptor(getTextureDescriptor(), resolvedDescriptor -> {
            var newValue = resolvedDescriptor.orElse(EntityTextureDescriptor.EMPTY);
            if (lastDescriptor.equals(newValue)) {
                return; // no changes
            }
            lastDescriptor = newValue;
            lastTextureSource = null;
            blockEntity.setTextureDescriptor(newValue);
            NetworkManager.sendToServer(UpdateArmourerPacket.Field.TEXTURE_DESCRIPTOR.buildPacket(blockEntity, newValue));
            // update to use
            var newTexutreSource = newValue.getSource();
            var newTextureValue = Objects.flatMap(newValue, EntityTextureDescriptor::getValue, "");
            defaultValues.put(newTexutreSource, newTextureValue);
            applyTextureSource(newTexutreSource);
        });
    }

    private void applyTextureSource(EntityTextureDescriptor.Source newValue) {
        if (lastTextureSource == newValue) {
            return;
        }
        defaultValues.put(lastTextureSource, textField.text());
        textField.setText(Objects.flatMap(defaultValues.get(newValue), it -> it, ""));
        textField.resignFirstResponder();
        //textBox.moveCursorToStart();
        sourceComboView.setSelectedIndex(newValue.ordinal());
        lastTextureSource = newValue;
        reloadStatus();
    }

    private void applyTextureModel(EntityTextureDescriptor.Model newValue) {
        if (lastTextureModel == newValue) {
            return;
        }
        modelComboView.setSelectedIndex(newValue.ordinal());
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.TEXTURE_MODEL.buildPacket(blockEntity, newValue));
        lastTextureModel = newValue;
    }

    private void updateFlagValue(UIControl sender) {
        var oldFlags = blockEntity.getFlags();
        blockEntity.setShowGuides(checkShowGuides.isSelected());
        blockEntity.setShowModelGuides(checkShowModelGuides.isSelected());
        blockEntity.setShowHelper(checkShowHelper.isSelected());
        var flags = blockEntity.getFlags();
        if (flags == oldFlags) {
            return;
        }
        blockEntity.setFlags(flags);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.FLAGS.buildPacket(blockEntity, flags));
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

    private EntityTextureDescriptor getTextureDescriptor() {
        var value = textField.text();
        if (Strings.isNotEmpty(value)) {
            var userType = EntityTextureDescriptor.Source.values()[sourceComboView.selectedIndex()];
            if (userType == EntityTextureDescriptor.Source.URL) {
                return EntityTextureDescriptor.fromURL(value);
            }
            if (userType == EntityTextureDescriptor.Source.USER) {
                return EntityTextureDescriptor.fromName(value);
            }
        }
        return EntityTextureDescriptor.EMPTY;
    }
}
