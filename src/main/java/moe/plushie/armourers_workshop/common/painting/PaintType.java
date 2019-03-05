package moe.plushie.armourers_workshop.common.painting;

import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.TranslateUtils;

public class PaintType {

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
    }

    public PaintType(int id, int markerIndex, String name) {
        this(id, markerIndex, false, name);
    }

    public ExtraColourType getColourType() {
        return colourType;
    }

    public PaintType setExtraColourType(ExtraColourType colourType) {
        this.colourType = colourType;
        return this;
    }
    
    public float getU() {
        return textureU;
    }
    
    public float getV() {
        return textureV;
    }

    public PaintType setTextureUV(float u, float v) {
        this.textureU = u;
        this.textureV = v;
        return this;
    }

    public int getId() {
        return id;
    }

    public int getMarkerIndex() {
        return markerIndex;
    }

    public boolean hasAverageColourChannel() {
        return hasColourChannel;
    }

    public int getChannelIndex() {
        return channelIndex;
    }

    public void setColourChannelIndex(int channelIndex) {
        this.channelIndex = channelIndex;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        String unlocalizedText = "paintType." + LibModInfo.ID.toLowerCase() + ":";
        unlocalizedText += name.toLowerCase() + ".name";
        return TranslateUtils.translate(unlocalizedText);
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
