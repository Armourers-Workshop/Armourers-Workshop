package moe.plushie.armourers_workshop.core.permission;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class TargetPermissionContext extends PlayerPermissionContext {

    public final Entity target;

    public TargetPermissionContext(Player ep, @Nullable Entity entity) {
        super(ep);
        this.target = entity;
    }
}
