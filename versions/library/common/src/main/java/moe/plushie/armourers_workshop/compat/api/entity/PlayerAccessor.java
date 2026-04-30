package moe.plushie.armourers_workshop.compat.api.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.player.Player;

@Available("[16, )")
public interface PlayerAccessor extends LivingEntityAccessor {

    @Override
    default Player aw2$self() {
        return (Player) this;
    }

    default boolean aw2$isFlying() {
        return this.aw2$self().getAbilities().flying;
    }

    default boolean aw2$isFishing() {
        return this.aw2$self().fishing != null;
    }
}
