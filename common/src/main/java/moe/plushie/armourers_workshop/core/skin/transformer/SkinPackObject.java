package moe.plushie.armourers_workshop.core.skin.transformer;

import com.google.gson.JsonElement;
import moe.plushie.armourers_workshop.api.common.IResource;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Size2f;
import moe.plushie.armourers_workshop.utils.math.Size3f;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

public class SkinPackObject implements IDataPackObject {

    private final JsonElement element;

    public SkinPackObject(IDataPackObject object) {
        this.element = object.jsonValue();
    }

    @Nullable
    public static SkinPackObject from(IResource resource) throws IOException {
        if (resource == null) {
            return null;
        }
        InputStream inputStream = new BufferedInputStream(resource.getInputStream());
        try {
            IDataPackObject object = StreamUtils.fromPackObject(inputStream);
            if (object == null) {
                return null;
            }
            return new SkinPackObject(object);
        } catch (Exception exception) {
            throw new IOException(exception);
        }
    }

    public TexturePos texturePosValue() {
        Collection<IDataPackObject> values = allValues();
        if (values.size() >= 2) {
            Iterator<IDataPackObject> iterator = values.iterator();
            return new TexturePos(iterator.next().intValue(), iterator.next().intValue());
        }
        return TexturePos.ZERO;
    }

    public Vector2f vector2fValue() {
        Collection<IDataPackObject> values = allValues();
        if (values.size() >= 2) {
            Iterator<IDataPackObject> iterator = values.iterator();
            return new Vector2f(iterator.next().floatValue(), iterator.next().floatValue());
        }
        return Vector2f.ZERO;
    }

    public Size2f size2fValue() {
        Collection<IDataPackObject> values = allValues();
        if (values.size() >= 2) {
            Iterator<IDataPackObject> iterator = values.iterator();
            return new Size2f(iterator.next().floatValue(), iterator.next().floatValue());
        }
        return Size2f.ZERO;
    }

    public Rectangle2f rectangle2fValue() {
        Collection<IDataPackObject> values = allValues();
        if (values.size() >= 4) {
            Iterator<IDataPackObject> iterator = values.iterator();
            float x1 = iterator.next().floatValue();
            float y1 = iterator.next().floatValue();
            float x2 = iterator.next().floatValue();
            float y2 = iterator.next().floatValue();
            return new Rectangle2f(x1, y1, x2 - x1, y2 - y1);
        }
        return Rectangle2f.ZERO;
    }

    public Size3f size3fValue() {
        Collection<IDataPackObject> values = allValues();
        if (values.size() >= 3) {
            Iterator<IDataPackObject> iterator = values.iterator();
            return new Size3f(iterator.next().floatValue(), iterator.next().floatValue(), iterator.next().floatValue());
        }
        return Size3f.ZERO;
    }

    public Vector3f vector3fValue() {
        Collection<IDataPackObject> values = allValues();
        if (values.size() >= 3) {
            Iterator<IDataPackObject> iterator = values.iterator();
            return new Vector3f(iterator.next().floatValue(), iterator.next().floatValue(), iterator.next().floatValue());
        }
        return Vector3f.ZERO;
    }

    public void at(String keyPath, IOConsumer<SkinPackObject> consumer) throws IOException {
        IDataPackObject object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        consumer.accept(new SkinPackObject(by(keyPath)));
    }

    public void each(String keyPath, IOConsumer<SkinPackObject> consumer) throws IOException {
        IDataPackObject object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        for (IDataPackObject value : object.allValues()) {
            consumer.accept(new SkinPackObject(value));
        }
    }

    public void each(String keyPath, IOConsumer2<String, SkinPackObject> consumer) throws IOException {
        IDataPackObject object = by(keyPath);
        if (object.isNull()) {
            return;
        }
        for (Pair<String, IDataPackObject> pair : object.entrySet()) {
            consumer.accept(pair.getKey(), new SkinPackObject(pair.getValue()));
        }
    }

    public IDataPackObject by(String keyPath) {
        // when this is a full key, ignore.
        if (has(keyPath)) {
            return get(keyPath);
        }
        String[] keys = keyPath.split("\\.");
        IDataPackObject object = this;
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
