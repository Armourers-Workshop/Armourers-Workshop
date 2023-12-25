package moe.plushie.armourers_workshop.init;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

public class ModOptions {

    private static final ModOptions INSTANCE = new ModOptions();

    private final File contentPath;
    private final LinkedHashMap<String, IDataPackObject> values = new LinkedHashMap<>();

    private ModOptions() {
        contentPath = new File(EnvironmentManager.getRootDirectory(), "options.json");
        try {
            if (contentPath.exists()) {
                load();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static ModOptions getInstance() {
        return INSTANCE;
    }

    public void putString(String key, String value) {
        values.put(key, IDataPackObject.of(new JsonPrimitive(value)));
        setChanged();
    }

    public String getString(String key) {
        IDataPackObject obj = values.get(key);
        if (obj != null) {
            return obj.stringValue();
        }
        return null;
    }

    public void putInt(String key, int value) {
        values.put(key, IDataPackObject.of(new JsonPrimitive(value)));
        setChanged();
    }

    public int getInt(String key) {
        IDataPackObject obj = values.get(key);
        if (obj != null) {
            return obj.intValue();
        }
        return 0;
    }

    public void putBoolean(String key, boolean value) {
        values.put(key, IDataPackObject.of(new JsonPrimitive(value)));
        setChanged();
    }

    public boolean getBoolean(String key) {
        IDataPackObject obj = values.get(key);
        if (obj != null) {
            return obj.boolValue();
        }
        return false;
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
        Util.backgroundExecutor().execute(this::save);
    }
}

