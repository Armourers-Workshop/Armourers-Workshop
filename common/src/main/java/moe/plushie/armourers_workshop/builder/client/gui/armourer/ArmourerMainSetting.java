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
import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinComboBox;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ArmourerMainSetting extends ArmourerBaseSetting implements UITextFieldDelegate {

    private static final List<ISkinType> SUPPORTED_SKIN_TYPES = new ImmutableList.Builder<ISkinType>()
            .add(SkinTypes.ARMOR_HEAD)
            .add(SkinTypes.ARMOR_CHEST)
            .add(SkinTypes.ARMOR_LEGS)
            .add(SkinTypes.ARMOR_FEET)
            .add(SkinTypes.ARMOR_WINGS)
            .add(SkinTypes.ITEM_SWORD)
            .add(SkinTypes.ITEM_SHIELD)
            .add(SkinTypes.ITEM_BOW)
            .add(SkinTypes.ITEM_TRIDENT)
            .add(SkinTypes.ITEM_PICKAXE)
            .add(SkinTypes.ITEM_AXE)
            .add(SkinTypes.ITEM_SHOVEL)
            .add(SkinTypes.ITEM_HOE)
            .add(SkinTypes.ITEM)
            .add(SkinTypes.BLOCK)
            .add(SkinTypes.ADVANCED)
            .build();

    private final UITextField nameTextField = new UITextField(new CGRect(8, 58, 158, 16));
    private final UITextField flavorTextField = new UITextField(new CGRect(8, 90, 158, 16));

    private final SkinComboBox skinTypeBox = new SkinComboBox(new CGRect(7, 21, 50, 16));

    protected final ArmourerMenu container;
    protected final ArmourerBlockEntity blockEntity;
    protected final String modVersion;

    protected ISkinType skinType = SkinTypes.ARMOR_HEAD;

    protected ArmourerMainSetting(ArmourerMenu container) {
        super("armourer.main");
        this.modVersion = EnvironmentManager.getVersion();
        this.container = container;
        this.blockEntity = container.getBlockEntity();
        if (this.blockEntity != null) {
            this.skinType = blockEntity.getSkinType();
        }
    }

    public void init() {
        // TODO Make button icons for save/load buttons.
        // GuiIconButton buttonSave = new GuiIconButton(parent, 13, 88, 16, 16, 16, GuiHelper.getLocalizedControlName(guiName, "save"), TEXTURE_BUTTONS);
        // GuiIconButton buttonLoad = new GuiIconButton(parent, 14, 88, 16 + 13, 16, 16, GuiHelper.getLocalizedControlName(guiName, "load"), TEXTURE_BUTTONS);

        UIButton saveBtn = new UIButton(new CGRect(88, 16, 50, 12));
        saveBtn.setTitle(getDisplayText("save"), UIControl.State.ALL);
        saveBtn.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        saveBtn.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        saveBtn.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ArmourerMainSetting::saveSkin);
        addSubview(saveBtn);

        UIButton loadBtn = new UIButton(new CGRect(88, 29, 50, 12));
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
            UILabel modLabel = new UILabel(new CGRect(8, 131, 160, 9));
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

        UIImageView slot1 = new UIImageView(new CGRect(63, 20, 18, 18));
        UIImageView slot2 = new UIImageView(new CGRect(142, 16, 26, 26));
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
        SkinProperties skinProperties = blockEntity.getSkinProperties();
        nameTextField.setText(skinProperties.get(SkinProperty.ALL_CUSTOM_NAME));
        flavorTextField.setText(skinProperties.get(SkinProperty.ALL_FLAVOUR_TEXT));
        skinTypeBox.setSelectedSkin(blockEntity.getSkinType());
    }

    private void setupLabel(int x, int y, NSString text) {
        UILabel label = new UILabel(new CGRect(x, y, bounds().getWidth(), 9));
        label.setText(text);
        addSubview(label);
    }

    private void setupHelpView(int x, int y, String key) {
        UIButton button = new UIButton(new CGRect(x, y, 7, 8));
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
        var newValue = blockEntity.getSkinProperties().copy();
        newValue.put(SkinProperty.ALL_CUSTOM_NAME, nameTextField.text());
        newValue.put(SkinProperty.ALL_FLAVOUR_TEXT, flavorTextField.text());
        if (newValue.equals(blockEntity.getSkinProperties())) {
            return; // not any changes.
        }
        blockEntity.setSkinProperties(newValue);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.SKIN_PROPERTIES.buildPacket(blockEntity, newValue));
    }

    private void loadSkin(UIControl sender) {
        var player = EnvironmentManager.getPlayer();
        if (player == null || !container.shouldLoadArmourItem(player)) {
            return;
        }
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.ITEM_LOAD.buildPacket(blockEntity, new CompoundTag()));
    }

    private void saveSkin(UIControl sender) {
        Player player = EnvironmentManager.getPlayer();
        if (player == null || !container.shouldSaveArmourItem(player)) {
            return;
        }
        GameProfile origin = Minecraft.getInstance().getUser().getGameProfile();
        CompoundTag nbt = DataSerializers.writeGameProfile(new CompoundTag(), origin);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.ITEM_SAVE.buildPacket(blockEntity, nbt));
    }

    private void updateSkinPropertiesReturn() {
        nameTextField.resignFirstResponder();
        flavorTextField.resignFirstResponder();
    }
}
