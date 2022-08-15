package moe.plushie.armourers_workshop.builder.client.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.builder.blockentity.OutfitMakerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.OutfitMakerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateOutfitMakerPacket;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWImageExtendedButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTextField;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;

import java.util.Objects;

@Environment(value = EnvType.CLIENT)
public class OutfitMakerScreen extends AWAbstractContainerScreen<OutfitMakerMenu> {

    private final OutfitMakerBlockEntity tileEntity;
    protected AWTextField textItemName;
    protected AWTextField textItemFlavour;
    protected AWImageButton buttonSave;

    public OutfitMakerScreen(OutfitMakerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 240;
        this.tileEntity = container.getTileEntity();
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 7;

        this.inventoryLabelX = 8;
        this.inventoryLabelY = imageHeight - 96;

        this.textItemName = this.addTextField(leftPos + 8, topPos + 18, tileEntity.getItemName(), "skinName");
        this.textItemFlavour = this.addTextField(leftPos + 8, topPos + 38, tileEntity.getItemFlavour(), "skinFlavour");

        this.buttonSave = new AWImageExtendedButton(leftPos + 146, topPos + 120, 20, 20, 176, 224, RenderUtils.TEX_OUTFIT_MAKER, this::saveSkin, this::addHoveredButton, getDisplayText("save"));
        this.buttonSave.setIconSize(16, 16);
        this.addButton(buttonSave);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, RenderUtils.TEX_OUTFIT_MAKER);
    }

    protected void saveSkinInfo(AWTextField textField, AWTextField.EditEvent event) {
        if (event != AWTextField.EditEvent.END) {
            return;
        }
        UpdateOutfitMakerPacket.Field field = UpdateOutfitMakerPacket.Field.ITEM_NAME;
        if (textField == textItemFlavour) {
            field = UpdateOutfitMakerPacket.Field.ITEM_FLAVOUR;
        }
        String value = textField.getValue();
        if (Objects.equals(value, field.get(tileEntity))) {
            return; // ignore when value not changes
        }
        NetworkManager.sendToServer(new UpdateOutfitMakerPacket(tileEntity, field, value));
    }

    protected void saveSkin(Button sender) {
        if (!menu.shouldCrafting()) {
            return;
        }
        GameProfile origin = Minecraft.getInstance().getUser().getGameProfile();
        CompoundTag nbt = DataSerializers.writeGameProfile(new CompoundTag(), origin);
        UpdateOutfitMakerPacket.Field field = UpdateOutfitMakerPacket.Field.ITEM_CRAFTING;
        NetworkManager.sendToServer(new UpdateOutfitMakerPacket(tileEntity, field, nbt));
    }

    protected AWTextField addTextField(int x, int y, String value, String placeholderKey) {
        AWTextField textBox = new AWTextField(font, x, y, 158, 16, TextComponent.EMPTY);
        textBox.setMaxLength(40);
        textBox.setValue(value);
        textBox.setPlaceholder(getDisplayText(placeholderKey));
        textBox.setEventListener(this::saveSkinInfo);
        addButton(textBox);
        return textBox;
    }

    protected Component getDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.outfit-maker" + "." + key);
    }

    @Override
    public boolean shouldRenderPluginScreen() {
        return true;
    }
}
