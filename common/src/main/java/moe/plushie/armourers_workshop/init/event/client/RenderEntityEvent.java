package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public interface RenderEntityEvent<T extends Entity, S extends EntityRenderState> {

    S renderState();

    IEntityRenderer<T, S> entityRenderer();

    interface Setup<T extends Entity, S extends EntityRenderState> extends RenderEntityEvent<T, S> {

        float partialTicks();

        T entity();
    }

    interface Pre<T extends Entity, S extends EntityRenderState> extends RenderEntityEvent<T, S> {

        int lightmap();

        int overlay();

        IGraphicsContext context();
    }

    interface Post<T extends Entity, S extends EntityRenderState> extends RenderEntityEvent<T, S> {

        int lightmap();

        int overlay();

        IGraphicsContext context();
    }
}
