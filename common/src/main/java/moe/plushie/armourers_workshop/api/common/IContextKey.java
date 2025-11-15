package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;

public interface IContextKey<T> {

    IResourceLocation registryName();
}
