package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.armature.core.DefaultArmaturePluginContext;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class EntityRenderPatch<T extends Entity> {

    protected final int libraryVersion;

    protected final SkinRenderContext renderingContext = new SkinRenderContext();
    protected final DefaultArmaturePluginContext pluginContext = new DefaultArmaturePluginContext();

    protected BakedArmatureTransformer transformer;

    public EntityRenderPatch(EntityRenderData renderData) {
        this.libraryVersion = SkinRendererManager.getVersion();
        this.pluginContext.setRenderData(renderData);
        this.renderingContext.setRenderData(renderData);
    }

    protected static <T extends Entity, P extends EntityRenderPatch<? super T>> void _activate(Class<?> clazz, T entity, float partialTicks, int packedLight, PoseStack poseStackIn, EntityRenderer<?> entityRenderer, Consumer<P> handler, Function<EntityRenderData, EntityRenderPatch<? extends T>> provider) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var renderPatch = renderData.getRenderPatch();
        if (!clazz.isInstance(renderPatch) || !renderPatch.isValid()) {
            var renderPatch1 = provider.apply(renderData);
            renderPatch = ObjectUtils.unsafeCast(renderPatch1);
            renderData.setRenderPatch(renderPatch);
            if (renderPatch == null) {
                return; // can't create.
            }
        }
        renderPatch.onInit(entity, partialTicks, packedLight, poseStackIn, entityRenderer);
        renderPatch.onActivate(entity);
        if (handler != null) {
            handler.accept(ObjectUtils.unsafeCast(renderPatch));
        }
    }

    protected static <T extends Entity, P extends EntityRenderPatch<? super T>> void _apply(Class<?> clazz, T entity, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, Consumer<P> handler) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            var renderPatch = renderData.getRenderPatch();
            if (clazz.isInstance(renderPatch)) {
                if (handler != null) {
                    handler.accept(ObjectUtils.unsafeCast(renderPatch));
                }
                renderPatch.onApply(entity, poseStackIn, bufferSourceIn);
            }
        }
    }

    protected static <T extends Entity, P extends EntityRenderPatch<? super T>> void _deactivate(Class<?> clazz, T entity, Consumer<P> handler) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            var renderPatch = renderData.getRenderPatch();
            if (clazz.isInstance(renderPatch)) {
                renderPatch.onDeactivate(entity);
                if (handler != null) {
                    handler.accept(ObjectUtils.unsafeCast(renderPatch));
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

    public boolean isValid() {
        return libraryVersion == SkinRendererManager.getVersion();
    }

    public BakedArmatureTransformer getTransformer() {
        return transformer;
    }

    public DefaultArmaturePluginContext getPluginContext() {
        return pluginContext;
    }

    public SkinRenderContext getRenderingContext() {
        return renderingContext;
    }
}

