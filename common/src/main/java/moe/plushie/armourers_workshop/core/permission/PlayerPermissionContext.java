package moe.plushie.armourers_workshop.core.permission;

import com.google.common.base.Preconditions;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import net.minecraft.world.entity.player.Player;

public class PlayerPermissionContext implements IPermissionContext {

    public final Player player;

    public PlayerPermissionContext(Player ep) {
        this.player = Preconditions.checkNotNull(ep, "Player can't be null in PlayerContext!");
    }
}
