package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.other.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.other.config.IConfigSpec;
import moe.plushie.armourers_workshop.api.other.config.IConfigValue;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.config.FabricConfig;
import moe.plushie.armourers_workshop.init.config.FabricConfigSpec;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigBuilderImpl {

    public static void reloadSpec(IConfigSpec spec, FabricConfigSpec forgeSpec) {
        SpecProxy proxy = ObjectUtils.safeCast(spec, SpecProxy.class);
        if (proxy != null && proxy.spec == forgeSpec) {
            proxy.reload();
        }
    }

    public static IConfigSpec createClientSpec() {
        return createSpec(FabricConfig.Type.CLIENT, proxy -> new ModConfigSpec.Client() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }

    public static IConfigSpec createCommonSpec() {
        return createSpec(FabricConfig.Type.COMMON, proxy -> new ModConfigSpec.Common() {
            public IConfigBuilder builder() {
                return proxy;
            }
        });
    }

    private static <T extends IConfigBuilder> IConfigSpec createSpec(FabricConfig.Type type, Function<BuilderProxy, T> applier) {
        Pair<T, FabricConfigSpec> pair = new FabricConfigSpec.Builder().configure(builder -> applier.apply(new BuilderProxy(builder)));
        IConfigSpec spec = pair.getKey().build();
        if (spec instanceof SpecProxy) {
            ((SpecProxy) spec).spec = pair.getValue();
        }
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ArmourersWorkshop.MOD_ID);
        if (container.isPresent()) {
            FabricConfig config = new FabricConfig(type, pair.getValue(), container.get());
        }
        return spec;
    }

    public static class SpecProxy implements IConfigSpec {

        protected FabricConfigSpec spec;
        protected HashMap<String, ValueProxy<Object>> values;
        protected Map<String, Object> snapshot;

        public SpecProxy(HashMap<String, ValueProxy<Object>> values) {
            this.values = values;
        }

        @Override
        public Map<String, Object> snapshot() {
            HashMap<String, Object> fields = new HashMap<>();
            values.forEach((key, value) -> {
                if (value.getter != null) {
                    fields.put(key, value.getter.get());
                }
            });
            return fields;
        }

        @Override
        public void apply(Map<String, Object> snapshot) {
            this.snapshot = snapshot;
            if (this.snapshot == null) {
                this.reload();
                return;
            }
            snapshot.forEach((key, object) -> {
                ValueProxy<Object> value = values.get(key);
                if (value.setter != null) {
                    value.write(object);
                }
            });
        }

        @Override
        public void reload() {
            // when the server snapshot is applied, ignore reload call.
            if (this.snapshot != null) {
                return;
            }
            this.values.forEach((key, value) -> {
                if (value.setter != null) {
                    value.setter.accept(value.read());
                }
            });
        }

        @Override
        public void save() {
            // when the server snapshot is applied, ignore reload call.
            if (this.snapshot != null) {
                return;
            }
            this.values.forEach((key, value) -> {
                if (value.getter != null) {
                    value.write(value.getter.get());
                }
            });
            if (this.spec != null) {
                this.spec.save();
            }
        }
    }

    public static class ValueProxy<T> implements IConfigValue<T> {

        protected final String path;
        protected final FabricConfigSpec.ConfigValue<T> configValue;

        protected Consumer<T> setter;
        protected Supplier<T> getter;

        public ValueProxy(String path, FabricConfigSpec.ConfigValue<T> configValue) {
            this.path = path;
            this.configValue = configValue;
        }

        @Override
        public T read() {
            return this.configValue.get();
        }

        @Override
        public void write(T value) {
            this.configValue.set(value);
        }

        @Override
        public void bind(Consumer<T> setter, Supplier<T> getter) {
            this.setter = setter;
            this.getter = getter;
        }
    }

    public static class BuilderProxy implements IConfigBuilder {

        HashMap<String, ValueProxy<Object>> values = new HashMap<>();
        FabricConfigSpec.Builder builder;

        BuilderProxy(FabricConfigSpec.Builder builder) {
            this.builder = builder;
        }

        @Override
        public IConfigValue<Boolean> define(String path, boolean defaultValue, String... description) {
            return put(path, builder.comment(description).define(path, defaultValue));
        }

        @Override
        public IConfigValue<Integer> defineInRange(String path, int defaultValue, int min, int max, String... description) {
            return put(path, builder.comment(description).defineInRange(path, defaultValue, min, max));
        }

        @Override
        public IConfigValue<Double> defineInRange(String path, double defaultValue, double min, double max, String... description) {
            return put(path, builder.comment(description).defineInRange(path, defaultValue, min, max));
        }

        @Override
        public <T> IConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator, String... description) {
            return put(path, builder.comment(description).defineList(path, defaultValue, elementValidator));
        }

        @Override
        public void defineCategory(String name, String description, Runnable runnable) {
            builder.comment(description);
            builder.push(name);
            runnable.run();
            builder.pop();
        }

        @Override
        public IConfigBuilder builder() {
            return this;
        }

        @Override
        public IConfigSpec build() {
            return new SpecProxy(values);
        }

        private <T> IConfigValue<T> put(String path, FabricConfigSpec.ConfigValue<T> value) {
            ValueProxy<T> proxy = new ValueProxy<>(path, value);
            values.put(path, ObjectUtils.unsafeCast(proxy));
            return proxy;
        }
    }
}
