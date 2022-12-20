package moe.plushie.armourers_workshop.api.client.model;

import moe.plushie.armourers_workshop.api.data.IExtraDateStorage;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Collection;

public interface IModelHolder<T extends Model> extends IExtraDateStorage {

    default boolean isBaby() {
        return false;
    }

    default float getBabyScale() {
        return 1f;
    }

    default IVector3f getBabyOffset() {
        return null;
    }

    ModelPart getPart(String name);

    Collection<ModelPart> getAllParts();
}

