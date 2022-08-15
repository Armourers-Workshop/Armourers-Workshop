package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.util.Strings;

public class SkinnableMenu extends AbstractBlockContainerMenu {

    private final String title;
    private int row;
    private int colum;
    private Container inventory;

    public SkinnableMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, ContainerLevelAccess worldPos) {
        super(menuType, block, containerId, worldPos);
        SkinnableBlockEntity tileEntity = getTileEntity();
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
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (inventory != null) {
            inventory.stopOpen(player);
        }
    }

    protected void addCustomSlots(Container inventory, int x, int y, int column, int row) {
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

    protected void addPlayerSlots(Container inventory, int slotsX, int slotsY) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }

    public Component getInventoryName() {
        if (Strings.isNotBlank(title)) {
            return TranslateUtils.literal(title);
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
