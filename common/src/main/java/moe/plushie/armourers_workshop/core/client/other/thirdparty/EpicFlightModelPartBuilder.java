package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import java.util.function.Consumer;
import java.util.function.Function;

public class EpicFlightModelPartBuilder {

    private final Function<String, Consumer<Boolean>> builder;

    public EpicFlightModelPartBuilder(Function<String, Consumer<Boolean>> builder) {
        this.builder = builder;
    }

    public Consumer<Boolean> build(String name) {
        return builder.apply(name);
    }
}
