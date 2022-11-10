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
    private int column;
    private Container inventory;

    public SkinnableMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, ContainerLevelAccess worldPos) {
        super(menuType, block, containerId, worldPos);
        SkinnableBlockEntity tileEntity = getTileEntity();
        this.title = tileEntity.getInventoryName();

        row = 3;
        column = 9;
        inventory = playerInventory.player.getEnderChestInventory();

        if (!tileEntity.isEnderInventory()) {
            row = tileEntity.getInventoryHeight();
            column = tileEntity.getInventoryWidth();
            inventory = tileEntity.getInventory();
        }

        // guiHeight = top + row * 18 + middle + 98 = row * 18 + 124
        int guiTop = 20;
        int guiMiddle = 6;
        int guiWidth = 176;
        addPlayerSlots(playerInventory, 8, guiTop + row * 18 + guiMiddle + 16); // 16: inventory name
        addCustomSlots(inventory, (guiWidth - (column * 18)) / 2, guiTop);

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

    protected void addCustomSlots(Container inventory, int x, int y) {
        if (inventory == null) {
            return;
        }
        for (int j = 0; j < row; j++) {
            for (int i = 0; i < column; i++) {
                addSlot(new Slot(inventory, i + j * column, x + 18 * i + 1, y + j * 18 + 1));
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

    public int getColumn() {
        return column;
    }
}
