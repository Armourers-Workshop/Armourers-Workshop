package moe.plushie.armourers_workshop.builder.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.builder.container.OutfitMakerContainer;
import moe.plushie.armourers_workshop.builder.tileentity.OutfitMakerTileEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageExtendedButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWTextField;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateOutfitMakerPacket;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class OutfitMakerScreen extends AWAbstractContainerScreen<OutfitMakerContainer> {

    protected AWTextField textItemName;
    protected AWTextField textItemFlavour;
    protected AWImageButton buttonSave;

    private final OutfitMakerTileEntity tileEntity;

    public OutfitMakerScreen(OutfitMakerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 240;
        this.tileEntity = container.getTileEntity(OutfitMakerTileEntity.class);
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
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
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
        UpdateOutfitMakerPacket packet = new UpdateOutfitMakerPacket(tileEntity, field, value);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    protected void saveSkin(Button sender) {
        if (!menu.shouldCrafting()) {
            return;
        }
        GameProfile origin = Minecraft.getInstance().getUser().getGameProfile();
        CompoundNBT nbt = NBTUtil.writeGameProfile(new CompoundNBT(), origin);
        UpdateOutfitMakerPacket.Field field = UpdateOutfitMakerPacket.Field.ITEM_CRAFTING;
        UpdateOutfitMakerPacket packet = new UpdateOutfitMakerPacket(tileEntity, field, nbt);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    protected AWTextField addTextField(int x, int y, String value, String placeholderKey) {
        AWTextField textBox = new AWTextField(font, x, y, 158, 16, StringTextComponent.EMPTY);
        textBox.setMaxLength(40);
        textBox.setValue(value);
        textBox.setPlaceholder(getDisplayText(placeholderKey));
        textBox.setEventListener(this::saveSkinInfo);
        addButton(textBox);
        return textBox;
    }

    protected ITextComponent getDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.outfit-maker" + "." + key);
    }
}
