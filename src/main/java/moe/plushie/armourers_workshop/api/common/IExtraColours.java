package moe.plushie.armourers_workshop.api.common;

public interface IExtraColours {

    public int getColour(ExtraColourType type);

    public byte[] getColourBytes(ExtraColourType type);

    public void setColour(ExtraColourType type, int trgb);

    public void setColourBytes(ExtraColourType type, byte[] rgbt);

    public static enum ExtraColourType {
        SKIN, HAIR, EYE, MISC_1, MISC_2, MISC_3, MISC_4;
    }
}
