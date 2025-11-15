package moe.plushie.armourers_workshop.core.client.render.state;

import net.minecraft.world.entity.player.Player;

public class PlayerRenderState extends LivingEntityRenderState {

    protected boolean isFishing = false;

    public boolean isFishing() {
        return isFishing;
    }

    public static void extract(Player entity, PlayerRenderState renderState) {
        renderState.isFlying = entity.getAbilities().flying;
        renderState.isFishing = entity.fishing != null;
    }
}
