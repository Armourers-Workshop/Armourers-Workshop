package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.math.ITransformf;

import java.util.function.Function;

public interface ITransformProvider {

    void setTransformProvider(Function<String, ITransformf> provider);

    Function<String, ITransformf> getTransformProvider();
}
