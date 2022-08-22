package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(value = EnvType.CLIENT)
public class ArmourerMainSetting extends ArmourerBaseSetting implements UITextFieldDelegate {

    private final UITextField nameTextField = new UITextField(new CGRect(8, 58, 158, 16));
    private final UITextField flavorTextField = new UITextField(new CGRect(8, 90, 158, 16));

    private final SkinComboBox skinTypeBox = new SkinComboBox(new CGRect(7, 21, 50, 16));

    protected final ArmourerMenu container;
    protected final ArmourerBlockEntity tileEntity;
    protected final String modVersion;

    protected ISkinType skinType = SkinTypes.ARMOR_HEAD;

    protected ArmourerMainSetting(ArmourerMenu container) {
        super("inventory.armourers_workshop.armourer.main");
        this.modVersion = EnvironmentManager.getVersion();
        this.container = container;
        this.tileEntity = container.getTileEntity();
        if (this.tileEntity != null) {
            this.skinType = tileEntity.getSkinType();
        }
    }

    private static List<ISkinType> getSkinTypes() {
        ArrayList<ISkinType> skinTypes = new ArrayList<>();
        for (ISkinType skinType : SkinTypes.values()) {
            if (skinType != SkinTypes.UNKNOWN && skinType != SkinTypes.OUTFIT) {
                skinTypes.add(skinType);
            }
        }
        return skinTypes;
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
        skinTypeBox.reloadSkins(getSkinTypes());
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
        SkinProperties skinProperties = tileEntity.getSkinProperties();
        nameTextField.setValue(skinProperties.get(SkinProperty.ALL_CUSTOM_NAME));
        flavorTextField.setValue(skinProperties.get(SkinProperty.ALL_FLAVOUR_TEXT));
        skinTypeBox.setSelectedSkin(tileEntity.getSkinType());
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
        addSubview(button);
    }

    private void updateSkinType(UIControl control) {
        ISkinType skinType = skinTypeBox.selectedSkin();;
        if (Objects.equals(skinType, this.skinType)) {
            return; // no changes
        }
        this.skinType = skinType;
        if (this.tileEntity == null) {
            return;
        }
        this.tileEntity.setSkinType(skinType);
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.SKIN_TYPE;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, skinType);
        NetworkManager.sendToServer(packet);
    }

    private void updateSkinProperties() {
        SkinProperties skinProperties = new SkinProperties(tileEntity.getSkinProperties());
        skinProperties.put(SkinProperty.ALL_CUSTOM_NAME, nameTextField.value());
        skinProperties.put(SkinProperty.ALL_FLAVOUR_TEXT, flavorTextField.value());
        if (skinProperties.equals(tileEntity.getSkinProperties())) {
            return; // not any changes.
        }
        this.tileEntity.setSkinProperties(skinProperties);
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.SKIN_PROPERTIES;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, skinProperties);
        NetworkManager.sendToServer(packet);
    }

    private void loadSkin(UIControl sender) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !container.shouldLoadArmourItem(player)) {
            return;
        }
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_LOAD;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, new CompoundTag());
        NetworkManager.sendToServer(packet);
    }

    private void saveSkin(UIControl sender) {
        Player player = Minecraft.getInstance().player;
        if (player == null || !container.shouldSaveArmourItem(player)) {
            return;
        }
        GameProfile origin = Minecraft.getInstance().getUser().getGameProfile();
        CompoundTag nbt = DataSerializers.writeGameProfile(new CompoundTag(), origin);
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_SAVE;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, nbt);
        NetworkManager.sendToServer(packet);
    }

    private void updateSkinPropertiesReturn() {
        nameTextField.resignFirstResponder();
        flavorTextField.resignFirstResponder();
    }
}
