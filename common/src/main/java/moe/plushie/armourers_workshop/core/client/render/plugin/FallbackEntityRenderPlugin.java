package moe.plushie.armourers_workshop.core.client.render.plugin;

import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRendererContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.init.event.client.RenderEntityEvent;
import net.minecraft.world.entity.Entity;

public class FallbackEntityRenderPlugin<T extends Entity, S extends EntityRenderState> extends EntityRenderPlugin<T, S> {

    private final BakedArmature armature;

    public FallbackEntityRenderPlugin(BakedArmatureTransformer transformer, EntityRendererContext context) {
        super(context);
        this.transformer = transformer;
        this.armature = BakedArmature.mutableBy(transformer.armature());
    }

    public static <T extends Entity, S extends EntityRenderState> void prepare(RenderEntityEvent.Setup<T, S> event) {
        _prepare(FallbackEntityRenderPlugin.class, event, null, (rendererStorage) -> {
            var transformer = rendererStorage.getTransformer(null);
            if (transformer != null) {
                return new FallbackEntityRenderPlugin<>(transformer, rendererStorage);
            }
            return null;
        });
    }

    public static <T extends Entity, S extends EntityRenderState> void activate(RenderEntityEvent.Pre<T, S> event) {
        _activate(FallbackEntityRenderPlugin.class, event, null);
    }

    public static <T extends Entity, S extends EntityRenderState> void deactivate(RenderEntityEvent.Post<T, S> event) {
        _deactivate(FallbackEntityRenderPlugin.class, event, null);
    }

    @Override
    protected void activate(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        context.saveGraphicsState();
        super.activate(renderState, lightmap, overlay, context);
    }

    @Override
    protected void deactivate(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        super.deactivate(renderState, lightmap, overlay, context);
        context.restoreGraphicsState();
    }

    @Override
    public BakedArmature getArmature(BakedArmature armature) {
        if (armature == null) {
            armature = this.armature;
        }
        return super.getArmature(armature);
    }
}
