package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IContainerLevelAccess;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class HologramProjectorMenu extends AbstractBlockEntityMenu<HologramProjectorBlockEntity> {

    private final Inventory playerInventory;
    private final Container inventory;
    private int group;

    public HologramProjectorMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IContainerLevelAccess worldPos) {
        super(menuType, block, containerId, worldPos);
        this.playerInventory = playerInventory;
        this.inventory = blockEntity.getInventory();
        this.reload(0, 0, 240, 240);
    }

    public void reload(int inventoryX, int inventoryY, int width, int height) {
        slots.clear();
        addPlayerSlots(playerInventory, inventoryX + 8, inventoryY + 16, visibleSlotBuilder(this::shouldRenderInventory));
        if (inventory != null) {
            addSkinSlots(inventory, 1, width, height);
        }
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size());
    }

    public boolean shouldRenderInventory() {
        return getGroup() == 1;
    }

    protected void addSkinSlots(Container inventory, int group, int width, int height) {
        int size = inventory.getContainerSize();
        int slotsX = (width - 176) / 2 + 80;
        int slotsY = 16;
        for (int i = 0; i < size; ++i) {
            int tx = slotsX + i * 19;
            addSlot(new GroupSlot(inventory, group, i, tx, slotsY));
        }
    }

    public final class GroupSlot extends Slot {

        private final int group;

        public GroupSlot(Container inventory, int group, int index, int x, int y) {
            super(inventory, index, x, y);
            this.group = group;
        }

        @Override
        public boolean isActive() {
            return getGroup() == group;
        }
    }
}
