package moe.plushie.armourers_workshop.core.api;

import moe.plushie.armourers_workshop.core.skin.SkinDyeType;
import net.minecraft.util.ResourceLocation;

public interface ISkinPaintType {

    int getId();

    SkinDyeType getDyeType();

    float getU();

    float getV();

    ResourceLocation getRegistryName();
}
