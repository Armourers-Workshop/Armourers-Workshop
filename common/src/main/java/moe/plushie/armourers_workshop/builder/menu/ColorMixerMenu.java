package moe.plushie.armourers_workshop.builder.menu;

import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.menu.AbstractBlockContainerMenu;
import moe.plushie.armourers_workshop.utils.ext.OpenUseOnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ColorMixerMenu extends AbstractBlockContainerMenu {

    private final Container inventory = new SimpleContainer(2);

    public ColorMixerMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(menuType, block, containerId, access);
        this.addPlayerSlots(playerInventory, 48, 158);
        this.addCustomSlot(inventory, 0, 83, 101);
        this.addCustomSlot(inventory, 1, 134, 101);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, inventory));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addCustomSlot(Container inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return slot == 0 && (itemStack.getItem() instanceof IItemColorProvider);
            }

            @Override
            public void setChanged() {
                ItemStack itemStack = inventory.getItem(0);
                Item item = itemStack.getItem();
                if (item instanceof IPaintToolPicker && inventory.getItem(1).isEmpty()) {
                    ItemStack newItemStack = itemStack.copy();
                    access.execute((world, pos) -> ((IPaintToolPicker) item).usePickTool(buildContext(world, pos, newItemStack)));
                    inventory.setItem(0, ItemStack.EMPTY);
                    inventory.setItem(1, newItemStack);
                }
                super.setChanged();
            }
        });
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

    protected UseOnContext buildContext(Level world, BlockPos pos, ItemStack itemStack) {
        BlockHitResult traceResult = BlockHitResult.miss(Vec3.ZERO, Direction.NORTH, pos);
        return new OpenUseOnContext(world, null, InteractionHand.OFF_HAND, itemStack, traceResult);
    }
}