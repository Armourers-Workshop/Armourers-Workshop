package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerSlot;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.utils.Strings;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

public class SkinnableMenu extends BlockEntityContainerMenu<SkinnableBlockEntity> {

    private final String title;
    private int row;
    private int column;
    private Container inventory;

    public SkinnableMenu(IMenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos worldPos) {
        super(menuType, block, containerId, worldPos);
        this.title = blockEntity.getInventoryName();

        row = 3;
        column = 9;
        inventory = playerInventory.player.getEnderChestInventory();

        if (!blockEntity.isEnderInventory()) {
            row = blockEntity.getInventoryHeight();
            column = blockEntity.getInventoryWidth();
            inventory = blockEntity.getInventory();
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
    protected void abi$removed(Player player) {
        super.abi$removed(player);
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
                addSlot(new AbstractContainerSlot(inventory, i + j * column, x + 18 * i + 1, y + j * 18 + 1));
            }
        }
    }

    public Component name() {
        if (Strings.isNotBlank(title)) {
            return TranslateUtils.formatted(title);
        }
        return Component.translatable("inventory.armourers_workshop.skinnable");
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }
}
