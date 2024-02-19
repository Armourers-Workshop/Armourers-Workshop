package moe.plushie.armourers_workshop.compatibility.forge;

import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Available("[1.21, )")
public class AbstractForgeConfig {

    public static void register(Type type, Spec spec) {
        ModLoadingContext.get().registerConfig(type.value, spec.value);
    }

    public static class Spec {

        protected final ModConfigSpec value;

        public Spec(Object value) {
            this((ModConfigSpec) value);
        }

        public Spec(ModConfigSpec value) {
            this.value = value;
        }

        public void save() {
            value.save();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Spec spec1 = (Spec) o;
            return Objects.equal(value, spec1.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    public static class ConfigValue<T> {

        protected final ModConfigSpec.ConfigValue<T> value;

        public ConfigValue(ModConfigSpec.ConfigValue<T> value) {
            this.value = value;
        }

        public void set(T newValue) {
            value.set(newValue);
        }

        public T get() {
            return value.get();
        }
    }

    public static class Builder {

        protected final ModConfigSpec.Builder builder;

        public Builder(ModConfigSpec.Builder builder) {
            this.builder = builder;
        }

        public static <T> Pair<T, Spec> configure(Function<Builder, T> consumer) {
            Pair<T, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(it -> consumer.apply(new Builder(it)));
            return Pair.of(pair.getLeft(), new Spec(pair.getValue()));
        }

        public void push(String name) {
            builder.push(name);
        }

        public void pop() {
            builder.pop();
        }

        public ConfigValue<Boolean> define(String path, boolean defaultValue) {
            return new ConfigValue<>(builder.define(path, defaultValue));
        }

        public ConfigValue<Integer> defineInRange(String path, int defaultValue, int minValue, int maxValue) {
            return new ConfigValue<>(builder.defineInRange(path, defaultValue, minValue, maxValue));
        }

        public ConfigValue<Double> defineInRange(String path, double defaultValue, double minValue, double maxValue) {
            return new ConfigValue<>(builder.defineInRange(path, defaultValue, minValue, maxValue));
        }

        public <T> ConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
            return new ConfigValue<>(builder.defineList(path, defaultValue, elementValidator));
        }

        public Builder comment(String... comment) {
            builder.comment(comment);
            return this;
        }
    }

    public enum Type {

        COMMON(ModConfig.Type.COMMON),
        CLIENT(ModConfig.Type.CLIENT),
        SERVER(ModConfig.Type.SERVER);

        final ModConfig.Type value;

        Type(ModConfig.Type value) {
            this.value = value;
        }

        public String getExtension() {
            return value.extension();
        }
    }
}
