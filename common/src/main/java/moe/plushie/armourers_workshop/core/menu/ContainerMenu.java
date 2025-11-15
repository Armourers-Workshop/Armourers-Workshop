package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerMenu;
import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerSlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public abstract class ContainerMenu extends AbstractContainerMenu {

    public ContainerMenu(@Nullable IMenuType<?> menuType, int menuId) {
        super(menuType, menuId);
    }

    protected void addPlayerSlots(Container inventory, int slotsX, int slotsY) {
        addPlayerSlots(inventory, slotsX, slotsY, AbstractContainerSlot::new);
    }

    protected void addPlayerSlots(Container inventory, int slotsX, int slotsY, ISlotBuilder builder) {
        for (var col = 0; col < 9; ++col) {
            this.addSlot(builder.create(inventory, col, slotsX + col * 18, slotsY + 58));
        }
        for (var row = 0; row < 3; ++row) {
            for (var col = 0; col < 9; ++col) {
                this.addSlot(builder.create(inventory, col + row * 9 + 9, slotsX + col * 18, slotsY + row * 18));
            }
        }
    }

    protected ISlotBuilder visibleSlotBuilder(BooleanSupplier supplier) {
        return (inv, slot, x, y) -> new AbstractContainerSlot(inv, slot, x, y) {
            @Override
            protected boolean abi$isActive() {
                return supplier.getAsBoolean();
            }
        };
    }

    @Override
    protected ItemStack abi$quickMoveStack(Player player, int index) {
        return abi$quickMoveStack(player, index, slots.size());
    }

    protected ItemStack abi$quickMoveStack(Player player, int index, int slotSize) {
        var slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        var itemStack = slot.getItem();
        if (index >= 36) {
            if (!(moveItemStackTo(itemStack, 0, 9, false) || moveItemStackTo(itemStack, 9, 36, false))) {
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

    public interface ISlotBuilder {

        AbstractContainerSlot create(Container inventory, int slot, int x, int y);
    }
}
