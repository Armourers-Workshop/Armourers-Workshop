package moe.plushie.armourers_workshop.api.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

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

    default IDataPackObject get(String key) {
        if (type() == Type.DICTIONARY) {
            return of(jsonValue().getAsJsonObject().get(key));
        }
        return of(null);
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

    default boolean boolValue() {
        if (type() == Type.BOOLEAN) {
            return jsonValue().getAsBoolean();
        }
        return false;
    }

    default Number numberValue() {
        if (type() == Type.NUMBER) {
            return jsonValue().getAsNumber();
        }
        return 0;
    }

    default String stringValue() {
        if (type() == Type.STRING) {
            return jsonValue().getAsString();
        }
        return "";
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
