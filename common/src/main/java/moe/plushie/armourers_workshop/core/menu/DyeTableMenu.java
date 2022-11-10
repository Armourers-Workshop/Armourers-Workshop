package moe.plushie.armourers_workshop.core.menu;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlot;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class DyeTableMenu extends AbstractBlockContainerMenu {

    private final ISkinPaintType[] paintTypes = {SkinPaintTypes.DYE_1, SkinPaintTypes.DYE_2, SkinPaintTypes.DYE_3, SkinPaintTypes.DYE_4, SkinPaintTypes.DYE_5, SkinPaintTypes.DYE_6, SkinPaintTypes.DYE_7, SkinPaintTypes.DYE_8};
    private final Container inventory;

    public DyeTableMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(menuType, block, containerId, access);
        this.inventory = getTileInventory();
        this.addPlayerSlots(playerInventory, 8, 108);
        this.addCustomSlots(inventory, 68, 36, 22, 22);
        this.addInputSlot(inventory, 8, 26, 23);
        this.addOutputSlot(inventory, 9, 26, 69);
    }

    public ItemStack getOutputStack() {
        return inventory.getItem(9);
    }

    public void setOutputStack(ItemStack itemStack) {
        inventory.setItem(9, itemStack);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addInputSlot(Container inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {

            @Override
            public boolean mayPickup(Player p_82869_1_) {
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

    protected void addOutputSlot(Container inventory, int slot, int x, int y) {
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

    protected void addCustomSlots(Container inventory, int x, int y, int itemWidth, int itemHeight) {
        for (int i = 0; i < 8; i++) {
            int ix = x + (i % 4) * itemWidth;
            int iy = y + (i / 4) * itemHeight;
            addSlot(new LockableSlot(inventory, i, ix, iy, SkinSlotType.DYE));
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
            IPaintColor paintColor = scheme.getColor(paintTypes[i]);
            if (paintColor != null) {
                colorStack = new ItemStack(ModItems.BOTTLE.get());
                ColorUtils.setColor(colorStack, paintColor);
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
            IPaintColor paintColor = ColorUtils.getColor(colorStack);
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

    public class LockableSlot extends SkinSlot {

        public LockableSlot(Container inventory, int slot, int x, int y, SkinSlotType... slotTypes) {
            super(inventory, slot, x, y, slotTypes);
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
