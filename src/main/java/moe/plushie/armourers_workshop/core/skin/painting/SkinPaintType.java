package moe.plushie.armourers_workshop.core.skin.painting;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.common.IExtraColours.ExtraColourType;

public class SkinPaintType implements ISkinPaintType {

    private final int id;
    private final int index;

    private boolean hasColourChannel;
    private int channelIndex;
    private ExtraColourType colourType;
    private float textureU;
    private float textureV;
    private String registryName;

    public SkinPaintType(int index, int id, boolean hasColourChannel) {
        this.id = id;
        this.index = index;
        this.hasColourChannel = hasColourChannel;
        this.textureU = 0;
        this.textureV = 0;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    //    @Override
//    public ExtraColourType getColourType() {
//        return colourType;
//    }
//
    public SkinPaintType setExtraColourType(ExtraColourType colourType) {
        this.colourType = colourType;
        return this;
    }

    public SkinPaintType setTextureUV(float u, float v) {
        this.textureU = u;
        this.textureV = v;
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


    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasAverageColourChannel() {
        return hasColourChannel;
    }

    @Override
    public int getChannelIndex() {
        return channelIndex;
    }

//    @Override
//    public void setColourChannelIndex(int channelIndex) {
//        this.channelIndex = channelIndex;
//    }


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
        SkinPaintType other = (SkinPaintType) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
