package moe.plushie.armourers_workshop.core.skin.serializer.io;

import com.google.gson.JsonElement;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IODataObject {

    static IODataObject of(JsonElement element) {
        return () -> element;
    }

    default Type type() {
        var object = jsonValue();
        if (object == null || object.isJsonNull()) {
            return Type.NULL;
        }
        if (object.isJsonObject()) {
            return Type.DICTIONARY;
        }
        if (object.isJsonArray()) {
            return Type.ARRAY;
        }
        if (object.isJsonPrimitive()) {
            var primitive = object.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return Type.NUMBER;
            }
            if (primitive.isBoolean()) {
                return Type.BOOLEAN;
            }
            if (primitive.isString()) {
                return Type.STRING;
            }
        }
        return Type.NULL;
    }

    default void add(IODataObject value) {
        if (type() == Type.ARRAY) {
            jsonValue().getAsJsonArray().add(value.jsonValue());
        }
    }

    default IODataObject at(int index) {
        if (type() == Type.ARRAY) {
            return of(jsonValue().getAsJsonArray().get(index));
        }
        return of(null);
    }

    default int size() {
        if (type() == Type.ARRAY) {
            return jsonValue().getAsJsonArray().size();
        }
        if (type() == Type.DICTIONARY) {
            return jsonValue().getAsJsonObject().size();
        }
        return 0;
    }

    default void set(String key, IODataObject value) {
        if (type() == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().add(key, value.jsonValue());
        }
    }

    default IODataObject get(String key) {
        if (type() == Type.DICTIONARY) {
            return of(jsonValue().getAsJsonObject().get(key));
        }
        return of(null);
    }

    default boolean has(String key) {
        if (type() == Type.DICTIONARY) {
            return jsonValue().getAsJsonObject().has(key);
        }
        return false;
    }

    default Collection<String> allKeys() {
        var keys = new ArrayList<String>();
        if (type() == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().entrySet().forEach(it -> keys.add(it.getKey()));
        }
        return keys;
    }

    default Collection<IODataObject> allValues() {
        var type = type();
        var values = new ArrayList<IODataObject>();
        if (type == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().entrySet().forEach(it -> values.add(of(it.getValue())));
        }
        if (type == Type.ARRAY) {
            jsonValue().getAsJsonArray().forEach(val -> values.add(of(val)));
        }
        return values;
    }

    default Collection<Pair<String, IODataObject>> entrySet() {
        var keys = new ArrayList<Pair<String, IODataObject>>();
        if (type() == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().entrySet().forEach(it -> keys.add(Pair.of(it.getKey(), of(it.getValue()))));
        }
        return keys;
    }

    default <T> ArrayList<T> collect(Function<IODataObject, ? extends T> mapper) {
        var values = allValues();
        var results = new ArrayList<T>(values.size());
        for (var value : values) {
            results.add(mapper.apply(value));
        }
        return results;
    }

    default boolean boolValue() {
        return switch (type()) {
            case STRING, NUMBER -> numberValue().intValue() != 0;
            case BOOLEAN -> jsonValue().getAsBoolean();
            default -> false;
        };
    }

    default Number numberValue() {
        return switch (type()) {
            case STRING, NUMBER -> jsonValue().getAsNumber();
            case BOOLEAN -> jsonValue().getAsBoolean() ? 1 : 0;
            default -> 0;
        };
    }

    default int intValue() {
        return numberValue().intValue();
    }

    default float floatValue() {
        return numberValue().floatValue();
    }

    default String stringValue() {
        return switch (type()) {
            case STRING, NUMBER, BOOLEAN -> jsonValue().getAsString();
            default -> "";
        };
    }

    default boolean isNull() {
        return type() == Type.NULL;
    }

    default void ifPresent(Consumer<IODataObject> consumer) {
        if (type() != Type.NULL) {
            consumer.accept(this);
        }
    }

    JsonElement jsonValue();

    enum Type {
        NULL, BOOLEAN, NUMBER, STRING, ARRAY, DICTIONARY
    }
}
