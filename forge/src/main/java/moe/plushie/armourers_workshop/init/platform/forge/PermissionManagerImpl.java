package moe.plushie.armourers_workshop.init.platform.forge;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.init.platform.PermissionManager;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;
import net.minecraftforge.server.permission.context.TargetContext;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PermissionManagerImpl {

    public static String registerNode(String node, PermissionManager.Level level, String desc) {
        return PermissionAPI.registerNode(node, of(level), desc);
    }

    public static boolean hasPermission(GameProfile profile, String node, @Nullable PermissionManager.PlayerContext context) {
        return PermissionAPI.hasPermission(profile, node, of(context));
    }

    private static DefaultPermissionLevel of(PermissionManager.Level level) {
        switch (level) {
            case ALL:
                return DefaultPermissionLevel.ALL;
            case OP:
                return DefaultPermissionLevel.OP;
            default:
                return DefaultPermissionLevel.NONE;
        }
    }

    private static IContext of(PermissionManager.PlayerContext context) {
        if (context == null) {
            return null;
        }
        if (context instanceof PermissionManager.BlockContext) {
            PermissionManager.BlockContext context1 = (PermissionManager.BlockContext) context;
            return new BlockPosContext(context1.player, context1.blockPos, context1.blockState, context1.facing);
        }
        if (context instanceof PermissionManager.TargetContext) {
            PermissionManager.TargetContext context1 = (PermissionManager.TargetContext) context;
            return new TargetContext(context1.player, context1.target);
        }
        return new PlayerContext(context.player);
    }
}
