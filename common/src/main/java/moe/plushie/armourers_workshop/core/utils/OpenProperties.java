package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class OpenProperties {

    protected final LinkedHashMap<String, Object> properties;

    public OpenProperties() {
        this(new LinkedHashMap<>());
    }

    public OpenProperties(LinkedHashMap<String, Object> properties) {
        this.properties = properties;
    }

    public void readFromStream(IInputStream stream) throws IOException {
        Serializer.DEFAULT.readFromStream(this, stream);
    }

    public void writeToStream(IOutputStream stream) throws IOException {
        Serializer.DEFAULT.writeToStream(this, stream);
    }

    public void readFromNBT(CompoundTag nbt) {
        Serializer.DEFAULT.readFromNBT(this, nbt);
    }

    public void writeToNBT(CompoundTag nbt) {
        Serializer.DEFAULT.writeToNBT(this, nbt);
    }

    public void put(String key, Object value) {
        if (value == null) {
            properties.remove(key);
        } else {
            properties.put(key, value);
        }
    }

    public void putAll(OpenProperties newValue) {
        properties.putAll(newValue.properties);
    }

    public void remove(String key) {
        properties.remove(key);
    }

    public void clear() {
        properties.clear();
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public boolean containsValue(String key) {
        return properties.containsValue(key);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public abstract OpenProperties copy();

    public abstract OpenProperties empty();

    public Set<Map.Entry<String, Object>> entrySet() {
        return properties.entrySet();
    }

    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenProperties that)) return false;
        return properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @Override
    public String toString() {
        return properties.toString();
    }


    private enum DataTypes {
        STRING, INT, DOUBLE, BOOLEAN, LIST, COMPOUND, FLOAT, LONG;

        @Nullable
        public static DataTypes byId(int id) {
            if (id >= 0 & id < DataTypes.values().length) {
                return DataTypes.values()[id];
            }
            return null;
        }
    }

    private static class Serializer {

        private static final Serializer DEFAULT = new Serializer();
        private static final Serializer ONLY_VALUE = new Serializer() {
            @Override
            protected void writeTypeToStream(DataTypes dataTypes, IOutputStream stream) {
                // ignore
            }
        };

        protected void writeToStream(OpenProperties instance, IOutputStream stream) throws IOException {
            stream.writeInt(instance.properties.size());
            for (var entry : instance.properties.entrySet()) {
                stream.writeString(entry.getKey());
                writeValueToStream(entry.getValue(), stream);
            }
        }

        protected void writeTypeToStream(DataTypes dataTypes, IOutputStream stream) throws IOException {
            stream.writeByte(dataTypes.ordinal());
        }

        protected void writeValueToStream(Object value, IOutputStream stream) throws IOException {
            if (value instanceof String stringValue) {
                writeTypeToStream(DataTypes.STRING, stream);
                stream.writeString(stringValue);
            } else if (value instanceof Long longValue) {
                writeTypeToStream(DataTypes.LONG, stream);
                stream.writeLong(longValue);
            } else if (value instanceof Integer intValue) {
                writeTypeToStream(DataTypes.INT, stream);
                stream.writeInt(intValue);
            } else if (value instanceof Float floatValue) {
                writeTypeToStream(DataTypes.FLOAT, stream);
                stream.writeFloat(floatValue);
            } else if (value instanceof Double doubleValue) {
                writeTypeToStream(DataTypes.DOUBLE, stream);
                stream.writeDouble(doubleValue);
            } else if (value instanceof Boolean boolValue) {
                writeTypeToStream(DataTypes.BOOLEAN, stream);
                stream.writeBoolean(boolValue);
            } else if (value instanceof List<?> listValue) {
                writeTypeToStream(DataTypes.LIST, stream);
                stream.writeInt(listValue.size());
                var serializer = DEFAULT; // only write element type
                for (var element : listValue) {
                    serializer.writeValueToStream(element, stream);
                    serializer = ONLY_VALUE;
                }
            } else if (value instanceof OpenProperties compoundValue) {
                writeTypeToStream(DataTypes.COMPOUND, stream);
                compoundValue.writeToStream(stream);
            }
        }

        protected void readFromStream(OpenProperties instance, IInputStream stream) throws IOException {
            int size = stream.readInt();
            for (int i = 0; i < size; ++i) {
                var key = stream.readString();
                var type = readTypeFromStream(stream);
                instance.properties.put(key, readValueFromStream(instance, type, stream));
            }
        }


        protected DataTypes readTypeFromStream(IInputStream stream) throws IOException {
            var byteType = stream.readByte();
            var type = DataTypes.byId(byteType);
            if (type == null) {
                throw new IOException("Error loading properties " + byteType);
            }
            return type;
        }

        protected Object readValueFromStream(OpenProperties instance, DataTypes type, IInputStream stream) throws IOException {
            return switch (type) {
                case STRING -> stream.readString();
                case INT -> stream.readInt();
                case LONG -> stream.readLong();
                case FLOAT -> stream.readFloat();
                case DOUBLE -> stream.readDouble();
                case BOOLEAN -> stream.readBoolean();
                case LIST -> {
                    int size = stream.readInt();
                    if (size == 0) {
                        yield Collections.emptyList();
                    }
                    var elementType = readTypeFromStream(stream);
                    var elements = new ArrayList<>();
                    for (int i = 0; i < size; ++i) {
                        elements.add(readValueFromStream(instance, elementType, stream));
                    }
                    yield elements;
                }
                case COMPOUND -> {
                    var properties1 = instance.empty();
                    properties1.readFromStream(stream);
                    yield properties1;
                }
            };
        }

        protected void writeToNBT(OpenProperties instance, CompoundTag nbt) {
            for (var entry : instance.properties.entrySet()) {
                nbt.put(entry.getKey(), writeValueToNbt(entry.getValue()));
            }
        }

        protected Tag writeValueToNbt(Object value) {
            if (value instanceof String stringValue) {
                return StringTag.valueOf(stringValue);
            }
            if (value instanceof Long longValue) {
                return LongTag.valueOf(longValue);
            }
            if (value instanceof Integer intValue) {
                return IntTag.valueOf(intValue);
            }
            if (value instanceof Float floatValue) {
                return FloatTag.valueOf(floatValue);
            }
            if (value instanceof Double doubleValue) {
                return DoubleTag.valueOf(doubleValue);
            }
            if (value instanceof Boolean boolValue) {
                return ByteTag.valueOf(boolValue);
            }
            if (value instanceof List<?> listValue) {
                var listTag = new ListTag();
                for (var element : listValue) {
                    listTag.add(writeValueToNbt(element));
                }
                return listTag;
            }
            if (value instanceof OpenProperties compoundValue) {
                var compoundTag = new CompoundTag();
                compoundValue.writeToNBT(compoundTag);
                return compoundTag;
            }
            return null;
        }

        protected void readFromNBT(OpenProperties instance, CompoundTag nbt) {
            for (var key : nbt.keySet()) {
                instance.properties.put(key, readValueFromNBT(instance, nbt.get(key)));
            }
        }

        protected Object readValueFromNBT(OpenProperties instance, Object value) {
            if (value instanceof StringTag stringTag) {
                return stringTag.value();
            }
            if (value instanceof LongTag longTag) {
                return longTag.longValue();
            }
            if (value instanceof IntTag intTag) {
                return intTag.intValue();
            }
            if (value instanceof FloatTag floatTag) {
                return floatTag.floatValue();
            }
            if (value instanceof DoubleTag doubleTag) {
                return doubleTag.doubleValue();
            }
            if (value instanceof ByteTag byteTag) {
                return byteTag.byteValue() != 0;
            }
            if (value instanceof ListTag listTag) {
                var elements = new ArrayList<>();
                for (var element : listTag) {
                    elements.add(readValueFromNBT(instance, element));
                }
                return elements;
            }
            if (value instanceof CompoundTag compoundTag) {
                var compoundValue = instance.empty();
                compoundValue.readFromNBT(compoundTag);
                return compoundValue;
            }
            return null;
        }
    }
}
