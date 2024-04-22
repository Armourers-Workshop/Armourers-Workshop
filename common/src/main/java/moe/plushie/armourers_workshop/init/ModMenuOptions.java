package moe.plushie.armourers_workshop.init;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

public class ModMenuOptions {

    private static final ModMenuOptions INSTANCE = new ModMenuOptions();

    private final File contentPath;
    private final LinkedHashMap<String, IDataPackObject> values = new LinkedHashMap<>();

    private ModMenuOptions() {
        contentPath = new File(EnvironmentManager.getRootDirectory(), "options.json");
        try {
            if (contentPath.exists()) {
                load();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static ModMenuOptions getInstance() {
        return INSTANCE;
    }

    public void putString(String key, String value) {
        values.put(key, IDataPackObject.of(new JsonPrimitive(value)));
        setChanged();
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        IDataPackObject obj = values.get(key);
        if (obj != null) {
            return obj.stringValue();
        }
        return defaultValue;
    }

    public void putInt(String key, int value) {
        values.put(key, IDataPackObject.of(new JsonPrimitive(value)));
        setChanged();
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        IDataPackObject obj = values.get(key);
        if (obj != null) {
            return obj.intValue();
        }
        return defaultValue;
    }

    public void putBoolean(String key, boolean value) {
        values.put(key, IDataPackObject.of(new JsonPrimitive(value)));
        setChanged();
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        IDataPackObject obj = values.get(key);
        if (obj != null) {
            return obj.boolValue();
        }
        return defaultValue;
    }

    private void load() throws IOException {
        IDataPackObject object = StreamUtils.fromPackObject(new FileInputStream(contentPath));
        if (object == null) {
            return;
        }
        object.entrySet().forEach(it -> values.put(it.getKey(), it.getValue()));
    }

    private void save() {
        try {
            IDataPackObject packObject = IDataPackObject.of(new JsonObject());
            values.forEach(packObject::set);
            StreamUtils.writePackObject(packObject, new FileOutputStream(contentPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setChanged() {
        EnvironmentExecutor.runOnBackground(() -> this::save);
    }
}

