package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.client.state.IEntityRenderState;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

public interface IEntityRenderer<T extends Entity, S extends IEntityRenderState> {

    interface Provider<T extends Entity> {

        IEntityRenderer<T, ?> create(Context context);
    }

    interface Context extends Supplier<Object> {
    }
}
