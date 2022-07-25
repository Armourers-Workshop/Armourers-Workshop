package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.resources.ResourceLocation;

public interface ISkinPaintType {

    int getId();

    int getIndex();

    float getU();

    float getV();

    ISkinDyeType getDyeType();

    ResourceLocation getRegistryName();
}
