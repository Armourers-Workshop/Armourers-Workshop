package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @SideOnly(Side.CLIENT)
    public String getLocalizedName();
}
