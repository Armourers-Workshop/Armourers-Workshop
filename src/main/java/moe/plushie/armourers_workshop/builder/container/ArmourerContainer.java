package moe.plushie.armourers_workshop.builder.container;

import moe.plushie.armourers_workshop.core.container.AbstractBlockContainer;
import moe.plushie.armourers_workshop.core.item.impl.IPaintPicker;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

@SuppressWarnings("NullableProblems")
public class ArmourerContainer extends AbstractBlockContainer {

    private final IInventory inventory = new Inventory(2);
    private Group group = null;

    public ArmourerContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerId, ModContainerTypes.ARMOURER, ModBlocks.ARMOURER, access);
        this.addPlayerSlots(playerInventory, 8, 142);
        this.addCustomSlot(inventory, 0, 64, 21);
        this.addCustomSlot(inventory, 1, 147, 21);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, inventory));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addCustomSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new GroupSlot(inventory, slot, x, y) {

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return slot == 0 && !SkinDescriptor.of(itemStack).isEmpty();
            }

            @Override
            public void setChanged() {
//                ItemStack itemStack = inventory.getItem(0);
//                Item item = itemStack.getItem();
//                if (item instanceof IPaintPicker && inventory.getItem(1).isEmpty()) {
//                    ItemStack newItemStack = itemStack.copy();
//                    access.execute((world, pos) -> ((IPaintPicker) item).pickColor(world, pos, newItemStack, null));
//                    inventory.setItem(0, ItemStack.EMPTY);
//                    inventory.setItem(1, newItemStack);
//                }
                super.setChanged();
            }
        });
    }

    protected void addPlayerSlots(IInventory inventory, int slotsX, int slotsY) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new GroupSlot(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new GroupSlot(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }


    public Group getGroup() {
        return this.group;
    }


    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean shouldRenderPlayerInventory() {
        return group == Group.MAIN;
    }

    public enum Group {
        MAIN, SKIN, DISPLAY, BLOCK
    }

    public class GroupSlot extends Slot {

        public GroupSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return shouldRenderPlayerInventory();
        }
    }
}