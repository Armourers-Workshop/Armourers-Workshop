package moe.plushie.armourers_workshop.init.platform.fabric;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.init.platform.PermissionManager;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PermissionManagerImpl {

    public static String registerNode(String node, PermissionManager.Level level, String desc) {
        return node;
    }

    public static boolean hasPermission(GameProfile profile, String node, @Nullable PermissionManager.PlayerContext context) {
        // TODO: @SAGESSE
        return true;
    }
}
