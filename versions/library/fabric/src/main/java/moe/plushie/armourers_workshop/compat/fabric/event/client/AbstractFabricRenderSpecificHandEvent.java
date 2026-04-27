package moe.plushie.armourers_workshop.compat.fabric.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.PlayerRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderSpecificArmEvents;

@Available("[16, 26)")
public class AbstractFabricRenderSpecificHandEvent {

    public static IEventHandler<RenderSpecificHandEvent> armFactory() {
        return (priority, receiveCancelled, subscriber) -> RenderSpecificArmEvents.ARM.register((player, lightmap, hand, context) -> {
            var flags = new boolean[]{false};
            subscriber.accept(new RenderSpecificHandEvent() {

                @Override
                public float partialTick() {
                    return 0;
                }

                @Override
                public int lightmap() {
                    return lightmap;
                }

                @Override
                public int overlay() {
                    return OverlayTexture.NO_OVERLAY;
                }

                @Override
                public OpenInteractionHand hand() {
                    return hand;
                }

                @Override
                public PlayerRenderState renderState() {
                    return AbstractRenderState.wrap(player);
                }

                @Override
                public IGraphicsContext context() {
                    return context;
                }

                @Override
                public void setCancelled(boolean isCancelled) {
                    flags[0] = isCancelled;
                }
            });
            return !flags[0];
        });
    }
}
