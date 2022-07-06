package moe.plushie.armourers_workshop.builder.gui.armourer.dialog;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.core.container.AbstractContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWCheckBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWConfirmDialog;
import moe.plushie.armourers_workshop.core.gui.widget.AWHelpButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class ArmourerReplaceDialog extends AWConfirmDialog {

    PlayerEntity player;
    Inventory inventory;

    PickerScreen pickerScreen;
    Button lastHoveredButton;

    AWCheckBox keepPaintBox;
    AWCheckBox keepColorBox;

    public ArmourerReplaceDialog(ITextComponent title) {
        super(title);
        this.player = Objects.requireNonNull(Minecraft.getInstance().player);
        this.inventory = createBackup(player.inventory);
        this.pickerScreen = new PickerScreen(new PickerContainer(inventory), player.inventory, StringTextComponent.EMPTY);
        this.imageWidth = 240;
        this.imageHeight = 130;
    }

    private Inventory createBackup(PlayerInventory inventory) {
        int size = inventory.getContainerSize();
        Inventory newInventory = new Inventory(size + 2);
        for (int i = 0; i < size; ++i) {
            newInventory.setItem(i, inventory.getItem(i).copy());
        }
        return newInventory;
    }

    @Override
    protected void init() {
        int realHeight = height;
        this.height = realHeight - 98;
        super.init();
        this.height = realHeight;

        int leftX = confirmButton.x + 1;
        int centerX = confirmButton.x + 111;
        int bottom = confirmButton.y - 4;

        this.keepColorBox = new AWCheckBox(leftX, bottom - 22, 9, 9, getText("keepColor"), false, Objects::hash);
        this.keepPaintBox = new AWCheckBox(leftX, bottom - 11, 9, 9, getText("keepPaint"), false, Objects::hash);

        this.addButton(keepPaintBox);
        this.addButton(keepColorBox);

        this.addLabel(leftX + 8, topPos + 25, 100, 9, getText("srcBlock"));
        this.addLabel(centerX + 8, topPos + 25, 100, 9, getText("desBlock"));
        this.addHelpButton(leftX, topPos + 25, "help.selector");
        this.addHelpButton(centerX, topPos + 25, "help.applier");

        this.pickerScreen.inventoryX = (width - 176) / 2;
        this.pickerScreen.inventoryY = (height - 98);
        this.pickerScreen.placeholderX = leftX + 32;
        this.pickerScreen.placeholderY = topPos + 44;

        this.pickerScreen.init(Minecraft.getInstance(), width, height);
        this.addWidget(pickerScreen);
    }

    @Override
    public void removed() {
        super.removed();
        this.pickerScreen.removed();
    }

    public boolean isKeepColor() {
        return keepColorBox.isSelected();
    }

    public boolean isKeepPaintType() {
        return keepPaintBox.isSelected();
    }

    public ItemStack getSelector() {
        return inventory.getItem(inventory.getContainerSize() - 2);
    }

    public ItemStack getApplier() {
        return inventory.getItem(inventory.getContainerSize() - 1);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0, -200);
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
        matrixStack.popPose();
        this.pickerScreen.render(matrixStack, mouseX, mouseY, p_230430_4_);
        if (this.lastHoveredButton != null) {
            this.renderTooltip(matrixStack, lastHoveredButton.getMessage(), mouseX, mouseY);
            this.lastHoveredButton = null;
        }
    }

    protected void addHoveredButton(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
        this.lastHoveredButton = button;
    }

    protected void addHelpButton(int x, int y, String key) {
        ITextComponent tooltip = getText(key);
        AWImageButton button = new AWHelpButton(x, y, 7, 8, Objects::hash, this::addHoveredButton, tooltip);
        addButton(button);
    }

    private ITextComponent getText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.replace" + "." + key);
    }

    static class PickerContainer extends AbstractContainer {

        Inventory inventory;

        protected PickerContainer(Inventory inventory) {
            super(null, 0);
            this.inventory = inventory;
            this.reload(0, 0, 0, 0);
        }

        @Override
        public boolean stillValid(PlayerEntity p_75145_1_) {
            return true;
        }

        @Override
        public ItemStack quickMoveStack(PlayerEntity player, int index) {
            return quickMoveStack(player, index, slots.size());
        }

        public void reload(int inventoryX, int inventoryY, int placeholderX, int placeholderY) {
            slots.clear();
            addPlayerSlots(inventory, inventoryX, inventoryY);
            addPlaceholderSlots(inventory, inventory.getContainerSize() - 2, placeholderX, placeholderY);
        }

        protected void addPlayerSlots(IInventory inventory, int x, int y) {
            int slotsX = x + 8;
            int slotsY = y + 16;
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col, slotsX + col * 18, slotsY + 58));
            }
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlot(new Slot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
                }
            }
        }

        protected void addPlaceholderSlots(IInventory inventory, int offset, int placeholderX, int placeholderY) {
            for (int i = 0; i < 2; ++i) {
                addSlot(new Slot(inventory, offset + i, placeholderX + i * 110, placeholderY) {
                    @Override
                    public boolean mayPlace(ItemStack itemStack) {
                        return itemStack.getItem() instanceof IItemColorProvider;
                    }
                });
            }
        }
    }

    static class PickerScreen extends ContainerScreen<PickerContainer> {

        int inventoryX;
        int inventoryY;
        int placeholderX;
        int placeholderY;

        public PickerScreen(PickerContainer p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
            super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
            this.imageWidth = 240;
            this.imageHeight = 240;
        }

        @Override
        protected void init() {
            this.topPos = 0;
            this.leftPos = 0;
            this.menu.reload(inventoryX, inventoryY, placeholderX, placeholderY);
        }

        @Override
        public void removed() {
            this.inventory.setCarried(ItemStack.EMPTY);
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
            super.render(matrixStack, mouseX, mouseY, p_230430_4_);
            this.renderTooltip(matrixStack, mouseX, mouseY);
        }

        @Override
        protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
            RenderUtils.blit(matrixStack, inventoryX, inventoryY, 0, 0, 176, 98, RenderUtils.TEX_PLAYER_INVENTORY);
            RenderUtils.bind(RenderUtils.TEX_ARMOURER);
            RenderUtils.blit(matrixStack, placeholderX - 5, placeholderY - 5, 230, 18, 26, 26); // input
            RenderUtils.blit(matrixStack, placeholderX - 5 + 110, placeholderY - 5, 230, 18, 26, 26); // replaced
        }

        @Override
        protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
            font.draw(matrixStack, inventory.getDisplayName(), inventoryX + 8, inventoryY + 5, 0x404040);
        }

        protected void slotClicked(@Nullable Slot slot, int slotIndex, int p_184098_3_, ClickType clickType) {
            if (slot == null) {
                return;
            }
            menu.clicked(slot.index, p_184098_3_, clickType, inventory.player);
        }
    }
}
