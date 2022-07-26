package moe.plushie.armourers_workshop.api.config;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IConfigValue<T> {

    T read();

    void write(T value);

    void bind(Consumer<T> setter, Supplier<T> getter);
}
