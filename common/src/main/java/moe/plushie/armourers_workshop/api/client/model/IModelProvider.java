package moe.plushie.armourers_workshop.api.client.model;

import net.minecraft.world.entity.Entity;

public interface IModelProvider<T extends Entity> {

    IModel getModel(T entity);
}
