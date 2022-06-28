package moe.plushie.armourers_workshop.library.container;

import moe.plushie.armourers_workshop.core.container.AbstractBlockContainer;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import moe.plushie.armourers_workshop.init.common.ModItems;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class SkinLibraryContainer extends AbstractBlockContainer {

    protected final IInventory inventory;
    protected final PlayerInventory playerInventory;

    public int inventoryWidth = 162;
    public int inventoryHeight = 76;

    private int libraryVersion = 0;

    public SkinLibraryContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable access) {
        this(containerId, ModContainerTypes.SKIN_LIBRARY, ModBlocks.SKIN_LIBRARY, playerInventory, access);
    }

    public SkinLibraryContainer(int containerId, @Nullable ContainerType<?> containerType, Block block, PlayerInventory playerInventory, IWorldPosCallable access) {
        super(containerId, containerType, block, access);
        this.inventory = getTileInventory();
        this.playerInventory = playerInventory;
        this.reload(0, 0, 240, 240);
    }

    public void reload(int x, int y, int width, int height) {
        int inventoryX = 6;
        int inventoryY = height - inventoryHeight - 4;
        this.slots.clear();
        this.addPlayerSlots(playerInventory, inventoryX, inventoryY);
        this.addInputSlot(inventory, 0, inventoryX, inventoryY - 27);
        this.addOutputSlot(inventory, 1, inventoryX + inventoryWidth - 22, inventoryY - 27);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (playerInventory.player instanceof ServerPlayerEntity) {
            SkinLibraryManager.Server server = SkinLibraryManager.getServer();
            if (libraryVersion != server.getVersion()) {
                server.sendTo((ServerPlayerEntity) playerInventory.player);
                libraryVersion = server.getVersion();
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addInputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() == ModItems.SKIN_TEMPLATE || !SkinDescriptor.of(itemStack).isEmpty();
            }
        });
    }

    protected void addOutputSlot(IInventory inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
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

    public ItemStack getInputStack() {
        return inventory.getItem(0);
    }

    public ItemStack getOutputStack() {
        return inventory.getItem(1);
    }

    public PlayerEntity getPlayer() {
        return playerInventory.player;
    }

    public boolean shouldSaveStack() {
        return getOutputStack().isEmpty();
    }

    public boolean shouldLoadStack() {
        return getOutputStack().isEmpty() && !getInputStack().isEmpty() && getInputStack().getItem() == ModItems.SKIN_TEMPLATE;
    }

    public void crafting(SkinDescriptor descriptor) {
        boolean consume = true;
        ItemStack itemStack = getInputStack();
        ItemStack newItemStack = itemStack.copy();
        if (descriptor != null) {
            // only consumes the template
            newItemStack = SkinItem.replace(itemStack.copy(), descriptor);
            consume = itemStack.getItem() == ModItems.SKIN_TEMPLATE;
        }
        inventory.setItem(1, newItemStack);
        if (consume) {
            itemStack.shrink(1);
        }
    }
}