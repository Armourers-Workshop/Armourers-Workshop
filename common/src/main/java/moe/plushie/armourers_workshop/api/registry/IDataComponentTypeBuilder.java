package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.common.IDataComponentType;

public interface IDataComponentTypeBuilder<T> extends IRegistryBuilder<IDataComponentType<T>> {

    IDataComponentTypeBuilder<T> tag(String tag);
}
