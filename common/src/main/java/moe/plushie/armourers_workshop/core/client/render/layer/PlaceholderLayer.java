package moe.plushie.armourers_workshop.core.client.render.layer;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.renderer.layer.AbstractRenderLayer;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

@OnlyIn(Dist.CLIENT)
public class PlaceholderLayer<T extends Entity, S extends EntityRenderState, M extends IEntityModel<S>> extends AbstractRenderLayer<T, S, M> {

    public PlaceholderLayer(IEntityRenderer<T, S> renderer) {
        super(renderer);
    }

    @Override
    protected void abi$render(S renderState, int lightmap, int overlay, float netHeadYaw, float headPitch, IGraphicsContext context) {
        // none
    }
}
