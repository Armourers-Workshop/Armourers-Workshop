package moe.plushie.armourers_workshop.api.data;

import net.minecraft.resources.ResourceLocation;

public interface IDataPackBuilder {

    void append(IDataPackObject object, ResourceLocation location);

    void build();
}
