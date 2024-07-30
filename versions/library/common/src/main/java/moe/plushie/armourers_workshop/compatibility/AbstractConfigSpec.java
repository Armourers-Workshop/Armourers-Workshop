package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.api.config.IConfigValue;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbstractConfigSpec implements IConfigSpec {

    private Object spec;
    private Runnable saver;

    private Map<String, Object> snapshot;

    private final Type type;
    private final ArrayList<Runnable> listeners = new ArrayList<>();
    private final HashMap<String, Value<Object>> values;

    public AbstractConfigSpec(Type type, HashMap<String, Value<Object>> values) {
        this.type = type;
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof AbstractConfigSpec spec1) {
            return Objects.equals(spec, spec1.spec);
        }
        return Objects.equals(spec, o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(spec);
    }

    @Override
    public Map<String, Object> snapshot() {
        var fields = new HashMap<String, Object>();
        values.forEach((key, value) -> {
            if (value.getter != null) {
                fields.put(key, value.getter.get());
            } else {
                fields.put(key, null);
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
            var value = values.get(key);
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
        if (this.saver != null) {
            this.saver.run();
            this.setChanged();
        }
    }

    @Override
    public void notify(Runnable action) {
        this.listeners.add(action);
    }

    protected <T> void bind(T config, Consumer<T> saver) {
        this.spec = config;
        this.saver = () -> saver.accept(config);
    }

    private void setChanged() {
        // when the config did changes, we need notify to all listeners.
        this.listeners.forEach(Runnable::run);
    }


    public enum Type {
        COMMON, CLIENT, SERVER
    }

    public static class Value<T> implements IConfigValue<T> {

        protected final String path;

        protected final Supplier<T> reader;
        protected final Consumer<T> writer;

        protected Consumer<T> setter;
        protected Supplier<T> getter;

        public Value(String path, Supplier<T> reader, Consumer<T> writer) {
            this.path = path;
            this.reader = reader;
            this.writer = writer;
        }

        @Override
        public T read() {
            return reader.get();
        }

        @Override
        public void write(T value) {
            writer.accept(value);
        }

        @Override
        public void bind(Consumer<T> setter, Supplier<T> getter) {
            this.setter = setter;
            this.getter = getter;
        }
    }

    public static abstract class Builder implements IConfigBuilder {

        protected String root = "";
        protected final HashMap<String, Value<Object>> values = new HashMap<>();

        @Override
        public void defineCategory(String name, String description, Runnable runnable) {
            var parent = root;
            comment(description);
            push(name);
            root = root + name + ".";
            runnable.run();
            root = parent;
            pop();
        }

        @Override
        public IConfigValue<Boolean> define(String path, boolean defaultValue, String... description) {
            return defineValue(path, comment(description).define(path, defaultValue));
        }

        @Override
        public IConfigValue<String> define(String path, String defaultValue, String... description) {
            return defineValue(path, comment(description).define(path, defaultValue));
        }

        @Override
        public IConfigValue<Integer> defineInRange(String path, int defaultValue, int min, int max, String... description) {
            return defineValue(path, comment(description).defineInRange(path, defaultValue, min, max));
        }

        @Override
        public IConfigValue<Double> defineInRange(String path, double defaultValue, double min, double max, String... description) {
            return defineValue(path, comment(description).defineInRange(path, defaultValue, min, max));
        }

        @Override
        public <T> IConfigValue<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator, String... description) {
            return defineValue(path, comment(description).defineList(path, defaultValue, elementValidator));
        }

        public <T> IConfigValue<T> defineValue(String path, Value<T> value) {
            values.put(root + path, ObjectUtils.unsafeCast(value));
            return value;
        }

        @Override
        public IConfigBuilder builder() {
            return this;
        }

        protected abstract Builder push(String name);

        protected abstract Builder pop();

        protected abstract Builder comment(String... comment);

        protected abstract Value<Boolean> define(String path, boolean defaultValue);

        protected abstract Value<String> define(String path, String defaultValue);

        protected abstract Value<Integer> defineInRange(String path, int defaultValue, int minValue, int maxValue);

        protected abstract Value<Double> defineInRange(String path, double defaultValue, double minValue, double maxValue);

        protected abstract <T> Value<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator);
    }
}
