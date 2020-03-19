package moe.plushie.armourers_workshop.utils;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public final class PlayerUtils {

    private PlayerUtils() {
        throw new IllegalAccessError("Utility class.");
    }

    public static boolean gameProfilesMatch(GameProfile profile1, GameProfile profile2) {
        if (profile1 == null) {
            return false;
        }
        if (profile2 == null) {
            return false;
        }

        if (profile1.getId() != null && profile2.getId() != null) {
            if (profile1.getId().equals(profile2.getId())) {
                return true;
            }
        }

        if (profile1.getName() != null && profile2.getName() != null) {
            if (profile1.getName().equals(profile2.getName())) {
                return true;
            }
        }

        return false;
    }

    public static EnumFacing getDirection(int x, int y, int z, EntityPlayer player) {
        return EnumFacing.byIndex(getOrientation(x, y, z, player));
    }

    public static int getOrientation(int x, int y, int z, EntityLivingBase entity) {
        if (MathHelper.abs((float) entity.posX - x) < 2.0F && MathHelper.abs((float) entity.posZ - z) < 2.0F) {
            double d0 = entity.posY + entity.getEyeHeight() - entity.getYOffset();

            if (d0 - y > 2.0D) {
                return 1;
            }
            if (y - d0 > 0.0D) {
                return 0;
            }
        }

        int l = MathHelper.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
    }

    public static EnumFacing getDirectionSide(EntityPlayer player) {
        return EnumFacing.byIndex(getOrientationSide(player));
    }

    public static int getOrientationSide(EntityLivingBase entity) {
        int l = MathHelper.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        return l == 0 ? 3 : (l == 1 ? 4 : (l == 2 ? 2 : (l == 3 ? 5 : 0)));
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
}
