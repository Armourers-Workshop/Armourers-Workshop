package moe.plushie.armourers_workshop.api.permission;

import moe.plushie.armourers_workshop.api.common.IGameProfile;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface IPermissionNode {

    Component name();

    Component description();

    IResourceLocation registryName();

    boolean resolve(IGameProfile profile, IPermissionContext context);

    boolean resolve(Player player, IPermissionContext context);
}
