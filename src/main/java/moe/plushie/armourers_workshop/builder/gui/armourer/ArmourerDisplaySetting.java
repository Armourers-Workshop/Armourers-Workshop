package moe.plushie.armourers_workshop.builder.gui.armourer;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.core.gui.widget.AWTextField;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;

public class ArmourerDisplaySetting extends ArmourerBaseSetting {

    protected final ArmourerTileEntity tileEntity;

    private AWComboBox comboList;
    private AWTextField textBox;
    private AWLabel inputType;

    private AWCheckBox checkShowGuides;
    private AWCheckBox checkShowHelper;


    private final HashMap<PlayerTextureDescriptor.Source, String> defaultValues = new HashMap<>();

    private PlayerTextureDescriptor lastDescriptor = PlayerTextureDescriptor.EMPTY;
    private PlayerTextureDescriptor.Source lastSource = PlayerTextureDescriptor.Source.NONE;

    public ArmourerDisplaySetting(ArmourerContainer container) {
        super("inventory.armourers_workshop.armourer.displaySettings");
        this.tileEntity = container.getTileEntity(ArmourerTileEntity.class);
        this.reloadData();
    }

    @Override
    protected void init() {
        super.init();

        this.checkShowGuides = new AWCheckBox(leftPos + 10, topPos + 115, 9, 9, getDisplayText("showGuide"), false, this::updateFlagValue);
        this.checkShowHelper = new AWCheckBox(leftPos + 10, topPos + 130, 9, 9, getDisplayText("showHelper"), false, this::updateFlagValue);

        this.addButton(checkShowGuides);
        this.addButton(checkShowHelper);

        this.addLabel(leftPos + 10, topPos + 20, width, 10, getDisplayText("label.skinType"));
        this.inputType = addLabel(leftPos + 10, topPos + 55, width, 10, getDisplayText("label.username"));

        this.addTextField(leftPos + 10, topPos + 65, defaultValues.get(lastSource));
        this.addButton(new ExtendedButton(leftPos + 10, topPos + 90, 100, 20, getDisplayText("set"), this::submit));

        this.addComboList(leftPos + 10, topPos + 30, lastSource);
        this.reloadStatus();
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
        checkShowGuides.setSelected(tileEntity.isShowGuides());
        checkShowHelper.setSelected(tileEntity.isShowHelper());
        checkShowHelper.visible = tileEntity.usesHelper();
        // update input type
        if (lastSource == PlayerTextureDescriptor.Source.URL) {
            inputType.setMessage(getDisplayText("label.url"));
        } else {
            inputType.setMessage(getDisplayText("label.username"));
        }
    }

    private void prepareDefaultValue() {
        defaultValues.clear();
        if (tileEntity != null) {
            lastDescriptor = tileEntity.getTextureDescriptor();
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
        textBox.setFocus(false);
        int index = comboList.getSelectedIndex();
        PlayerTextureDescriptor.Source source = PlayerTextureDescriptor.Source.values()[index + 1];
        applyText(source, textBox.getValue());
    }

    private void changeSource(PlayerTextureDescriptor.Source newSource) {
        if (this.lastSource == newSource) {
            return;
        }
        defaultValues.put(lastSource, textBox.getValue());
        textBox.setValue(defaultValues.getOrDefault(newSource, ""));
        textBox.setFocus(false);
        textBox.moveCursorToStart();
        comboList.setSelectedIndex(newSource.ordinal() - 1);
        lastSource = newSource;
        reloadStatus();
    }

    private void applyText(PlayerTextureDescriptor.Source source, String value) {
        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.EMPTY;
        if (Strings.isNotEmpty(value)) {
            if (source == PlayerTextureDescriptor.Source.URL) {
                descriptor = new PlayerTextureDescriptor(value);
            }
            if (source == PlayerTextureDescriptor.Source.USER) {
                descriptor = new PlayerTextureDescriptor(new GameProfile(null, value));
            }
        }
        PlayerTextureLoader.getInstance().loadTextureDescriptor(descriptor, resolvedDescriptor -> {
            PlayerTextureDescriptor newValue = resolvedDescriptor.orElse(PlayerTextureDescriptor.EMPTY);
            if (lastDescriptor.equals(newValue)) {
                return; // no changes
            }
            lastSource = PlayerTextureDescriptor.Source.NONE;
            lastDescriptor = newValue;
            tileEntity.setTextureDescriptor(newValue);
            UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.TEXTURE_DESCRIPTOR;
            UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, newValue);
            NetworkHandler.getInstance().sendToServer(packet);
            // update to use
            defaultValues.put(newValue.getSource(), newValue.getValue());
            changeSource(newValue.getSource());
        });
    }

    private void updateFlagValue(Button sender) {
        int oldFlags = tileEntity.getFlags();
        tileEntity.setShowGuides(checkShowGuides.isSelected());
        tileEntity.setShowHelper(checkShowHelper.isSelected());
        int flags = tileEntity.getFlags();
        if (flags == oldFlags) {
            return;
        }
        tileEntity.setFlags(flags);
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.FLAGS;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, flags);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void addComboList(int x, int y, PlayerTextureDescriptor.Source source) {
        int selectedIndex = 0;
        if (source != PlayerTextureDescriptor.Source.NONE) {
            selectedIndex = source.ordinal() - 1;
        }
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        items.add(new AWComboBox.ComboItem(getDisplayText("dropdown.user")));
        items.add(new AWComboBox.ComboItem(getDisplayText("dropdown.url")));
        comboList = new AWComboBox(x, y, 80, 14, items, selectedIndex, button -> {
            if (button instanceof AWComboBox) {
                int index = ((AWComboBox) button).getSelectedIndex();
                changeSource(PlayerTextureDescriptor.Source.values()[index + 1]);
            }
        });
        comboList.setPopLevel(200);
        addButton(comboList);
    }

    private void addTextField(int x, int y, String defaultValue) {
        textBox = new AWTextField(font, x, y, 120, 16, StringTextComponent.EMPTY);
        textBox.setMaxLength(1024);
        textBox.setReturnHandler(this::submit);
        if (Strings.isNotBlank(defaultValue)) {
            textBox.setValue(defaultValue);
        }
        addButton(textBox);
    }
}
