package moe.plushie.armourers_workshop.core.skin.data.property;

import moe.plushie.armourers_workshop.api.skin.ISkinProperties;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.nbt.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SkinProperties implements ISkinProperties {

//    // Properties for all skins.
//    public static final SkinProperty<String> ALL_CUSTOM_NAME = new SkinProperty<>("customName", "");
//    public static final SkinProperty<String> ALL_FLAVOUR_TEXT = new SkinProperty<>("flavour", "");
//    public static final SkinProperty<String> ALL_AUTHOR_NAME = new SkinProperty<>("authorName", "");
//    public static final SkinProperty<String> ALL_AUTHOR_UUID = new SkinProperty<>("authorUUID", "");
//
//    // Properties.
//    @Deprecated
//    public static final SkinProperty<Boolean> MODEL_OVERRIDE = new SkinProperty<>("armourOverride", false);
//    public static final SkinProperty<Boolean> MODEL_OVERRIDE_HEAD = new SkinProperty<>("overrideModelHead", false);
//    public static final SkinProperty<Boolean> MODEL_OVERRIDE_CHEST = new SkinProperty<>("overrideModelChest", false);
//    public static final SkinProperty<Boolean> MODEL_OVERRIDE_ARM_LEFT = new SkinProperty<>("overrideModelArmLeft", false);
//    public static final SkinProperty<Boolean> MODEL_OVERRIDE_ARM_RIGHT = new SkinProperty<>("overrideModelArmRight", false);
//    public static final SkinProperty<Boolean> MODEL_OVERRIDE_LEG_LEFT = new SkinProperty<>("overrideModelLegLeft", false);
//    public static final SkinProperty<Boolean> MODEL_OVERRIDE_LEG_RIGHT = new SkinProperty<>("overrideModelLegRight", false);
//
//    @Deprecated
//    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY = new SkinProperty<>("armourHideOverlay", false);
//    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_HEAD = new SkinProperty<>("hideOverlayHead", false);
//    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_CHEST = new SkinProperty<>("hideOverlayChest", false);
//    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_ARM_LEFT = new SkinProperty<>("hideOverlayArmLeft", false);
//    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_ARM_RIGHT = new SkinProperty<>("hideOverlayArmRight", false);
//    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_LEG_LEFT = new SkinProperty<>("hideOverlayLegLeft", false);
//    public static final SkinProperty<Boolean> MODEL_HIDE_OVERLAY_LEG_RIGHT = new SkinProperty<>("hideOverlayLegRight", false);
//
//    public static final SkinProperty<Boolean> MODEL_LEGS_LIMIT_LIMBS = new SkinProperty<>("limitLimbs", false);
//
//    public static final SkinProperty<String> OUTFIT_PART_INDEXS = new SkinProperty<>("partIndexs", "");
//
//    public static final SkinProperty<Boolean> BLOCK_GLOWING = new SkinProperty<>("blockGlowing", false);
//    public static final SkinProperty<Boolean> BLOCK_LADDER = new SkinProperty<>("blockLadder", false);
//    public static final SkinProperty<Boolean> BLOCK_NO_COLLISION = new SkinProperty<>("blockNoCollision", false);
//    public static final SkinProperty<Boolean> BLOCK_SEAT = new SkinProperty<>("blockSeat", false);
//    public static final SkinProperty<Boolean> BLOCK_MULTIBLOCK = new SkinProperty<>("blockMultiblock", false);
//    public static final SkinProperty<Boolean> BLOCK_BED = new SkinProperty<>("blockBed", false);
//    public static final SkinProperty<Boolean> BLOCK_INVENTORY = new SkinProperty<>("blockInventory", false);
//    public static final SkinProperty<Boolean> BLOCK_ENDER_INVENTORY = new SkinProperty<>("blockEnderInventory", false);
//    public static final SkinProperty<Integer> BLOCK_INVENTORY_WIDTH = new SkinProperty<>("blockInventoryWidth", 9);
//    public static final SkinProperty<Integer> BLOCK_INVENTORY_HEIGHT = new SkinProperty<>("blockInventoryHeight", 4);
//
//    public static final SkinProperty<Double> WINGS_MAX_ANGLE = new SkinProperty<>("wingsMaxAngle", 75D);
//    public static final SkinProperty<Double> WINGS_MIN_ANGLE = new SkinProperty<>("wingsMinAngle", 0D);
//    public static final SkinProperty<Double> WINGS_IDLE_SPEED = new SkinProperty<>("wingsIdleSpeed", 6000D);
//    public static final SkinProperty<Double> WINGS_FLYING_SPEED = new SkinProperty<>("wingsFlyingSpeed", 350D);
//    public static final SkinProperty<String> WINGS_MOVMENT_TYPE = new SkinProperty<>("wingsMovmentType", MovementType.EASE.toString());
//
//
//    // Properties for skin parts.
//    public static final SkinProperty<String> PART_TARGET = new SkinProperty<>("target", "");
//    public static final SkinProperty<Boolean> PART_OVERRIDE = new SkinProperty<>("partOverride", false);
//    public static final SkinProperty<Boolean> PART_HIDE_OVERLAY = new SkinProperty<>("partHideOverlay", false);
//    public static final SkinProperty<Boolean> PART_LEGS_LIMIT_LIMB = new SkinProperty<>("limitLimb", false);
//
//    // Legacy


    protected LinkedHashMap<String, Object> properties;


    public SkinProperties() {
        properties = new LinkedHashMap<>();
    }

    public SkinProperties(SkinProperties skinProps) {
        properties = new LinkedHashMap<>(skinProps.properties);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(SkinProperty<T> property) {
        return (T) properties.getOrDefault(property.getKey(), property.getDefaultValue());
    }

    public <T> void put(SkinProperty<T> property, T value) {
        properties.put(property.getKey(), value);
    }

    public <T> void remove(SkinProperty<T> property) {
        properties.remove(property.getKey());
    }


    public <T> boolean containsKey(SkinProperty<T> property) {
        return properties.containsKey(property.getKey());
    }

    public <T> boolean containsValue(SkinProperty<T> property) {
        return properties.containsValue(property.getKey());
    }


    public void put(String key, Object value) {
        properties.put(key, value);
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return properties.entrySet();
    }

    public ArrayList<String> getPropertiesList() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < properties.size(); i++) {
            String key = (String) properties.keySet().toArray()[i];
            list.add(key + ":" + properties.get(key));
        }
        return list;
    }

    public void copyFrom(SkinProperties properties) {
        this.properties = new LinkedHashMap<>(properties.properties);
    }

    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(properties.size());
        for (int i = 0; i < properties.size(); i++) {
            String key = (String) properties.keySet().toArray()[i];
            Object value = properties.get(key);
            StreamUtils.writeStringUtf8(stream, key);
            if (value instanceof String) {
                stream.writeByte(DataTypes.STRING.ordinal());
                StreamUtils.writeStringUtf8(stream, (String) value);
            }
            if (value instanceof Integer) {
                stream.writeByte(DataTypes.INT.ordinal());
                stream.writeInt((Integer) value);
            }
            if (value instanceof Double) {
                stream.writeByte(DataTypes.DOUBLE.ordinal());
                stream.writeDouble((Double) value);
            }
            if (value instanceof Boolean) {
                stream.writeByte(DataTypes.BOOLEAN.ordinal());
                stream.writeBoolean((Boolean) value);
            }
        }
    }

    public void readFromStream(DataInputStream stream, int fileVersion) throws IOException {
        int count = stream.readInt();
        for (int i = 0; i < count; i++) {

            String key = null;
            if (fileVersion > 12) {
                key = StreamUtils.readStringUtf8(stream);
            } else {
                key = stream.readUTF();
            }
            int byteType = stream.readByte();
            DataTypes type = DataTypes.STRING;
            if (byteType >= 0 & byteType < DataTypes.values().length) {
                type = DataTypes.values()[byteType];
            } else {
                throw new IOException("Error loading skin properties " + byteType);
            }

            Object value = null;
            switch (type) {
                case STRING:
                    if (fileVersion > 12) {
                        value = StreamUtils.readStringUtf8(stream);
                    } else {
                        value = stream.readUTF();
                    }
                    break;
                case INT:
                    value = stream.readInt();
                    break;
                case DOUBLE:
                    value = stream.readDouble();
                    break;
                case BOOLEAN:
                    value = stream.readBoolean();
                    break;
            }
            properties.put(key, value);
        }
    }

