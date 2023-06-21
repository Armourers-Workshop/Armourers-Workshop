package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.compatibility.core.AbstractContainerMenuImpl;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public abstract class AbstractContainerMenu extends AbstractContainerMenuImpl {

    public AbstractContainerMenu(@Nullable MenuType<?> containerType, int containerId) {
        super(containerType, containerId);
    }

    public ItemStack quickMoveStack(Player player, int i) {
        // TODO: IMP
        return ItemStack.EMPTY;
    }

    public ItemStack quickMoveStack(Player player, int index, int slotSize) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (index >= 36) {
            if (!(moveItemStackTo(itemStack, 9, 36, false) || moveItemStackTo(itemStack, 0, 9, false))) {
                return ItemStack.EMPTY;
            }
            slot.set(ItemStack.EMPTY);
            return itemStack.copy();
        }
        if (!moveItemStackTo(itemStack, 36, slotSize, false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    protected void addPlayerSlots(Container inventory, int slotsX, int slotsY) {
        addPlayerSlots(inventory, slotsX, slotsY, Slot::new);
    }

    protected void addPlayerSlots(Container inventory, int slotsX, int slotsY, ISlotBuilder builder) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(builder.create(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(builder.create(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }

    protected ISlotBuilder visibleSlotBuilder(BooleanSupplier supplier) {
        return (inv, slot, x, y) -> new Slot(inv, slot, x, y) {
            @Override
            public boolean isActive() {
                return supplier.getAsBoolean();
            }
        };
    }

    public interface ISlotBuilder {

        Slot create(Container inventory, int slot, int x, int y);
    }
}
