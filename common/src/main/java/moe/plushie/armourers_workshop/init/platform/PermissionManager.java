package moe.plushie.armourers_workshop.init.platform;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PermissionManager {

    /**
     * <b>Only use this after PreInit state!</b>
     *
     * @param node  Permission node, best if it's lowercase and contains '.' (e.g. <code>"modid.subgroup.permission_id"</code>)
     * @param level Default permission level for this node. If not isn't registered, it's level is going to be 'NONE'
     * @param desc  Optional description of the node
     */
    @ExpectPlatform
    public static String registerNode(String node, Level level, String desc) {
        return node;
    }

    /**
     * @param profile GameProfile of the player who is requesting permission. The player doesn't have to be online
     * @param node    Permission node. See {@link #registerNode(String, Level, String)}
     * @param context Context for this permission. Highly recommended to not be null. See {@link PlayerContext}
     * @return true, if player has permission, false if he does not.
     */
    @ExpectPlatform
    public static boolean hasPermission(GameProfile profile, String node, @Nullable PlayerContext context) {
        return false;
    }

    /**
     * <table><thead><tr><th>Level</th><th>Player</th><th>OP</th></tr>
     * </thead><tbody>
     * <tr><td>ALL</td><td>true</td><td>true</td></tr>
     * <tr><td>OP</td><td>false</td><td>true</td></tr>
     * <tr><td>NONE</td><td>false</td><td>false</td></tr>
     * </tbody></table>
     */
    public enum Level {
        ALL,
        OP,
        NONE
    }

    public static class PlayerContext {

        public final Player player;

        public PlayerContext(Player ep) {
            this.player = Preconditions.checkNotNull(ep, "Player can't be null in PlayerContext!");
        }
    }

    public static class TargetContext extends PlayerContext {

        public final Entity target;

        public TargetContext(Player ep, @Nullable Entity entity) {
            super(ep);
            this.target = entity;
        }
    }

    public static class BlockContext extends PlayerContext {

        public final BlockPos blockPos;
        public final BlockState blockState;
        public final Direction facing;

        public BlockContext(Player ep, BlockPos pos, @Nullable BlockState state, @Nullable Direction f) {
            super(ep);
            this.blockPos = Preconditions.checkNotNull(pos, "BlockPos can't be null in BlockPosContext!");
            this.blockState = state;
            this.facing = f;
        }
    }
}

