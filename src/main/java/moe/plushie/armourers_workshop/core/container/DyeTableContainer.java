package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.tileentity.DyeTableTileEntity;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import moe.plushie.armourers_workshop.init.common.AWBlocks;
import moe.plushie.armourers_workshop.init.common.AWContainerTypes;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.AWItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class DyeTableContainer extends Container {

    private final ISkinPaintType[] paintTypes = {SkinPaintTypes.DYE_1, SkinPaintTypes.DYE_2, SkinPaintTypes.DYE_3, SkinPaintTypes.DYE_4, SkinPaintTypes.DYE_5, SkinPaintTypes.DYE_6, SkinPaintTypes.DYE_7, SkinPaintTypes.DYE_8};
    private final IInventory inventory;
    private final IWorldPosCallable access;

    public DyeTableContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(AWContainerTypes.DYE_TABLE, containerId);
        this.access = access;
        this.inventory = getEntity();
        this.addPlayerSlots(playerInventory, 8, 108);
        this.addCustomSlots(inventory, 68, 36, 22, 22);
        this.addInputSlot(inventory, 8, 26, 23);
        this.addOutputSlot(inventory, 9, 26, 69);
    }

    public DyeTableTileEntity getEntity() {
        TileEntity tileEntity = access.evaluate(World::getBlockEntity).orElse(null);
        if (tileEntity instanceof DyeTableTileEntity) {
            return (DyeTableTileEntity) tileEntity;
        }
        return null;
    }

    public void setOutputStack(ItemStack itemStack) {
        inventory.setItem(9, itemStack);
    }

    public ItemStack getOutputStack() {
        return inventory.getItem(9);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.access, player, AWBlocks.DYE_TABLE);
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
            slot.set(ItemStack.EMPTY);
            return itemStack.copy();
        }
        if (!moveItemStackTo(itemStack, 36, slots.size() - 1, false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    protected void addInputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public boolean mayPickup(PlayerEntity p_82869_1_) {
                return false;
            }

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return !SkinDescriptor.of(itemStack).isEmpty();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                if (inventory.getItem(9).isEmpty()) {
                    loadSkin(getItem());
                }
            }
        });
    }

    protected void addOutputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public boolean mayPlace(ItemStack p_75214_1_) {
                return false;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                if (!hasItem()) {
                    loadSkin(ItemStack.EMPTY);
                }
            }
        });
    }

    protected void addCustomSlots(IInventory inventory, int x, int y, int itemWidth, int itemHeight) {
        for (int i = 0; i < 8; i++) {
            int ix = x + (i % 4) * itemWidth;
            int iy = y + (i / 4) * itemHeight;
            Slot slot = addSlot(new LockableSlot(inventory, i, ix, iy));
            slot.setBackground(AWCore.resource("textures/atlas/items.png"), AWCore.getSlotIcon("dye"));
        }
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

    protected void loadSkin(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            inventory.clearContent();
            return;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        ColorScheme scheme = descriptor.getColorScheme();
        for (int i = 0; i < paintTypes.length; ++i) {
            ItemStack colorStack = ItemStack.EMPTY;
            PaintColor paintColor = scheme.getColor(paintTypes[i]);
            if (paintColor != null) {
                colorStack = new ItemStack(AWItems.BOTTLE);
                ColoredItem.setColor(colorStack, paintColor);
            }
            inventory.setItem(i, colorStack);
        }
        setOutputStack(itemStack.copy());
    }

    protected void applySkin(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        ColorScheme newScheme = new ColorScheme();
        for (int i = 0; i < paintTypes.length; ++i) {
            ItemStack colorStack = inventory.getItem(i);
            PaintColor paintColor = ColoredItem.getColor(colorStack);
            if (paintColor != null) {
                newScheme.setColor(paintTypes[i], paintColor);
            }
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (newScheme.equals(descriptor.getColorScheme())) {
            return; // not any changes.
        }
        descriptor = new SkinDescriptor(descriptor, newScheme);
        ItemStack newItemStack = itemStack.copy();
        SkinDescriptor.setDescriptor(newItemStack, descriptor);
        setOutputStack(newItemStack);
    }

    public class LockableSlot extends Slot {

        public LockableSlot(IInventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() instanceof BottleItem;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            applySkin(getOutputStack());
        }
    }
}