package moe.plushie.armourers_workshop.core.container;

import moe.plushie.armourers_workshop.core.base.AWBlocks;
import moe.plushie.armourers_workshop.core.tileentity.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.core.utils.AWDataSerializers;
import moe.plushie.armourers_workshop.core.utils.ContainerTypeBuilder;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

@SuppressWarnings("NullableProblems")
public class HologramProjectorContainer extends Container {

    public static final ContainerType<HologramProjectorContainer> TYPE = ContainerTypeBuilder
            .create(HologramProjectorContainer::new, IWorldPosCallable.class)
            .withTitle(TranslateUtils.title("inventory.armourers_workshop.hologram-projector"))
            .withDataProvider(AWDataSerializers::readWorldPos, AWDataSerializers::writeWorldPos)
            .build("hologram-projector");

    private final IWorldPosCallable pos;

    private final PlayerInventory playerInventory;
    private final IInventory inventory;
    private int group;

    public HologramProjectorContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable worldPos) {
        super(TYPE, containerId);
        this.pos = worldPos;
        this.playerInventory = playerInventory;
        this.inventory = getInventory();
        this.reload(0, 0, 240, 240);
    }

    public HologramProjectorTileEntity getEntity() {
        TileEntity tileEntity = pos.evaluate(World::getBlockEntity).orElse(null);
        if (tileEntity instanceof HologramProjectorTileEntity) {
            return (HologramProjectorTileEntity) tileEntity;
        }
        return null;
    }

    public IInventory getInventory() {
        TileEntity tileEntity = pos.evaluate(World::getBlockEntity).orElse(null);
        if (tileEntity instanceof IInventory) {
            return (IInventory) tileEntity;
        }
        return null;
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
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.pos, player, AWBlocks.HOLOGRAM_PROJECTOR);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = slot.getItem();
        if (slot instanceof GroupSlot) {
            if (!(moveItemStackTo(itemStack, 9, 36, false) || moveItemStackTo(itemStack, 0, 9, false))) {
                return ItemStack.EMPTY;
            }
            slot.setChanged();
            return itemStack.copy();
        }
        if (!moveItemStackTo(itemStack, 36, 37, false)) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        return ItemStack.EMPTY;
    }

    public boolean shouldRenderPlayerInventory() {
        return getGroup() == 1;
    }

    protected void addSkinSlots(IInventory inventory, int group, int width, int height) {
        int size = inventory.getContainerSize();
        int slotsX = (width - 176) / 2 + 80;
        int slotsY = 16;
        for (int i = 0; i < size; ++i) {
            int tx = slotsX + i * 19;
            addSlot(new GroupSlot(inventory, group, i, tx, slotsY));
        }
    }

    protected void addPlayerSlots(IInventory inventory, int x, int y) {
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
        public PlayerSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isActive() {
            return shouldRenderPlayerInventory();
        }
    }

    public final class GroupSlot extends Slot {

        private final int group;

        public GroupSlot(IInventory inventory, int group, int index, int x, int y) {
            super(inventory, index, x, y);
            this.group = group;
        }

        @Override
        public boolean isActive() {
            return getGroup() == group;
        }
    }
}
