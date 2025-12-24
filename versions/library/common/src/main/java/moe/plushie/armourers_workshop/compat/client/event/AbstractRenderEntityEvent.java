package moe.plushie.armourers_workshop.compat.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.entity.AbstractEntityRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.init.event.client.RenderEntityEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

@Available("[1.16, 1.26)")
public class AbstractRenderEntityEvent {

    public static <T extends Entity, S extends EntityRenderState> RenderEntityEvent.Setup<T, S> setup(T entity, float partialTick, EntityRenderer<?> renderer) {
        return new RenderEntityEvent.Setup<>() {

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
            public IEntityRenderer<T, S> entityRenderer() {
                return AbstractEntityRenderer.wrap(renderer);
            }
        };
    }

    public static <T extends Entity, S extends EntityRenderState> RenderEntityEvent.Pre<T, S> pre(T entity, int lightmap, int overlay, PoseStack poseStack, MultiBufferSource bufferSource, EntityRenderer<?> renderer) {
        return new RenderEntityEvent.Pre<>() {

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
            public IEntityRenderer<T, S> entityRenderer() {
                return AbstractEntityRenderer.wrap(renderer);
            }
        };
    }

    public static <T extends Entity, S extends EntityRenderState> RenderEntityEvent.Post<T, S> post(T entity, int lightmap, int overlay, PoseStack poseStack, MultiBufferSource bufferSource, EntityRenderer<?> renderer) {
        return new RenderEntityEvent.Post<>() {

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
            public IEntityRenderer<T, S> entityRenderer() {
                return AbstractEntityRenderer.wrap(renderer);
            }
        };
    }
}
