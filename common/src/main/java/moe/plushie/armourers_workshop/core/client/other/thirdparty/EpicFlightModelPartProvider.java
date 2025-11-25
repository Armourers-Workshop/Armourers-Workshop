package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import java.util.function.Function;

public class EpicFlightModelPartProvider {

    private final Function<String, EpicFlightModelPart> factory;

    public EpicFlightModelPartProvider(Function<String, EpicFlightModelPart> factory) {
        this.factory = factory;
    }

    public EpicFlightModelPart build(String name) {
        return factory.apply(name);
    }
}
