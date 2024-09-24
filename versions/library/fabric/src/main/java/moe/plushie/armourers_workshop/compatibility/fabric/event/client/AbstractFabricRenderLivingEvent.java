package moe.plushie.armourers_workshop.compatibility.fabric.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderLivingEntityEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderLivingEntityEvents;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.16, )")
public class AbstractFabricRenderLivingEvent {

    public static IEventHandler<RenderLivingEntityEvent.Pre> preFactory() {
        return (priority, receiveCancelled, subscriber) -> RenderLivingEntityEvents.PRE.register((entity, partialTicks, light, poseStack, buffers, renderer) -> subscriber.accept(new RenderLivingEntityEvent.Pre() {
            @Override
            public float getPartialTicks() {
                return partialTicks;
            }

            @Override
            public int getPackedLight() {
                return light;
            }

            @Override
            public LivingEntity getEntity() {
                return entity;
            }

            @Override
            public LivingEntityRenderer<?, ?> getRenderer() {
                return renderer;
            }

            @Override
            public PoseStack getPoseStack() {
                return poseStack;
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return buffers;
            }
        }));
    }

    public static IEventHandler<RenderLivingEntityEvent.Post> postFactory() {
        return (priority, receiveCancelled, subscriber) -> RenderLivingEntityEvents.POST.register((entity, partialTicks, light, poseStack, buffers, renderer) -> subscriber.accept(new RenderLivingEntityEvent.Post() {

            @Override
            public float getPartialTicks() {
                return partialTicks;
            }

            @Override
            public int getPackedLight() {
                return light;
            }

            @Override
            public LivingEntity getEntity() {
                return entity;
            }

            @Override
            public LivingEntityRenderer<?, ?> getRenderer() {
                return renderer;
            }

            @Override
            public PoseStack getPoseStack() {
                return poseStack;
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return buffers;
            }
        }));
    }
}
