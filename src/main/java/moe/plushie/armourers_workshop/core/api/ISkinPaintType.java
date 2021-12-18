package moe.plushie.armourers_workshop.core.api;

public interface ISkinPaintType {

    int getId();

    int getIndex();


//    ExtraColourType getColourType();
//void setColourChannelIndex(int channelIndex);

    float getU();

    float getV();


    boolean hasAverageColourChannel();

    int getChannelIndex();

    String getRegistryName();
}
