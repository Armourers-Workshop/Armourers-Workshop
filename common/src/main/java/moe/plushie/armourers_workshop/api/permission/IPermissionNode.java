package moe.plushie.armourers_workshop.api.permission;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface IPermissionNode {

    Component name();

    Component description();

    IResourceLocation registryName();

    boolean resolve(GameProfile profile, IPermissionContext context);

    default boolean resolve(Player player, IPermissionContext context) {
        return resolve(player.getGameProfile(), context);
    }
}
