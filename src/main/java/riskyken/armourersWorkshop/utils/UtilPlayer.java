package riskyken.armourersWorkshop.utils;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;

public final class UtilPlayer {

    public static UUID getIDFromPlayer(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        if (player.getGameProfile() == null) {
            return player.getPersistentID();
        }
        return player.getGameProfile().getId();
    }
    
    public static int getNumberOfItemInInventory(EntityPlayer player, Item item) {
        int itemCount = 0;
        InventoryPlayer inventory = player.inventory;
        for (int i = 0; i < inventory.mainInventory.length; i++) {
            if (inventory.mainInventory[i].getItem() == item) {
                itemCount += inventory.mainInventory[i].stackSize;
            }
        }
        return itemCount;
    }
    
    public static void consumeInventoryItemCount(EntityPlayer player, Item item, int count) {
        int removeCount = count;
        InventoryPlayer inventory = player.inventory;
        for (int i = 0; i < inventory.mainInventory.length; i++) {
            if (inventory.mainInventory[i].getItem() == item) {
                if (inventory.mainInventory[i].stackSize >= removeCount) {
                    removeCount -= inventory.mainInventory[i].stackSize;
                    inventory.mainInventory[i] = null;
                } else {
                    inventory.mainInventory[i].stackSize = removeCount;
                    removeCount = 0;
                }
            }
            if (removeCount < 1) {
                return;
            }
        }
    }
}
