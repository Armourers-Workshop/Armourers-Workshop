package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.client.state.ILivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public interface ILivingEntityRenderer<T extends LivingEntity, S extends ILivingEntityRenderState, M extends IEntityModel<S>> extends IEntityRenderer<T, S> {

    M abi$getModel();

    List<Object> abi$getLayers();
}
