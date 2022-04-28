package moe.plushie.armourers_workshop.api.common;

import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;

public interface IItemModelPropertiesProvider {

    void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder);
}
