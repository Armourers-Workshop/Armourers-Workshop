package moe.plushie.armourers_workshop.builder.menu;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.builder.blockentity.ColorMixerBlockEntity;
import moe.plushie.armourers_workshop.compat.core.item.AbstractUseOnContext;
import moe.plushie.armourers_workshop.compat.core.menu.AbstractContainerSlot;
import moe.plushie.armourers_workshop.core.data.paint.IItemPaintable;
import moe.plushie.armourers_workshop.core.data.paint.IPaintToolPicker;
import moe.plushie.armourers_workshop.core.menu.BlockEntityContainerMenu;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ColorMixerMenu extends BlockEntityContainerMenu<ColorMixerBlockEntity> {

    private final Container inventory = new SimpleContainer(2);

    public ColorMixerMenu(IMenuType<?> menuType, Block block, int containerId, Inventory playerInventory, IGlobalPos access) {
        super(menuType, block, containerId, access);
        this.addPlayerSlots(playerInventory, 48, 158);
        this.addCustomSlot(inventory, 0, 83, 101);
        this.addCustomSlot(inventory, 1, 134, 101);
    }

    protected void addCustomSlot(Container inventory, int slot, int x, int y) {
        addSlot(new AbstractContainerSlot(inventory, slot, x, y) {

            @Override
            protected void abi$setChanged() {
                var itemStack = inventory.getItem(0);
                var item = itemStack.getItem();
                if (item instanceof IPaintToolPicker toolPicker && inventory.getItem(1).isEmpty()) {
                    var newItemStack = itemStack.copy();
                    access.execute((world, pos) -> toolPicker.usePickTool(buildContext(world, pos, newItemStack)));
                    inventory.setItem(0, ItemStack.EMPTY);
                    inventory.setItem(1, newItemStack);
                }
                super.abi$setChanged();
            }

            @Override
            protected boolean abi$mayPlace(ItemStack itemStack) {
                return slot == 0 && (itemStack.getItem() instanceof IItemPaintable);
            }
        });
    }

    protected IUseOnContext buildContext(Level level, BlockPos pos, ItemStack itemStack) {
        return AbstractUseOnContext.create(level, null, OpenInteractionHand.OFF_HAND, itemStack, pos);
    }

    @Override
    protected void abi$removed(Player player) {
        super.abi$removed(player);
        abi$clearContainer(player, inventory);
    }

    @Override
    protected ItemStack abi$quickMoveStack(Player player, int index) {
        return abi$quickMoveStack(player, index, slots.size() - 1);
    }
}
