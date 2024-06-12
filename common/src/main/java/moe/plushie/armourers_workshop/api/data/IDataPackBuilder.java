package moe.plushie.armourers_workshop.api.data;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;

public interface IDataPackBuilder {

    void append(IDataPackObject object, IResourceLocation location);

    void build();
}
