package moe.plushie.armourers_workshop.core.api.common.painting;

import moe.plushie.armourers_workshop.core.api.common.IExtraColours.ExtraColourType;

public interface IPaintType {

    public ExtraColourType getColourType();

    public float getU();

    public float getV();

    public int getId();

    public int getMarkerIndex();

    public boolean hasAverageColourChannel();

    public int getChannelIndex();

    public void setColourChannelIndex(int channelIndex);

    public String getName();

    public String getUnlocalizedName();

    public String getLocalizedName();
}