//    @Override
//    public void removeProperty(String key) {
//        properties.remove(key);
//    }
//
//    @Override
//    public void setProperty(String key, Object value) {
//        properties.put(key, value);
//    }
//
//    @Override
//    public String getPropertyString(String key, String defaultValue) {
//        Object value = properties.get(key);
//        if (value instanceof String) {
//            return (String) value;
//        }
//        return defaultValue;
//    }
//
//    @Override
//    public int getPropertyInt(String key, int defaultValue) {
//        Object value = properties.get(key);
//        if (value instanceof Integer) {
//            return (Integer) value;
//        }
//        return defaultValue;
//    }
//
//    @Override
//    public double getPropertyDouble(String key, double defaultValue) {
//        Object value = properties.get(key);
//        if (value instanceof Double) {
//            return (Double) value;
//        }
//        return defaultValue;
//    }
//
//    @Override
//    public Boolean getPropertyBoolean(String key, Boolean defaultValue) {
//        Object value = properties.get(key);
//        if (value instanceof Boolean) {
//            return (Boolean) value;
//        }
//        return defaultValue;
//    }
//
//    @Override
//    public Object getProperty(String key, Object defaultValue) {
//        Object value = properties.get(key);
//        if (value != null) {
//            return value;
//        }
//        return defaultValue;
//    }
//
//    @Override
//    public boolean haveProperty(String key) {
//        return properties.containsKey(key);
//    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkinProperties other = (SkinProperties) obj;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SkinProperties [properties=" + properties + "]";
    }

    public void readFromNBT(CompoundNBT nbt) {
        for (String key : nbt.getAllKeys()) {
            INBT value = nbt.get(key);
            if (value instanceof StringNBT) {
                properties.put(key, ((StringNBT)value).getAsString());
            } else if (value instanceof IntNBT) {
                properties.put(key, ((IntNBT) value).getAsInt());
            } else if (value instanceof FloatNBT) {
                properties.put(key, ((FloatNBT) value).getAsFloat());
            } else if (value instanceof DoubleNBT) {
                properties.put(key, ((DoubleNBT) value).getAsDouble());
            } else if (value instanceof ByteNBT) {
                properties.put(key, ((ByteNBT) value).getAsByte() != 0);
            }
        }
    }

    public void writeToNBT(CompoundNBT nbt) {
        properties.forEach((key, value) -> {
            if (value instanceof String) {
                nbt.putString(key, (String) value);
            } else if (value instanceof Integer) {
                nbt.putInt(key, (int) value);
            } else if (value instanceof Float) {
                nbt.putDouble(key, (float) value);
            } else if (value instanceof Double) {
                nbt.putDouble(key, (double) value);
            } else if (value instanceof Boolean) {
                nbt.putBoolean(key, (boolean) value);
            }
        });
    }

    enum DataTypes {
        STRING, INT, DOUBLE, BOOLEAN
    }

    public static class Stub extends SkinProperties {
        private final int index;

        public Stub(SkinProperties paranet, int index) {
            super();
            this.properties = paranet.properties;
            this.index = index;
        }

        @Override
        public <T> void put(SkinProperty<T> property, T value) {
            if (property.isMultipleKey()) {
                properties.put(getIndexedKey(property), value);
            } else {
                properties.put(property.getKey(), value);
            }
        }

        @Override
        public <T> void remove(SkinProperty<T> property) {
            if (property.isMultipleKey()) {
                properties.remove(getIndexedKey(property));
            } else {
                properties.remove(property.getKey());
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(SkinProperty<T> property) {
            if (property.isMultipleKey() && properties.containsKey(getIndexedKey(property))) {
                return (T) properties.getOrDefault(getIndexedKey(property), property.getDefaultValue());
            } else {
                return (T) properties.getOrDefault(property.getKey(), property.getDefaultValue());
            }
        }

        @Override
        public <T> boolean containsKey(SkinProperty<T> property) {
            if (property.isMultipleKey() && properties.containsKey(getIndexedKey(property))) {
                return true;
            }
            return properties.containsKey(property.getKey());
        }

        @Override
        public <T> boolean containsValue(SkinProperty<T> property) {
            if (property.isMultipleKey() && properties.containsValue(getIndexedKey(property))) {
                return true;
            }
            return properties.containsValue(property.getKey());
        }

        private String getIndexedKey(SkinProperty property) {
            return property.getKey() + String.valueOf(index);
        }
    }
}
