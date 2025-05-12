package moe.plushie.armourers_workshop.compatibility.fabric;

import com.mojang.authlib.GameProfile;
import me.lucko.fabric.api.permissions.v0.Permissions;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.init.platform.fabric.EnvironmentManagerImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.PermissionNodeBuilderImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class AbstractFabricPermissionManager {

    public static IPermissionNode makeNode(IResourceLocation registryName, int level) {
        var node = registryName.toLanguageKey();
        return new PermissionNodeBuilderImpl.NodeImpl(registryName) {

            @Override
            public boolean resolve(Player player, IPermissionContext context) {
                // only work in server side.
                if (player instanceof ServerPlayer) {
                    return Permissions.check(player, node, level);
                }
                return true;
            }

            @Override
            public boolean resolve(GameProfile profile, IPermissionContext context) {
                // only work in server side.
                var server = EnvironmentManagerImpl.getServer();
                if (server != null) {
                    return Permissions.check(profile, node, level, server).join();
                }
                return true;
            }
        };
    }
}
