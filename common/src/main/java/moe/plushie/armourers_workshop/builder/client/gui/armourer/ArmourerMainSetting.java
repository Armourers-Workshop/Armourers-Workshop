package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinComboBox;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;

public class ArmourerMainSetting extends ArmourerBaseSetting implements UITextFieldDelegate {

    private static final List<SkinType> SUPPORTED_SKIN_TYPES = Collections.immutableList(builder -> {
        builder.add(SkinTypes.ARMOR_HEAD);
        builder.add(SkinTypes.ARMOR_CHEST);
        builder.add(SkinTypes.ARMOR_LEGS);
        builder.add(SkinTypes.ARMOR_FEET);
        builder.add(SkinTypes.ARMOR_WINGS);
        builder.add(SkinTypes.ITEM_SWORD);
        builder.add(SkinTypes.ITEM_SHIELD);
        builder.add(SkinTypes.ITEM_BOW);
        builder.add(SkinTypes.ITEM_TRIDENT);
        builder.add(SkinTypes.ITEM_PICKAXE);
        builder.add(SkinTypes.ITEM_AXE);
        builder.add(SkinTypes.ITEM_SHOVEL);
        builder.add(SkinTypes.ITEM_HOE);
        builder.add(SkinTypes.ITEM);
        builder.add(SkinTypes.BLOCK);
        builder.add(SkinTypes.ADVANCED);
    });

    private final UITextField nameTextField = new UITextField(new CGRect(8, 58, 158, 16));
    private final UITextField flavorTextField = new UITextField(new CGRect(8, 90, 158, 16));

    private final SkinComboBox skinTypeBox = new SkinComboBox(new CGRect(7, 21, 50, 16));

    protected final ArmourerMenu container;
    protected final ArmourerBlockEntity blockEntity;
    protected final String modVersion;

    protected SkinType skinType = SkinTypes.ARMOR_HEAD;

    protected ArmourerMainSetting(ArmourerMenu container) {
        super("armourer.main");
        this.modVersion = EnvironmentManager.getModVersion(ModConstants.MOD_ID);
        this.container = container;
        this.blockEntity = container.getBlockEntity();
        if (this.blockEntity != null) {
            this.skinType = blockEntity.skinType();
        }
    }

    public void init() {
        // TODO Make button icons for save/load buttons.
        // GuiIconButton buttonSave = new GuiIconButton(parent, 13, 88, 16, 16, 16, GuiHelper.getLocalizedControlName(guiName, "save"), TEXTURE_BUTTONS);
        // GuiIconButton buttonLoad = new GuiIconButton(parent, 14, 88, 16 + 13, 16, 16, GuiHelper.getLocalizedControlName(guiName, "load"), TEXTURE_BUTTONS);

        var saveBtn = new UIButton(new CGRect(88, 16, 50, 12));
        saveBtn.setTitle(getDisplayText("save"), UIControl.State.ALL);
        saveBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        saveBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        saveBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerMainSetting::saveSkin);
        addSubview(saveBtn);

