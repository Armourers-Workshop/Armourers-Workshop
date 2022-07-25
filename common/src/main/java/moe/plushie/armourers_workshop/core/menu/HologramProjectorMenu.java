package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HologramProjectorMenu extends AbstractBlockContainerMenu {

    private final Inventory playerInventory;
    private final Container inventory;
    private int group;

    public HologramProjectorMenu(int containerId, Inventory playerInventory, ContainerLevelAccess worldPos) {
        super(containerId, ModMenuTypes.HOLOGRAM_PROJECTOR, ModBlocks.HOLOGRAM_PROJECTOR, worldPos);
        this.playerInventory = playerInventory;
        this.inventory = getTileInventory();
        this.reload(0, 0, 240, 240);
    }

    public void reload(int inventoryX, int inventoryY, int width, int height) {
        slots.clear();
        addPlayerSlots(playerInventory, inventoryX, inventoryY);
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

    protected void addPlayerSlots(Container inventory, int x, int y) {
        int slotsX = x + 8;
        int slotsY = y + 16;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new PlayerSlot(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new PlayerSlot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }

    public final class PlayerSlot extends Slot {
        public PlayerSlot(Container inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return shouldRenderInventory();
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
