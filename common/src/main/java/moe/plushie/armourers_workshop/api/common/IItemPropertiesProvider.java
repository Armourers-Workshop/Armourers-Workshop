package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;

import java.util.function.BiConsumer;

public interface IItemPropertiesProvider {

    void createModelProperties(BiConsumer<IResourceLocation, IItemModelProperty> builder);
}
