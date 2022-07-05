package moe.plushie.armourers_workshop.builder.gui.armourer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import moe.plushie.armourers_workshop.builder.tileentity.ArmourerTileEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWHelpButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWSkinTypeComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTextField;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ArmourerMainSetting extends ArmourerBaseSetting {

    protected final ArmourerContainer container;
    protected final ArmourerTileEntity tileEntity;
    protected final ArtifactVersion modVersion;

    protected AWSkinTypeComboBox skinTypeBox;
    protected AWTextField skinName;
    protected AWTextField skinFlavor;

    protected ExtendedButton btnSave;
    protected ExtendedButton btnLoad;

    protected ISkinType skinType = SkinTypes.ARMOR_HEAD;

    protected ArmourerMainSetting(ArmourerContainer container) {
        super("inventory.armourers_workshop.armourer.main");
        this.modVersion = AWCore.getVersion();
        this.container = container;
        this.tileEntity = container.getTileEntity(ArmourerTileEntity.class);
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

    @Override
    protected void init() {
        super.init();

        // TODO Make button icons for save/load buttons.
        // GuiIconButton buttonSave = new GuiIconButton(parent, 13, 88, 16, 16, 16, GuiHelper.getLocalizedControlName(guiName, "save"), TEXTURE_BUTTONS);
        // GuiIconButton buttonLoad = new GuiIconButton(parent, 14, 88, 16 + 13, 16, 16, GuiHelper.getLocalizedControlName(guiName, "load"), TEXTURE_BUTTONS);

        this.btnSave = new ExtendedButton(leftPos + 88, topPos + 16, 50, 12, getDisplayText("save"), this::saveSkin);
        this.btnLoad = new ExtendedButton(leftPos + 88, topPos + 29, 50, 12, getDisplayText("load"), this::loadSkin);
        this.addButton(btnSave);
        this.addButton(btnLoad);

        this.skinName = new AWTextField(font, leftPos + 8, topPos + 58, 158, 16, StringTextComponent.EMPTY);
        this.skinName.setMaxLength(40);
        this.skinName.setEventListener(this::updateSkinPropertiesEvent);
        this.skinName.setReturnHandler(this::updateSkinPropertiesReturn);
        this.addButton(skinName);

        this.skinFlavor = new AWTextField(font, leftPos + 8, topPos + 90, 158, 16, StringTextComponent.EMPTY);
        this.skinFlavor.setMaxLength(40);
        this.skinFlavor.setEventListener(this::updateSkinPropertiesEvent);
        this.skinFlavor.setReturnHandler(this::updateSkinPropertiesReturn);
        this.addButton(skinFlavor);

        this.addLabel(leftPos + 14, topPos + 48, width, 10, getDisplayText("label.itemName"));
        this.addLabel(leftPos + 14, topPos + 80, width, 10, getDisplayText("label.flavour"));
        if (modVersion != null) {
            StringTextComponent text = new StringTextComponent(modVersion.toString());
            int textWidth = font.width(text);
            this.addLabel(leftPos + width - textWidth - 7, topPos + height - 96, textWidth, 9, text);
        }

        this.addHelpButton(leftPos + 6, topPos + 12, "help.skinType");
        this.addHelpButton(leftPos + 6, topPos + 48, "help.itemName");
        this.addHelpButton(leftPos + 6, topPos + 80, "help.itemFlavour");
        this.addHelpButton(leftPos + 81, topPos + 18, "help.save");
        this.addHelpButton(leftPos + 81, topPos + 30, "help.load");

        this.skinTypeBox = new AWSkinTypeComboBox(leftPos + 7, topPos + 21, 50, 16, getSkinTypes(), skinType, this::updateSkinType);
        this.skinTypeBox.setMaxRowCount(8);
        this.skinTypeBox.setPopLevel(200);
        this.addButton(skinTypeBox);

        this.reloadData();
    }

    @Override
    public void reloadData() {
        SkinProperties skinProperties = tileEntity.getSkinProperties();
        this.skinName.setValue(skinProperties.get(SkinProperty.ALL_CUSTOM_NAME));
        this.skinFlavor.setValue(skinProperties.get(SkinProperty.ALL_FLAVOUR_TEXT));
        this.skinTypeBox.setSelectedSkin(tileEntity.getSkinType());
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
        RenderUtils.bind(RenderUtils.TEX_ARMOURER);
        RenderUtils.blit(matrixStack, leftPos + 63, topPos + 20, 238, 0, 18, 18); // input
        RenderUtils.blit(matrixStack, leftPos + 142, topPos + 16, 230, 18, 26, 26); // output
    }

    @Override
    protected void addHoveredButton(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
        if (getFocused() != button && getFocused() != null && getFocused().isMouseOver(mouseX, mouseY)) {
            return;
        }
        super.addHoveredButton(button, matrixStack, mouseX, mouseY);
    }

    protected void addHelpButton(int x, int y, String key) {
        ITextComponent tooltip = getDisplayText(key);
        AWImageButton button = new AWHelpButton(x, y, 7, 8, Objects::hash, this::addHoveredButton, tooltip);
        addButton(button);
    }

    private void updateSkinType(ISkinType skinType) {
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
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void updateSkinProperties() {
        SkinProperties skinProperties = new SkinProperties(tileEntity.getSkinProperties());
        skinProperties.put(SkinProperty.ALL_CUSTOM_NAME, skinName.getValue());
        skinProperties.put(SkinProperty.ALL_FLAVOUR_TEXT, skinFlavor.getValue());
        if (skinProperties.equals(tileEntity.getSkinProperties())) {
            return; // not any changes.
        }
        this.tileEntity.setSkinProperties(skinProperties);
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.SKIN_PROPERTIES;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, skinProperties);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void loadSkin(Button sender) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !container.shouldLoadArmourItem(player)) {
            return;
        }
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_LOAD;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, new CompoundNBT());
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void saveSkin(Button sender) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !container.shouldSaveArmourItem(player)) {
            return;
        }
        GameProfile origin = Minecraft.getInstance().getUser().getGameProfile();
        CompoundNBT nbt = NBTUtil.writeGameProfile(new CompoundNBT(), origin);
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.ITEM_SAVE;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, nbt);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void updateSkinPropertiesReturn(String value) {
        skinName.setFocus(false);
        skinFlavor.setFocus(false);
        setFocused(null);
    }

    private void updateSkinPropertiesEvent(AWTextField textField, AWTextField.EditEvent event) {
        if (event == AWTextField.EditEvent.END) {
            updateSkinProperties();
        }
    }
}
