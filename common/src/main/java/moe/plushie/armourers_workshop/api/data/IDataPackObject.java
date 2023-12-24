package moe.plushie.armourers_workshop.api.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IDataPackObject {

    static IDataPackObject of(JsonElement element) {
        return () -> element;
    }

    default Type type() {
        JsonElement object = jsonValue();
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
            JsonPrimitive primitive = object.getAsJsonPrimitive();
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

    default void add(IDataPackObject value) {
        if (type() == Type.ARRAY) {
            jsonValue().getAsJsonArray().add(value.jsonValue());
        }
    }

    default IDataPackObject at(int index) {
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

    default void set(String key, IDataPackObject value) {
        if (type() == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().add(key, value.jsonValue());
        }
    }

    default IDataPackObject get(String key) {
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
        ArrayList<String> keys = new ArrayList<>();
        if (type() == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().entrySet().forEach(it -> keys.add(it.getKey()));
        }
        return keys;
    }

    default Collection<IDataPackObject> allValues() {
        Type type = type();
        ArrayList<IDataPackObject> values = new ArrayList<>();
        if (type == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().entrySet().forEach(it -> values.add(of(it.getValue())));
        }
        if (type == Type.ARRAY) {
            jsonValue().getAsJsonArray().forEach(val -> values.add(of(val)));
        }
        return values;
    }

    default Collection<Pair<String, IDataPackObject>> entrySet() {
        ArrayList<Pair<String, IDataPackObject>> keys = new ArrayList<>();
        if (type() == Type.DICTIONARY) {
            jsonValue().getAsJsonObject().entrySet().forEach(it -> keys.add(Pair.of(it.getKey(), of(it.getValue()))));
        }
        return keys;
    }

    default <T> ArrayList<T> collect(Function<IDataPackObject, ? extends T> mapper) {
        Collection<IDataPackObject> values = allValues();
        ArrayList<T> results = new ArrayList<>(values.size());
        for (IDataPackObject value : values) {
            results.add(mapper.apply(value));
        }
        return results;
    }

    default boolean boolValue() {
        switch (type()) {
            case STRING:
            case NUMBER: {
                if (numberValue().intValue() != 0) {
                    return true;
                }
                return false;
            }
            case BOOLEAN: {
                return jsonValue().getAsBoolean();
            }
            default: {
                return false;
            }
        }
    }

    default Number numberValue() {
        switch (type()) {
            case STRING:
            case NUMBER: {
                return jsonValue().getAsNumber();
            }
            case BOOLEAN: {
                if (jsonValue().getAsBoolean()) {
                    return 1;
                }
                return 0;
            }
            default: {
                return 0;
            }
        }
    }

    default int intValue() {
        return numberValue().intValue();
    }

    default float floatValue() {
        return numberValue().floatValue();
    }

    default String stringValue() {
        switch (type()) {
            case STRING:
            case NUMBER:
            case BOOLEAN: {
                return jsonValue().getAsString();
            }
            default: {
                return "";
            }
        }
    }

    default boolean isNull() {
        return type() == Type.NULL;
    }

    default void ifPresent(Consumer<IDataPackObject> consumer) {
        if (type() != Type.NULL) {
            consumer.accept(this);
        }
    }

    JsonElement jsonValue();

    enum Type {
        NULL, BOOLEAN, NUMBER, STRING, ARRAY, DICTIONARY
    }
}
