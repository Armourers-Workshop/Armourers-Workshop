package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.compat.api.entity.PlayerAccessor;

public class PlayerRenderState extends LivingEntityRenderState {

    protected boolean isFishing = false;

    public boolean isFishing() {
        return isFishing;
    }

    public static void extract(PlayerAccessor entity, PlayerRenderState renderState) {
        renderState.isFlying = entity.aw2$isFlying();
        renderState.isFishing = entity.aw2$isFishing();
    }
}
