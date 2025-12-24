package moe.plushie.armourers_workshop.core.client.render.plugin;

import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRendererContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.init.event.client.RenderEntityEvent;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class EntityRenderPlugin<T extends Entity, S extends EntityRenderState> {

    protected final int version;

    protected BakedArmatureTransformer transformer;

    public EntityRenderPlugin(EntityRendererContext context) {
        this.version = context.version();
    }

    protected static <T extends Entity, S extends EntityRenderState, P extends EntityRenderPlugin<T, S>> void _prepare(Class<?> clazz, RenderEntityEvent.Setup<T, S> event, Consumer<P> handler, Function<EntityRendererContext, P> factory) {
        var renderState = event.renderState();
        // noinspection unchecked
        var renderPlugin = (P) renderState.renderPlugin();
        var rendererContext = EntityRendererContext.of(event.entityRenderer());
        if (!clazz.isInstance(renderPlugin) || !renderPlugin.isValid(rendererContext)) {
            renderPlugin = factory.apply(rendererContext);
            renderState.setRenderPlugin(renderPlugin);
            if (renderPlugin == null) {
                return; // can't create.
            }
        }
        renderPlugin.init(renderState, event.partialTick(), event.entityRenderer());
        renderPlugin.prepare(renderState, event.entity(), event.partialTick());
        if (handler != null) {
            handler.accept(renderPlugin);
        }
    }

    protected static <T extends Entity, S extends EntityRenderState, P extends EntityRenderPlugin<T, S>> void _activate(Class<?> clazz, RenderEntityEvent.Pre<T, S> event, Consumer<P> handler) {
        var renderState = event.renderState();
        // noinspection unchecked
        var renderPlugin = (P) renderState.renderPlugin();
        if (clazz.isInstance(renderPlugin)) {
            if (handler != null) {
                handler.accept(renderPlugin);
            }
            renderPlugin.activate(renderState, event.lightmap(), event.overlay(), event.context());
            AbstractRenderState.activate(renderState);
        }
    }

    protected static <T extends Entity, S extends EntityRenderState, P extends EntityRenderPlugin<T, S>> void _deactivate(Class<?> clazz, RenderEntityEvent.Post<T, S> event, Consumer<P> handler) {
        var renderState = event.renderState();
        // noinspection unchecked
        var renderPlugin = (P) renderState.renderPlugin();
        if (clazz.isInstance(renderPlugin)) {
            AbstractRenderState.deactivate(renderState);
            renderPlugin.deactivate(renderState, event.lightmap(), event.overlay(), event.context());
            if (handler != null) {
                handler.accept(renderPlugin);
            }
        }
    }

    protected void init(S renderState, float partialTick, IEntityRenderer<T, S> renderer) {
    }

    protected void prepare(S renderState, T entity, float partialTick) {
        updateTransformerIfNeeded();
        if (transformer != null) {
            transformer.prepare(renderState, entity, partialTick);
        }
    }

    protected void activate(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        updateTransformerIfNeeded();
        if (transformer != null) {
            transformer.activate(renderState, lightmap, overlay, context);
        }
    }

    protected void deactivate(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        if (transformer != null) {
            transformer.deactivate(renderState, lightmap, overlay, context);
        }
    }

    protected void updateTransformerIfNeeded() {
        // nop
    }

    public BakedArmature getArmature(BakedArmature armature) {
        if (transformer != null) {
            transformer.applyTo(armature);
            return armature;
        }
        return null;
    }

    public boolean isValid(EntityRendererContext context) {
        return version == context.version();
    }

    public ArmatureTransformerContext context() {
        if (transformer != null) {
            return transformer.context();
        }
        return null;
    }
}

