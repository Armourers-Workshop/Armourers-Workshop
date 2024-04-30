package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderSpecificHandEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderSpecificArmEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;

@Available("[1.16, )")
public class AbstractFabricRenderSpecificHandEvent {

    public static IEventHandler<RenderSpecificHandEvent> armFactory() {
        return subscriber -> RenderSpecificArmEvents.ARM.register((poseStack, buffers, light, player, hand) -> {
            boolean[] flags = {false};
            subscriber.accept(new RenderSpecificHandEvent() {

                @Override
                public InteractionHand getHand() {
                    return hand;
                }

                @Override
                public PoseStack getPoseStack() {
                    return poseStack;
                }

                @Override
                public MultiBufferSource getMultiBufferSource() {
                    return buffers;
                }

                @Override
                public int getPackedLight() {
                    return light;
                }

                @Override
                public AbstractClientPlayer getPlayer() {
                    return (AbstractClientPlayer) player;
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
