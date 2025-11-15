package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.client.event.AbstractRenderLivingEntityEvent;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;

@Available("[1.18, 1.22)")
public class AbstractForgeRenderLivingEvent implements AbstractForgeRenderLivingEventImpl {

    public static IEventHandler<RenderLivingEntityEvent.Setup<?, ?>> setupFactory() {
        return AbstractForgeClientEventsImpl.RENDER_LIVING_ENTITY_PRE.map(event -> AbstractRenderLivingEntityEvent.setup(event.getEntity(), event.getPartialTick(), event.getRenderer()));
    }

    public static IEventHandler<RenderLivingEntityEvent.Pre<?, ?>> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_LIVING_ENTITY_APPLY.map(event -> AbstractRenderLivingEntityEvent.pre(event.getEntity(), event.getPartialTick(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer()));
    }

    public static IEventHandler<RenderLivingEntityEvent.Post<?, ?>> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_LIVING_ENTITY_POST.map(event -> AbstractRenderLivingEntityEvent.post(event.getEntity(), event.getPartialTick(), event.getPackedLight(), OverlayTexture.NO_OVERLAY, event.getPoseStack(), event.getMultiBufferSource(), event.getRenderer()));
    }
}
