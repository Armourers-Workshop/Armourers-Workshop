package moe.plushie.armourers_workshop.api.common;

import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public interface IItemPropertiesProvider {

    void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder);
}
