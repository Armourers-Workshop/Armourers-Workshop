package moe.plushie.armourers_workshop.common.painting;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PaintType implements IPaintType {

    public static final ArrayList<IPaintType> PAINT_TYPES = new ArrayList<IPaintType>();

    private final int id;
    private final int markerIndex;
    private final String name;
    private boolean hasColourChannel;
    private int channelIndex;
    private ExtraColourType colourType;
    private float textureU;
    private float textureV;

    public PaintType(int id, int markerIndex, boolean hasColourChannel, String name) {
        this.id = id;
        this.markerIndex = markerIndex;
        this.hasColourChannel = hasColourChannel;
        this.name = name;
        this.textureU = 0;
        this.textureV = 0;
        PAINT_TYPES.add(this);
    }

    public PaintType(int id, int markerIndex, String name) {
        this(id, markerIndex, false, name);
    }

    @Override
    public ExtraColourType getColourType() {
        return colourType;
    }

    public PaintType setExtraColourType(ExtraColourType colourType) {
        this.colourType = colourType;
        return this;
    }

    @Override
    public float getU() {
        return textureU;
    }

    @Override
    public float getV() {
        return textureV;
    }

    public PaintType setTextureUV(float u, float v) {
        this.textureU = u;
        this.textureV = v;
        return this;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getMarkerIndex() {
        return markerIndex;
    }

    @Override
    public boolean hasAverageColourChannel() {
        return hasColourChannel;
    }

    @Override
    public int getChannelIndex() {
        return channelIndex;
    }

    @Override
    public void setColourChannelIndex(int channelIndex) {
        this.channelIndex = channelIndex;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnlocalizedName() {
        String unlocalizedText = "paintType." + LibModInfo.ID.toLowerCase() + ":";
        unlocalizedText += name.toLowerCase() + ".name";
        return unlocalizedText;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getLocalizedName() {
        return TranslateUtils.translate(getUnlocalizedName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaintType other = (PaintType) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
