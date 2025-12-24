package moe.plushie.armourers_workshop.core.client.render.plugin;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.core.client.other.EntityRendererContext;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntityRenderPlugin<T extends LivingEntity, S extends LivingEntityRenderState> extends EntityRenderPlugin<T, S> {

    protected IEntityModel<?> entityModel;
    protected ILivingEntityRenderer<T, S, ?> entityRenderer;

    public LivingEntityRenderPlugin(EntityRendererContext rendererContext) {
        super(rendererContext);
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> void prepare(RenderLivingEntityEvent.Setup<T, S> event) {
        _prepare(LivingEntityRenderPlugin.class, event, null, LivingEntityRenderPlugin::new);
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> void activate(RenderLivingEntityEvent.Pre<T, S> event) {
        _activate(LivingEntityRenderPlugin.class, event, null);
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> void deactivate(RenderLivingEntityEvent.Post<T, S> event) {
        _deactivate(LivingEntityRenderPlugin.class, event, null);
    }

    @Override
    protected void init(S renderState, float partialTick, IEntityRenderer<T, S> renderer) {
        if (renderer instanceof ILivingEntityRenderer<T, S, ?> renderer1) {
            init(renderState, partialTick, renderer1);
        }
    }

    protected void init(S renderState, float partialTick, ILivingEntityRenderer<T, S, ?> renderer) {
        super.init(renderState, partialTick, renderer);
        this.entityRenderer = renderer;
    }

    @Override
    protected void updateTransformerIfNeeded() {
        var entityModel = entityRenderer.abi$getModel();
        if (this.entityModel == entityModel) {
            return;
        }
        this.entityModel = entityModel;
        this.transformer = EntityRendererContext.of(entityRenderer).getTransformer(entityModel);
    }
}
