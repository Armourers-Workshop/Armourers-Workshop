package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;

@SuppressWarnings("unused")
public interface ISpecialModelRendererBuilder<T extends ISpecialModelRenderer<?>> extends IRegistryBuilder<ISpecialModelRendererType<T>> {
}

