package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderSpecificHandEvent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

@Available("[1.16, )")
public class AbstractForgeRenderSpecificHandEvent {

    public static IEventHandler<RenderSpecificHandEvent> armFactory() {
        return AbstractForgeClientEventsImpl.RENDER_SPECIFIC_HAND.map(event -> new RenderSpecificHandEvent() {
            @Override
            public InteractionHand getHand() {
                if (event.getArm() == HumanoidArm.RIGHT) {
                    return InteractionHand.MAIN_HAND;
                }
                return InteractionHand.OFF_HAND;
            }

            @Override
            public PoseStack getPoseStack() {
                return event.getPoseStack();
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return event.getMultiBufferSource();
            }

            @Override
            public int getPackedLight() {
                return event.getPackedLight();
            }

            @Override
            public AbstractClientPlayer getPlayer() {
                return event.getPlayer();
            }

            @Override
            public void setCancelled(boolean isCancelled) {
                event.setCanceled(isCancelled);
            }
        });
    }
}
