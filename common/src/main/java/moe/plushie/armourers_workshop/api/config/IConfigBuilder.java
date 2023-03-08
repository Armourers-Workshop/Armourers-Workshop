package moe.plushie.armourers_workshop.api.config;

import java.util.List;
import java.util.function.Predicate;

public interface IConfigBuilder {

    IConfigBuilder builder();

    default IConfigValue<Boolean> define(String path, boolean defaultValue, String... description) {
        return builder().define(path, defaultValue, description);
    }

    default IConfigValue<Integer> defineInRange(String path, int defaultValue, int min, int max, String... description) {
        return builder().defineInRange(path, defaultValue, min, max, description);
    }

    default IConfigValue<Double> defineInRange(String path, double defaultValue, double min, double max, String... description) {
        return builder().defineInRange(path, defaultValue, min, max, description);
    }

    default <T> IConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, String... description) {
        return defineList(path, defaultValue, v -> true, description);
    }

    default <T> IConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator, String... description) {
        return builder().defineList(path, defaultValue, elementValidator, description);
    }

    default void defineCategory(String name, String description, Runnable runnable) {
        builder().defineCategory(name, description, runnable);
    }

    default IConfigSpec build() {
        return builder().build();
    }
}
