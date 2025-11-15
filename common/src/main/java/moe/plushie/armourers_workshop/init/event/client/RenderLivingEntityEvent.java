package moe.plushie.armourers_workshop.init.event.client;

import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public interface RenderLivingEntityEvent<T extends LivingEntity, S extends LivingEntityRenderState> extends RenderEntityEvent<T, S> {

    @Override
    ILivingEntityRenderer<T, S, ?> entityRenderer();

    interface Setup<T extends LivingEntity, S extends LivingEntityRenderState> extends RenderEntityEvent.Setup<T, S>, RenderLivingEntityEvent<T, S> {
    }

    interface Pre<T extends LivingEntity, S extends LivingEntityRenderState> extends RenderEntityEvent.Pre<T, S>, RenderLivingEntityEvent<T, S> {
    }

    interface Post<T extends LivingEntity, S extends LivingEntityRenderState> extends RenderEntityEvent.Post<T, S>, RenderLivingEntityEvent<T, S> {
    }
}
