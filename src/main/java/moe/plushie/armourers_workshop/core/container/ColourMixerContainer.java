package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.core.base.AWBlocks;
import moe.plushie.armourers_workshop.core.crafting.recipe.SkinningRecipes;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.tileentity.ColourMixerTileEntity;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.ContainerTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

public class ColourMixerContainer extends Container {

    public static final ContainerType<ColourMixerContainer> TYPE = ContainerTypeBuilder
            .create(ColourMixerContainer::new, IWorldPosCallable.class)
            .withTitle(TranslateUtils.title("inventory.armourers_workshop.colour-mixer"))
            .withDataProvider(AWDataSerializers::readWorldPos, AWDataSerializers::writeWorldPos)
            .build("colour-mixer");

    private final IInventory inventory = new Inventory(2);

    private final IWorldPosCallable access;

    public ColourMixerContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(TYPE, containerId);
        this.access = access;
        this.addPlayerSlots(playerInventory, 48, 158);
        this.addCustomSlot(inventory, 0, 83, 101);
        this.addCustomSlot(inventory, 1, 134, 101);
    }

    public ColourMixerTileEntity getEntity() {
        TileEntity tileEntity = access.evaluate(World::getBlockEntity).orElse(null);
        if (tileEntity instanceof ColourMixerTileEntity) {
            return (ColourMixerTileEntity) tileEntity;
        }
        return null;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, AWBlocks.COLOUR_MIXER);
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        this.access.execute((world, pos) -> this.clearContainer(player, world, inventory));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (index >= 36) {
            if (!(moveItemStackTo(itemStack, 9, 36, false) || moveItemStackTo(itemStack, 0, 9, false))) {
                return ItemStack.EMPTY;
            }
            slot.setChanged();
            return itemStack.copy();
        }
        if (!moveItemStackTo(itemStack, 36, slots.size() - 1, false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    protected void addCustomSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return slot == 0 && (itemStack.getItem() instanceof ColoredItem);
            }

            @Override
            public void setChanged() {
                ItemStack itemStack = inventory.getItem(0);
                if (!itemStack.isEmpty() && inventory.getItem(1).isEmpty()) {
                    itemStack = itemStack.copy();
                    ColourMixerTileEntity tileEntity = getEntity();
                    if (tileEntity != null) {
                        ColoredItem.setColor(itemStack, tileEntity.getColor());
                    }
                    inventory.setItem(0, ItemStack.EMPTY);
                    inventory.setItem(1, itemStack);
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
}