package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class SkinnableContainer extends AbstractBlockContainer<Block> {

    private String title;
    private int row;
    private int colum;
    private IInventory inventory;

    public SkinnableContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable worldPos) {
        super(containerId, ModContainerTypes.SKINNABLE, ModBlocks.SKINNABLE, worldPos);
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
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return quickMoveStack(player, index, slots.size());
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
        TileEntity tileEntity = getTileEntity();
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

}
