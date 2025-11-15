package moe.plushie.armourers_workshop.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.compat.core.data.serializer.AbstractJsonDataSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class JsonSerializer extends AbstractJsonDataSerializer {

    private static final Gson GSON = new Gson();

    public JsonSerializer() {
        super(new JsonObject(), SerializationContext.EMPTY);
    }

    public JsonSerializer(SerializationContext context) {
        super(new JsonObject(), context);
    }

    public JsonSerializer(JsonObject object) {
        super(object, SerializationContext.EMPTY);
    }

    public JsonSerializer(JsonObject object, SerializationContext context) {
        super(object, context);
    }

    public JsonSerializer(InputStream inputStream) throws IOException {
        super(parse(inputStream), SerializationContext.EMPTY);
    }

    public static IODataObject readFromString(String jsonString) throws IOException {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalArgumentException("json cannot be null or empty");
        }
        return readFromStream(new ByteArrayInputStream(jsonString.getBytes()));
    }

    public static IODataObject readFromStream(InputStream inputStream) throws IOException {
        return IODataObject.of(parse(inputStream));
    }

    @Nullable
    public static IODataObject readFromResource(IResource resource) {
        try {
            return readFromStream(resource.inputStream());
        } catch (IOException exception) {
            return null;
        }
    }

    public static JsonObject parse(InputStream inputStream) throws IOException {
        try (var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            var jsonReader = new JsonReader(reader);
            jsonReader.setLenient(false);
            return GSON.getAdapter(JsonObject.class).read(jsonReader);
        } catch (Exception exception) {
            throw new JsonParseException(exception);
        }
    }
}
