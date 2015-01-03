package riskyken.armourersWorkshop.utils;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

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
}
