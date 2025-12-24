package moe.plushie.armourers_workshop.compat.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.entity.AbstractLivingEntityRenderer;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.16, 1.22)")
public class AbstractRenderLivingEntityEvent {

    public static <T extends LivingEntity, S extends LivingEntityRenderState> RenderLivingEntityEvent.Setup<T, S> setup(T entity, float partialTick, LivingEntityRenderer<?, ?> renderer) {
        return new RenderLivingEntityEvent.Setup<>() {

            @Override
            public float partialTick() {
                return partialTick;
            }

            @Override
            public T entity() {
                return entity;
            }

            @Override
            public S renderState() {
                return AbstractRenderState.wrap(entity);
            }

            @Override
            public ILivingEntityRenderer<T, S, ?> entityRenderer() {
                return AbstractLivingEntityRenderer.wrap(renderer);
            }
        };
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> RenderLivingEntityEvent.Pre<T, S> pre(T entity, int lightmap, int overlay, PoseStack poseStack, MultiBufferSource bufferSource, LivingEntityRenderer<?, ?> renderer) {
        return new RenderLivingEntityEvent.Pre<>() {

            @Override
            public int lightmap() {
                return lightmap;
            }

            @Override
            public int overlay() {
                return overlay;
            }

            @Override
            public S renderState() {
                return AbstractRenderState.wrap(entity);
            }

            @Override
            public IGraphicsContext context() {
                return AbstractGraphicsRenderer.wrap(poseStack, bufferSource);
            }

            @Override
            public ILivingEntityRenderer<T, S, ?> entityRenderer() {
                return AbstractLivingEntityRenderer.wrap(renderer);
            }
        };
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> RenderLivingEntityEvent.Post<T, S> post(T entity, int lightmap, int overlay, PoseStack poseStack, MultiBufferSource bufferSource, LivingEntityRenderer<?, ?> renderer) {
        return new RenderLivingEntityEvent.Post<>() {

            @Override
            public int lightmap() {
                return lightmap;
            }

            @Override
            public int overlay() {
                return overlay;
            }

            @Override
            public S renderState() {
                return AbstractRenderState.wrap(entity);
            }

            @Override
            public IGraphicsContext context() {
                return AbstractGraphicsRenderer.wrap(poseStack, bufferSource);
            }

            @Override
            public ILivingEntityRenderer<T, S, ?> entityRenderer() {
                return AbstractLivingEntityRenderer.wrap(renderer);
            }
        };
    }
}
