package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.core.skin.data.SkinDyeType;
import net.minecraft.util.ResourceLocation;

public interface ISkinPaintType {

    int getId();

    float getU();

    float getV();

    SkinDyeType getDyeType();

    ResourceLocation getRegistryName();
}
