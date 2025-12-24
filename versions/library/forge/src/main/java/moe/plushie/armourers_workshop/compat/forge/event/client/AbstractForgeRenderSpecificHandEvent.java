package moe.plushie.armourers_workshop.compat.forge.event.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.core.client.render.state.PlayerRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.init.event.client.RenderSpecificHandEvent;
import net.minecraft.world.entity.HumanoidArm;

@Available("[1.16, 1.26)")
public class AbstractForgeRenderSpecificHandEvent {

    public static IEventHandler<RenderSpecificHandEvent> armFactory() {
        return AbstractForgeClientEventsImpl.RENDER_SPECIFIC_HAND.map(event -> new RenderSpecificHandEvent() {
            @Override
            public float partialTick() {
                return 1.0f;
            }

            @Override
            public int lightmap() {
                return event.getPackedLight();
            }

            @Override
            public int overlay() {
                return OverlayTexture.NO_OVERLAY;
            }

            @Override
            public OpenInteractionHand hand() {
                if (event.getArm() == HumanoidArm.RIGHT) {
                    return OpenInteractionHand.MAIN_HAND;
                }
                return OpenInteractionHand.OFF_HAND;
            }

            @Override
            public PlayerRenderState renderState() {
                return AbstractRenderState.wrap(event.getPlayer());
            }

            @Override
            public IGraphicsContext context() {
                return AbstractGraphicsRenderer.wrap(event.getPoseStack(), event.getMultiBufferSource());
            }

            @Override
            public void setCancelled(boolean isCancelled) {
                event.setCanceled(isCancelled);
            }
        });
    }
}
