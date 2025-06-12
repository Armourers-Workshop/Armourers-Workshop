package moe.plushie.armourers_workshop.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonSerializer {

    private static final Gson GSON = new Gson();

    public static IODataObject readFromString(String jsonString) throws IOException {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalArgumentException("json cannot be null or empty");
        }
        return readFromStream(new ByteArrayInputStream(jsonString.getBytes()));
    }

    public static IODataObject readFromStream(InputStream inputStream) throws IOException {
        try (var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            var jsonReader = new JsonReader(reader);
            jsonReader.setLenient(false);
            var jsonObject = GSON.getAdapter(JsonObject.class).read(jsonReader);
            return IODataObject.of(jsonObject);
        } catch (Exception exception) {
            throw new JsonParseException(exception);
        }
    }

    @Nullable
    public static IODataObject readFromResource(IResource resource) {
        try {
            return readFromStream(resource.inputStream());
        } catch (IOException exception) {
            return null;
        }
    }
}
