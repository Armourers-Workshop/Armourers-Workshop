package moe.plushie.armourers_workshop.compat.client.entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

@Available("[1.16, 1.26)")
@OnlyIn(Dist.CLIENT)
public class AbstractVehicleUpdaterImpl {

    protected void findEntity(EntityRenderState renderState, Consumer<Entity> handler) {
        AbstractRenderState.unwrap(renderState, (entity, f, g) -> handler.accept(entity));
    }
}
