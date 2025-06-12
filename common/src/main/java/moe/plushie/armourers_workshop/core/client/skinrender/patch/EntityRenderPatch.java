package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.armature.core.DefaultArmaturePluginContext;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRendererContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public abstract class EntityRenderPatch<T extends Entity> {

    protected final int version;

    protected final SkinRenderContext renderingContext = new SkinRenderContext();
    protected final DefaultArmaturePluginContext pluginContext = new DefaultArmaturePluginContext();

    protected BakedArmatureTransformer transformer;

    public EntityRenderPatch(EntityRenderData renderData, EntityRendererContext context) {
        this.version = context.version();
        this.pluginContext.setRenderData(renderData);
        this.renderingContext.setRenderData(renderData);
    }

    protected static <T extends Entity, P extends EntityRenderPatch<? super T>> void _activate(Class<?> clazz, T entity, float partialTicks, int packedLight, PoseStack poseStackIn, EntityRenderer<?> entityRenderer, Consumer<P> handler, Factory<? extends T> factory) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var renderPatch = renderData.renderPatch();
        var rendererContext = EntityRendererContext.of(entityRenderer);
        if (!clazz.isInstance(renderPatch) || !renderPatch.isValid(rendererContext)) {
            var renderPatch1 = factory.create(renderData, rendererContext);
            renderPatch = Objects.unsafeCast(renderPatch1);
            renderData.setRenderPatch(renderPatch);
            if (renderPatch == null) {
                return; // can't create.
            }
        }
        renderPatch.onInit(entity, partialTicks, packedLight, poseStackIn, entityRenderer);
        renderPatch.onActivate(entity);
        if (handler != null) {
            handler.accept(Objects.unsafeCast(renderPatch));
        }
    }

    protected static <T extends Entity, P extends EntityRenderPatch<? super T>> void _apply(Class<?> clazz, T entity, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, Consumer<P> handler) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            var renderPatch = renderData.renderPatch();
            if (clazz.isInstance(renderPatch)) {
                if (handler != null) {
                    handler.accept(Objects.unsafeCast(renderPatch));
                }
                renderPatch.onApply(entity, poseStackIn, bufferSourceIn);
            }
        }
    }

    protected static <T extends Entity, P extends EntityRenderPatch<? super T>> void _deactivate(Class<?> clazz, T entity, Consumer<P> handler) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            var renderPatch = renderData.renderPatch();
            if (clazz.isInstance(renderPatch)) {
                renderPatch.onDeactivate(entity);
                if (handler != null) {
                    handler.accept(Objects.unsafeCast(renderPatch));
                }
            }
        }
    }

    protected void onInit(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, EntityRenderer<?> entityRenderer) {
        pluginContext.setPartialTicks(partialTicks);
        pluginContext.setAnimationTicks(TickUtils.animationTicks());
        pluginContext.setLightmap(packedLight);
        pluginContext.setPoseStack(AbstractPoseStack.wrap(poseStackIn));
    }

    protected void onActivate(T entity) {
        if (this.transformer != null) {
            this.transformer.prepare(entity, pluginContext);
        }
    }

    protected void onApply(T entity, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        if (this.transformer != null) {
            this.transformer.activate(entity, pluginContext);
        }
    }

    protected void onDeactivate(T entity) {
        if (this.transformer != null) {
            this.transformer.deactivate(entity, pluginContext);
        }
    }

    public boolean isValid(EntityRendererContext context) {
        return version == context.version();
    }

    public BakedArmatureTransformer transformer() {
        return transformer;
    }

    public DefaultArmaturePluginContext pluginContext() {
        return pluginContext;
    }

    public SkinRenderContext renderingContext() {
        return renderingContext;
    }

    @FunctionalInterface
    public interface Factory<T extends Entity> {

        EntityRenderPatch<T> create(EntityRenderData renderData, EntityRendererContext rendererStorage);
    }
}

