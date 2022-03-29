package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.util.Strings;

public class SkinnableContainer extends Container {

    private final IWorldPosCallable pos;
    private String title;
    private int row;
    private int colum;
    private IInventory inventory;

    public SkinnableContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable worldPos) {
        super(ModContainerTypes.SKINNABLE, containerId);
        this.pos = worldPos;
        SkinnableTileEntity tileEntity = getEntity();
        if (tileEntity == null) {
            return;
        }

        this.title = tileEntity.getInventoryName();

        row = 3;
        colum = 9;
        inventory = playerInventory.player.getEnderChestInventory();

        if (!tileEntity.isEnderInventory()) {
            row = tileEntity.getInventoryHeight();
            colum = tileEntity.getInventoryWidth();
            inventory = tileEntity.getInventory();
        }

        addPlayerSlots(playerInventory, 8, row * 18 + 41);
        addCustomSlots(inventory, 0, 0, colum, row);

        if (inventory != null) {
            inventory.startOpen(playerInventory.player);
        }
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        if (inventory != null) {
            inventory.stopOpen(player);
        }
    }

    protected void addCustomSlots(IInventory inventory, int x, int y, int column, int row) {
        if (inventory == null) {
            return;
        }
        int guiWidth = 176;
        for (int j = 0; j < row; j++) {
            for (int i = 0; i < column; i++) {
                addSlot(new Slot(inventory, x + i + j * column, y + (guiWidth / 2 - (column * 18) / 2) + 1 + 18 * i, 21 + j * 18));
            }
        }
    }

    protected void addPlayerSlots(IInventory inventory, int slotsX, int slotsY) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }

    public SkinnableTileEntity getEntity() {
        TileEntity tileEntity = pos.evaluate(World::getBlockEntity).orElse(null);
        if (tileEntity instanceof SkinnableTileEntity) {
            return (SkinnableTileEntity) tileEntity;
        }
        return null;
    }

    public ITextComponent getTitle() {
        if (Strings.isNotBlank(title)) {
            return new StringTextComponent(title);
        }
        return TranslateUtils.title("inventory.armourers_workshop.skinnable");
    }

    public int getRow() {
        return row;
    }

    public int getColum() {
        return colum;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.pos, player, ModBlocks.SKINNABLE);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (index >= 36) {
            if (!(moveItemStackTo(itemStack, 9, 36, false) || moveItemStackTo(itemStack, 0, 9, false))) {
                return ItemStack.EMPTY;
            }
            slot.setChanged();
            return itemStack.copy();
        }
        if (!moveItemStackTo(itemStack, 36, slots.size(), false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }
}
