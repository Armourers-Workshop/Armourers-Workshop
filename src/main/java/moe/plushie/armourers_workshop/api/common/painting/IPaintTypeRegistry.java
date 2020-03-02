package moe.plushie.armourers_workshop.api.common.painting;

import java.util.ArrayList;

public interface IPaintTypeRegistry {

    public boolean registerPaintType(IPaintType paintType);

    public int getExtraChannels();

    public IPaintType getPaintTypeFromColour(int trgb);

    public int setPaintTypeOnColour(IPaintType paintType, int colour);

    public IPaintType getPaintTypeFormByte(byte index);

    public IPaintType getPaintTypeFormName(String name);

    public ArrayList<IPaintType> getRegisteredTypes();

    public IPaintType getPaintTypeFromIndex(int index);
}
