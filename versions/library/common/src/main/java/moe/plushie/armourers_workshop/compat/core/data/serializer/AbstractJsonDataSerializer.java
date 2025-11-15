package moe.plushie.armourers_workshop.compat.core.data.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;

public class AbstractJsonDataSerializer extends AbstractDataSerializerImpl<JsonElement> {

    protected final JsonObject object;

    public AbstractJsonDataSerializer(JsonObject object, SerializationContext context) {
        super(JsonOps.INSTANCE, context);
        this.object = object;
    }

    @Override
    protected JsonElement readRootValue() {
        return object;
    }

    @Override
    protected void writeRootValue(JsonElement value) {
        for (var entry : value.getAsJsonObject().entrySet()) {
            object.add(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected boolean hasValue(String key) {
        return object.has(key);
    }

    @Override
    protected JsonElement readValue(String key) {
        return object.get(key);
    }

    @Override
    protected void writeValue(String key, JsonElement value) {
        object.add(key, value);
    }

    public JsonObject object() {
        return object;
    }
}
