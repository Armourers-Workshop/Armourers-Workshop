package riskyken.armourersWorkshop.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.ArmourersWorkshop;

public final class UtilPlayer {
    
    public static ForgeDirection getDirection(int x, int y, int z, EntityPlayer player) {
        return ForgeDirection.getOrientation(getOrientation(x, y, z, player));
    }
    
    public static int getOrientation(int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
            double d0 = entity.posY + entity.getEyeHeight() - (double) entity.yOffset;

            if (d0 - (double) y > 2.0D) { return 1; }
            if ((double) y - d0 > 0.0D) { return 0; }
        }

        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
    }
    
    public static ForgeDirection getDirectionSide(EntityPlayer player) {
        return ForgeDirection.getOrientation(getOrientationSide(player));
    }
    
    public static int getOrientationSide(EntityLivingBase entity) {
        int l = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
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

    public static void giveItem(EntityPlayer player, ItemStack stack) {
        if (stack == null) {
            return;
        }
        if (!player.inventory.addItemStackToInventory(stack)) {
            if (!player.getEntityWorld().isRemote) {
                UtilItems.spawnItemInWorld(player.getEntityWorld(), player.posX, player.posY, player.posZ, stack);
            }
        }
    }
    
    public static boolean isPlayerOp(EntityPlayer player) {
        MinecraftServer server = ArmourersWorkshop.proxy.getServer();
        if (player == null || player.getGameProfile() == null) {
            return false;
        }
        return server.getConfigurationManager().func_152596_g(player.getGameProfile());
    }
    
    private static MinecraftServer getIntegratedServer() {
        return null;
    }
}