        var loadBtn = new UIButton(new CGRect(88, 29, 50, 12));
        loadBtn.setTitle(getDisplayText("load"), UIControl.State.ALL);
        loadBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        loadBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        loadBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerMainSetting::loadSkin);
        addSubview(loadBtn);

        nameTextField.setMaxLength(40);
        nameTextField.setDelegate(this);
        addSubview(nameTextField);

        flavorTextField.setMaxLength(40);
        flavorTextField.setDelegate(this);
        addSubview(flavorTextField);

        setupLabel(14, 48, getDisplayText("label.itemName"));
        setupLabel(14, 80, getDisplayText("label.flavour"));
        if (modVersion != null) {
            var modLabel = new UILabel(new CGRect(8, 131, 160, 9));
            modLabel.setText(new NSString(modVersion));
            modLabel.setTextHorizontalAlignment(NSTextAlignment.Horizontal.RIGHT);
            addSubview(modLabel);
        }

        setupHelpView(6, 12, "help.skinType");
        setupHelpView(6, 48, "help.itemName");
        setupHelpView(6, 80, "help.itemFlavour");
        setupHelpView(81, 18, "help.save");
        setupHelpView(81, 30, "help.load");

        skinTypeBox.setMaxRows(7);
        skinTypeBox.reloadSkins(SUPPORTED_SKIN_TYPES);
        skinTypeBox.setSelectedSkin(skinType);
        skinTypeBox.addTarget(this, UIControl.Event.VALUE_CHANGED, ArmourerMainSetting::updateSkinType);
        addSubview(skinTypeBox);

        var slot1 = new UIImageView(new CGRect(63, 20, 18, 18));
        var slot2 = new UIImageView(new CGRect(142, 16, 26, 26));
        slot1.setImage(UIImage.of(ModTextures.ARMOURER).uv(238, 0).build());
        slot2.setImage(UIImage.of(ModTextures.ARMOURER).uv(230, 18).build());
        insertViewAtIndex(slot1, 0);
        insertViewAtIndex(slot2, 0);

        reloadData();
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        updateSkinPropertiesReturn();
        return true;
    }

    @Override
    public void textFieldDidEndEditing(UITextField textField) {
        updateSkinProperties();
    }

    @Override
    public void reloadData() {
        var skinProperties = blockEntity.skinProperties();
        nameTextField.setText(skinProperties.get(SkinProperty.ALL_CUSTOM_NAME));
        flavorTextField.setText(skinProperties.get(SkinProperty.ALL_FLAVOUR_TEXT));
        skinTypeBox.setSelectedSkin(blockEntity.skinType());
    }

    private void setupLabel(int x, int y, NSString text) {
        var label = new UILabel(new CGRect(x, y, bounds().width(), 9));
        label.setText(text);
        addSubview(label);
    }

    private void setupHelpView(int x, int y, String key) {
        var button = new UIButton(new CGRect(x, y, 7, 8));
        button.setBackgroundImage(ModTextures.helpButtonImage(), UIControl.State.ALL);
        button.setTooltip(getDisplayText(key));
        button.setCanBecomeFocused(false);
        addSubview(button);
    }

    private void updateSkinType(UIControl control) {
        var skinType = skinTypeBox.selectedSkin();
        if (Objects.equals(skinType, this.skinType)) {
            return; // no changes
        }
        this.skinType = skinType;
        if (this.blockEntity == null) {
            return;
        }
        blockEntity.setSkinType(skinType);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.SKIN_TYPE.buildPacket(blockEntity, skinType));
    }

    private void updateSkinProperties() {
        var newValue = blockEntity.skinProperties().copy();
        newValue.put(SkinProperty.ALL_CUSTOM_NAME, nameTextField.text());
        newValue.put(SkinProperty.ALL_FLAVOUR_TEXT, flavorTextField.text());
        if (newValue.equals(blockEntity.skinProperties())) {
            return; // not any changes.
        }
        blockEntity.setSkinProperties(newValue);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.SKIN_PROPERTIES.buildPacket(blockEntity, newValue));
    }

    private void loadSkin(UIControl sender) {
        var player = Minecraft.getInstance().player;
        if (player == null || !container.shouldLoadArmourItem(player)) {
            return;
        }
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.ITEM_LOAD.buildPacket(blockEntity, new CompoundTag()));
    }

    private void saveSkin(UIControl sender) {
        var player = Minecraft.getInstance().player;
        if (player == null || !container.shouldSaveArmourItem(player)) {
            return;
        }
        var origin = Minecraft.getInstance().getUser().getGameProfile();
        var serializer = new TagSerializer();
        serializer.encode(ExtraCodecs.GAME_PROFILE, origin);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.ITEM_SAVE.buildPacket(blockEntity, serializer.tag()));
    }

    private void updateSkinPropertiesReturn() {
        nameTextField.resignFirstResponder();
        flavorTextField.resignFirstResponder();
    }
}
