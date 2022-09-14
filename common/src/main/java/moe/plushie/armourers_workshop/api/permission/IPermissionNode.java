package moe.plushie.armourers_workshop.api.permission;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface IPermissionNode {

    Component getName();

    Component getDescription();

    ResourceLocation getRegistryName();

    boolean resolve(GameProfile profile, IPermissionContext context);

    default boolean resolve(Player player, IPermissionContext context) {
        return resolve(player.getGameProfile(), context);
    }
}
