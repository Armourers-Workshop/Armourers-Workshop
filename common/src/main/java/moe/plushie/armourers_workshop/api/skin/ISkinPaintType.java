package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.registry.IRegistryEntry;

public interface ISkinPaintType extends IRegistryEntry {

    int getId();

    int getIndex();

    float getU();

    float getV();

    ISkinDyeType getDyeType();
}
