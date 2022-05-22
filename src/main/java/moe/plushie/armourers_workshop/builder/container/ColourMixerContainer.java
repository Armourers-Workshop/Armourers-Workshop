package moe.plushie.armourers_workshop.builder.container;

import moe.plushie.armourers_workshop.core.container.AbstractBlockContainer;
import moe.plushie.armourers_workshop.core.item.impl.IPaintPicker;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class ColourMixerContainer extends AbstractBlockContainer {

    private final IInventory inventory = new Inventory(2);

    public ColourMixerContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerId, ModContainerTypes.COLOR_MIXER, ModBlocks.COLOR_MIXER, access);
        this.addPlayerSlots(playerInventory, 48, 158);
        this.addCustomSlot(inventory, 0, 83, 101);
        this.addCustomSlot(inventory, 1, 134, 101);
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
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return slot == 0 && (itemStack.getItem() instanceof IPaintPicker);
            }

            @Override
            public void setChanged() {
                ItemStack itemStack = inventory.getItem(0);
                Item item = itemStack.getItem();
                if (item instanceof IPaintPicker && inventory.getItem(1).isEmpty()) {
                    ItemStack newItemStack = itemStack.copy();
                    access.execute((world, pos) -> ((IPaintPicker) item).pickColor(buildContext(world, pos, newItemStack)));
                    inventory.setItem(0, ItemStack.EMPTY);
                    inventory.setItem(1, newItemStack);
                }
                super.setChanged();
            }
        });
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

    protected ItemUseContext buildContext(World world, BlockPos pos, ItemStack itemStack) {
        BlockRayTraceResult traceResult = BlockRayTraceResult.miss(Vector3d.ZERO, Direction.NORTH, pos);
        return new ItemUseContext(world, null, Hand.OFF_HAND, itemStack, traceResult);
    }
}