package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.api.config.IConfigValue;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigSpec;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigBuilderImpl {

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
            SpecProxy proxy = (SpecProxy)spec;
            proxy.spec = pair.getValue();
            proxy.type = type.extension();
        }
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID);
        if (container.isPresent()) {
            FabricConfig ignored = new FabricConfig(type, pair.getValue(), container.get());
        }
        return spec;
    }

    public static class SpecProxy implements IConfigSpec {

        private String type = "";
        private FabricConfigSpec spec;

        private Map<String, Object> snapshot;
        private final ArrayList<Runnable> listeners = new ArrayList<>();
        private final HashMap<String, ValueProxy<Object>> values;

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
            ModLog.debug("apply {} snapshot from server", type);
            snapshot.forEach((key, object) -> {
                ValueProxy<Object> value = values.get(key);
                if (value != null && value.setter != null) {
                    value.setter.accept(object);
                }
            });
            this.setChanged();
        }

        @Override
        public void reload() {
            // when the server snapshot is applied, ignore reload call.
            if (this.snapshot != null) {
                return;
            }
            ModLog.debug("apply {} changes from spec", type);
            this.values.forEach((key, value) -> {
                if (value.setter != null) {
                    value.setter.accept(value.read());
                }
            });
            this.setChanged();
        }

        @Override
        public void save() {
            // when the server snapshot is applied, ignore reload call.
            if (this.snapshot != null) {
                return;
            }
            ModLog.debug("save {} changes into spec", type);
            this.values.forEach((key, value) -> {
                if (value.getter != null) {
                    value.write(value.getter.get());
                }
            });
            if (this.spec != null) {
                this.spec.save();
                this.setChanged();
            }
        }

        @Override
        public void notify(Runnable action) {
            this.listeners.add(action);
        }

        private void setChanged() {
            // when the config did changes, we need notify to all listeners.
            this.listeners.forEach(Runnable::run);
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

        String root = "";
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
            String oldRoot = root;
            builder.comment(description);
            builder.push(name);
            root = root + name + ".";
            runnable.run();
            root = oldRoot;
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
            values.put(root + path, ObjectUtils.unsafeCast(proxy));
            return proxy;
        }
    }
}
