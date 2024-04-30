package moe.plushie.armourers_workshop.compatibility.forge.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientEventsImpl;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderLivingEntityEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.18, )")
public class AbstractForgeRenderLivingEvent {

    public static IEventHandler<RenderLivingEntityEvent.Pre> preFactory() {
        return AbstractForgeClientEventsImpl.RENDER_LIVING_ENTITY_PRE.map(event -> new RenderLivingEntityEvent.Pre() {
            @Override
            public float getPartialTicks() {
                return event.getPartialTick();
            }

            @Override
            public int getPackedLight() {
                return event.getPackedLight();
            }

            @Override
            public LivingEntity getEntity() {
                return event.getEntity();
            }

            @Override
            public LivingEntityRenderer<?, ?> getRenderer() {
                return event.getRenderer();
            }

            @Override
            public PoseStack getPoseStack() {
                return event.getPoseStack();
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return event.getMultiBufferSource();
            }
        });
    }

    public static IEventHandler<RenderLivingEntityEvent.Post> postFactory() {
        return AbstractForgeClientEventsImpl.RENDER_LIVING_ENTITY_POST.map(event -> new RenderLivingEntityEvent.Post() {

            @Override
            public float getPartialTicks() {
                return event.getPartialTick();
            }

            @Override
            public int getPackedLight() {
                return event.getPackedLight();
            }

            @Override
            public LivingEntity getEntity() {
                return event.getEntity();
            }

            @Override
            public LivingEntityRenderer<?, ?> getRenderer() {
                return event.getRenderer();
            }

            @Override
            public PoseStack getPoseStack() {
                return event.getPoseStack();
            }

            @Override
            public MultiBufferSource getMultiBufferSource() {
                return event.getMultiBufferSource();
            }
        });
    }
}
