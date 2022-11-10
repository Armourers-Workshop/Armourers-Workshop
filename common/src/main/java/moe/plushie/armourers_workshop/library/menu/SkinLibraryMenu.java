package moe.plushie.armourers_workshop.library.menu;

import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.menu.AbstractBlockContainerMenu;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class SkinLibraryMenu extends AbstractBlockContainerMenu {

    protected final Container inventory;
    protected final Inventory playerInventory;

    public int inventoryWidth = 162;
    public int inventoryHeight = 76;

    private int libraryVersion = 0;

    public SkinLibraryMenu(MenuType<?> menuType, Block block, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(menuType, block, containerId, access);
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
        if (playerInventory.player instanceof ServerPlayer) {
            SkinLibraryManager.Server server = SkinLibraryManager.getServer();
            if (libraryVersion != server.getVersion()) {
                server.sendTo((ServerPlayer) playerInventory.player);
                libraryVersion = server.getVersion();
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return quickMoveStack(player, index, slots.size() - 1);
    }

    protected void addInputSlot(Container inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() == ModItems.SKIN_TEMPLATE.get() || !SkinDescriptor.of(itemStack).isEmpty();
            }
        });
    }

    protected void addOutputSlot(Container inventory, int slot, int x, int y) {
        addSlot(new Slot(inventory, slot, x, y) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });
    }

    public ItemStack getInputStack() {
        return inventory.getItem(0);
    }

    public ItemStack getOutputStack() {
        return inventory.getItem(1);
    }

    public Player getPlayer() {
        return playerInventory.player;
    }

    public boolean shouldSaveStack() {
        return getOutputStack().isEmpty();
    }

    public boolean shouldLoadStack() {
        return getOutputStack().isEmpty() && !getInputStack().isEmpty() && getInputStack().getItem() == ModItems.SKIN_TEMPLATE.get();
    }

    public void crafting(SkinDescriptor descriptor) {
        boolean consume = true;
        ItemStack itemStack = getInputStack();
        ItemStack newItemStack = itemStack.copy();
        if (descriptor != null) {
            // only consumes the template
            newItemStack = SkinItem.replace(itemStack.copy(), descriptor);
            consume = itemStack.getItem() == ModItems.SKIN_TEMPLATE.get();
        }
        inventory.setItem(1, newItemStack);
        if (consume) {
            itemStack.shrink(1);
        }
    }
}
