package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.render.state.PlayerRenderState;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;

public interface RenderSpecificHandEvent {

    float partialTicks();

    int lightmap();

    int overlay();

    PlayerRenderState renderState();

    OpenInteractionHand hand();

    IGraphicsContext context();

    void setCancelled(boolean isCancelled);
}
