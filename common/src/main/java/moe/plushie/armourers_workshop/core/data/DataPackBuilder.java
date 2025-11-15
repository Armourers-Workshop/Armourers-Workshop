package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public interface DataPackBuilder {

    void append(IODataObject object, OpenResourceLocation location);

    void build();
}
