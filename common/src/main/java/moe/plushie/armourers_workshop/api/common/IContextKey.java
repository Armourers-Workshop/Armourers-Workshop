package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.core.IResourceKey;

public interface IContextKey<T> {

    IResourceKey registryName();
}
