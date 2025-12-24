package moe.plushie.armourers_workshop.compat.fabric;

import me.lucko.fabric.api.permissions.v0.Permissions;
import moe.plushie.armourers_workshop.api.common.IGameProfile;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.compat.client.utils.AbstractGameProfile;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.fabric.builder.PermissionNodeBuilderImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class AbstractFabricPermissionManager {

    public static IPermissionNode makeNode(OpenResourceLocation registryName, int level) {
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
            public boolean resolve(IGameProfile profile, IPermissionContext context) {
                // only work in server side.
                var server = EnvironmentManager.getServer();
                if (server != null) {
                    return Permissions.check(AbstractGameProfile.unwrap(profile), node, level, server).join();
                }
                return true;
            }
        };
    }
}
