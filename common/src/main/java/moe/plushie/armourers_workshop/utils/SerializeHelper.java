package moe.plushie.armourers_workshop.utils;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public final class SerializeHelper {

    private SerializeHelper() {
    }

    public static String readFile(File file, Charset encoding) {
        InputStream inputStream = null;
        String text = null;
        try {
            inputStream = new FileInputStream(file);
            text = StreamUtils.toString(inputStream, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(inputStream);
        }
        return text;
    }

    public static String readFile(InputStream inputStream, Charset encoding) throws IOException {
        return StreamUtils.toString(inputStream, encoding);
    }

    public static JsonElement readJsonFile(File file) {
        return readJsonFile(file, Charsets.UTF_8);
    }

    public static JsonElement readJsonFile(File file, Charset encoding) {
        return stringToJson(readFile(file, encoding));
    }

    public static void writeFile(File file, Charset encoding, String text) {
        OutputStream outputStream = null;
        try {
            SkinFileUtils.forceMkdirParent(file);
            outputStream = new FileOutputStream(file, false);
            byte[] data = text.getBytes(encoding);
            outputStream.write(data);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtils.closeQuietly(outputStream);
        }
    }

    public static void writeJsonFile(File file, Charset encoding, JsonElement json) {
        writeFile(file, encoding, json.toString());
    }

    public static void writeJsonFile(JsonElement json, File file) {
        writeFile(file, Charsets.UTF_8, json.toString());
    }

    public static JsonElement stringToJson(String jsonString) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(jsonString);
        } catch (Exception e) {
            ModLog.error("Error parsing json.");
            ModLog.error(e.getMessage());
            return null;
        }
    }

    public static Tag jsonToTag(JsonElement element) {
        if (element instanceof JsonPrimitive primitiveValue) {
            if (primitiveValue.isString()) {
                return StringTag.valueOf(primitiveValue.getAsString());
            }
            if (primitiveValue.isBoolean()) {
                return ByteTag.valueOf(primitiveValue.getAsBoolean());
            }
            var longValue = primitiveValue.getAsLong();
            var doubleValue = primitiveValue.getAsDouble();
            if (longValue == doubleValue) {
                return LongTag.valueOf(longValue);
            }
            return DoubleTag.valueOf(doubleValue);
        }
        if (element instanceof JsonArray arrayValue) {
            var listTag = new ListTag();
            for (var value1 : arrayValue) {
                var tag1 = jsonToTag(value1);
                if (tag1 != null) {
                    listTag.add(tag1);
                }
            }
            return listTag;
        }
        if (element instanceof JsonObject objectValue) {
            var compoundTag = new CompoundTag();
            for (var key : objectValue.keySet()) {
                var value = jsonToTag(objectValue.get(key));
                if (value != null) {
                    compoundTag.put(key, value);
                }
            }
            return compoundTag;
        }
        return null;
    }

    public static JsonElement tagToJson(Tag tag) {
        if (tag instanceof NumericTag numericTag) {
            return new JsonPrimitive(numericTag.getAsNumber());
        }
        if (tag instanceof StringTag stringTag) {
            return new JsonPrimitive(stringTag.getAsString());
        }
        if (tag instanceof CollectionTag<?> listTag) {
            var array = new JsonArray();
            for (var tag1 : listTag) {
                var value1 = tagToJson(tag1);
                if (value1 != null) {
                    array.add(value1);
                }
            }
            return array;
        }
        if (tag instanceof CompoundTag compoundTag) {
            var object = new JsonObject();
            for (var key : compoundTag.getAllKeys()) {
                var value = tagToJson(compoundTag.get(key));
                if (value != null) {
                    object.add(key, value);
                }
            }
            return object;
        }
        return JsonNull.INSTANCE;
    }
}
