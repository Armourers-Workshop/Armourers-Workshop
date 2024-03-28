package moe.plushie.armourers_workshop.api.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;

public interface IEntityModelProvider<T extends Entity, M extends EntityModel<T>> {

    M getModel(T entity);
}
