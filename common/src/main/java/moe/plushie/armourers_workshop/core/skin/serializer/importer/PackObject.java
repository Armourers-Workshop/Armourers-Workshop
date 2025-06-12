package moe.plushie.armourers_workshop.core.skin.serializer.importer;

import com.google.gson.JsonElement;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenSize2f;
import moe.plushie.armourers_workshop.core.math.OpenSize3f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.JsonSerializer;
import moe.plushie.armourers_workshop.core.utils.OpenExpression;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;

public class PackObject implements IODataObject {

    private final JsonElement element;

    public PackObject(IODataObject object) {
        this.element = object.jsonValue();
    }

    @Nullable
    public static PackObject from(PackResource resource) throws IOException {
        if (resource == null) {
            return null;
        }
        try (var inputStream = new BufferedInputStream(resource.inputStream())) {
            return new PackObject(JsonSerializer.readFromStream(inputStream));
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    public OpenExpression expression() {
        if (isNull()) {
            return null;
        }
        return new OpenExpression(stringValue());
    }

    public OpenVector2i vector2iValue() {
        var values = allValues();
        if (values.size() >= 2) {
            var iterator = values.iterator();
            return new OpenVector2i(iterator.next().intValue(), iterator.next().intValue());
        }
        return OpenVector2i.ZERO;
    }

    public OpenVector2f vector2fValue() {
        var values = allValues();
        if (values.size() >= 2) {
            var iterator = values.iterator();
            return new OpenVector2f(iterator.next().floatValue(), iterator.next().floatValue());
        }
        return OpenVector2f.ZERO;
    }

    public OpenSize2f size2fValue() {
        var values = allValues();
        if (values.size() >= 2) {
            var iterator = values.iterator();
            return new OpenSize2f(iterator.next().floatValue(), iterator.next().floatValue());
        }
        return OpenSize2f.ZERO;
    }

    public OpenRectangle2f rectangle2fValue() {
        var values = allValues();
        if (values.size() >= 4) {
            var iterator = values.iterator();
            var x1 = iterator.next().floatValue();
            var y1 = iterator.next().floatValue();
            var x2 = iterator.next().floatValue();
            var y2 = iterator.next().floatValue();
            return new OpenRectangle2f(x1, y1, x2 - x1, y2 - y1);
        }
        return OpenRectangle2f.ZERO;
    }

    public OpenSize3f size3fValue() {
        var values = allValues();
        if (values.size() >= 3) {
            var iterator = values.iterator();
            return new OpenSize3f(iterator.next().floatValue(), iterator.next().floatValue(), iterator.next().floatValue());
        }
        return OpenSize3f.ZERO;
    }

    public OpenVector3f vector3fValue() {
        var values = allValues();
        if (values.size() >= 3) {
            var iterator = values.iterator();
            return new OpenVector3f(iterator.next().floatValue(), iterator.next().floatValue(), iterator.next().floatValue());
        }
        return OpenVector3f.ZERO;
    }

    public void at(String keyPath, IOConsumer<PackObject> consumer) throws IOException {
        var object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        consumer.accept(new PackObject(by(keyPath)));
    }

    public void each(String keyPath, IOConsumer<PackObject> consumer) throws IOException {
        var object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        for (var value : object.allValues()) {
            consumer.accept(new PackObject(value));
        }
    }

    public void each(String keyPath, IOConsumer2<String, PackObject> consumer) throws IOException {
        var object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        for (var pair : object.entrySet()) {
            consumer.accept(pair.getKey(), new PackObject(pair.getValue()));
        }
    }

    @Override
    public PackObject at(int index) {
        return new PackObject(IODataObject.super.at(index));
    }

    @Override
    public PackObject get(String key) {
        return new PackObject(IODataObject.super.get(key));
    }

    public PackObject by(String keyPath) {
        // when this is a full key, ignore.
        if (has(keyPath)) {
            return get(keyPath);
        }
        var keys = keyPath.split("\\.");
        PackObject object = this;
        for (String key : keys) {
            object = object.get(key);
        }
        return object;
    }

    @Override
    public JsonElement jsonValue() {
        return element;
    }
}
