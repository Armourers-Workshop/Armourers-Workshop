package moe.plushie.armourers_workshop.api.skin;

public interface ISkinPaintType extends ISkinRegistryEntry {

    int getId();

    int getIndex();

    float getU();

    float getV();

    ISkinDyeType getDyeType();
}
