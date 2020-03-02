package moe.plushie.armourers_workshop.common.skin.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.io.IOUtils;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.common.skin.type.wings.SkinWings.MovementType;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.minecraft.nbt.NBTTagCompound;

public class SkinProperties implements ISkinProperties {

    // Properties for all skins.
    public static final SkinProperty<String> PROP_ALL_CUSTOM_NAME = new SkinProperty<String>("customName", "");
    public static final SkinProperty<String> PROP_ALL_FLAVOUR_TEXT = new SkinProperty<String>("flavour", "");
    public static final SkinProperty<String> PROP_ALL_AUTHOR_NAME = new SkinProperty<String>("authorName", "");
    public static final SkinProperty<String> PROP_ALL_AUTHOR_UUID = new SkinProperty<String>("authorUUID", "");

    // Properties.
    @Deprecated
    public static final SkinProperty<Boolean> PROP_MODEL_OVERRIDE = new SkinProperty<Boolean>("armourOverride", false);
    public static final SkinProperty<Boolean> PROP_MODEL_OVERRIDE_HEAD = new SkinProperty<Boolean>("overrideModelHead", false);
    public static final SkinProperty<Boolean> PROP_MODEL_OVERRIDE_CHEST = new SkinProperty<Boolean>("overrideModelChest", false);
    public static final SkinProperty<Boolean> PROP_MODEL_OVERRIDE_ARM_LEFT = new SkinProperty<Boolean>("overrideModelArmLeft", false);
    public static final SkinProperty<Boolean> PROP_MODEL_OVERRIDE_ARM_RIGHT = new SkinProperty<Boolean>("overrideModelArmRight", false);
    public static final SkinProperty<Boolean> PROP_MODEL_OVERRIDE_LEG_LEFT = new SkinProperty<Boolean>("overrideModelLegLeft", false);
    public static final SkinProperty<Boolean> PROP_MODEL_OVERRIDE_LEG_RIGHT = new SkinProperty<Boolean>("overrideModelLegRight", false);

    @Deprecated
    public static final SkinProperty<Boolean> PROP_MODEL_HIDE_OVERLAY = new SkinProperty<Boolean>("armourHideOverlay", false);
    public static final SkinProperty<Boolean> PROP_MODEL_HIDE_OVERLAY_HEAD = new SkinProperty<Boolean>("hideOverlayHead", false);
    public static final SkinProperty<Boolean> PROP_MODEL_HIDE_OVERLAY_CHEST = new SkinProperty<Boolean>("hideOverlayChest", false);
    public static final SkinProperty<Boolean> PROP_MODEL_HIDE_OVERLAY_ARM_LEFT = new SkinProperty<Boolean>("hideOverlayArmLeft", false);
    public static final SkinProperty<Boolean> PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT = new SkinProperty<Boolean>("hideOverlayArmRight", false);
    public static final SkinProperty<Boolean> PROP_MODEL_HIDE_OVERLAY_LEG_LEFT = new SkinProperty<Boolean>("hideOverlayLegLeft", false);
    public static final SkinProperty<Boolean> PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT = new SkinProperty<Boolean>("hideOverlayLegRight", false);

    public static final SkinProperty<Boolean> PROP_MODEL_LEGS_LIMIT_LIMBS = new SkinProperty<Boolean>("limitLimbs", false);

    public static final SkinProperty<String> PROP_OUTFIT_PART_INDEXS = new SkinProperty<String>("partIndexs", "");

    public static final SkinProperty<Boolean> PROP_BLOCK_GLOWING = new SkinProperty<Boolean>("blockGlowing", false);
    public static final SkinProperty<Boolean> PROP_BLOCK_LADDER = new SkinProperty<Boolean>("blockLadder", false);
    public static final SkinProperty<Boolean> PROP_BLOCK_NO_COLLISION = new SkinProperty<Boolean>("blockNoCollision", false);
    public static final SkinProperty<Boolean> PROP_BLOCK_SEAT = new SkinProperty<Boolean>("blockSeat", false);
    public static final SkinProperty<Boolean> PROP_BLOCK_MULTIBLOCK = new SkinProperty<Boolean>("blockMultiblock", false);
    public static final SkinProperty<Boolean> PROP_BLOCK_BED = new SkinProperty<Boolean>("blockBed", false);
    public static final SkinProperty<Boolean> PROP_BLOCK_INVENTORY = new SkinProperty<Boolean>("blockInventory", false);
    public static final SkinProperty<Boolean> PROP_BLOCK_ENDER_INVENTORY = new SkinProperty<Boolean>("blockEnderInventory", false);
    public static final SkinProperty<Integer> PROP_BLOCK_INVENTORY_WIDTH = new SkinProperty<Integer>("blockInventoryWidth", 9);
    public static final SkinProperty<Integer> PROP_BLOCK_INVENTORY_HEIGHT = new SkinProperty<Integer>("blockInventoryHeight", 4);

    public static final SkinProperty<Double> PROP_WINGS_MAX_ANGLE = new SkinProperty<Double>("wingsMaxAngle", 75D);
    public static final SkinProperty<Double> PROP_WINGS_MIN_ANGLE = new SkinProperty<Double>("wingsMinAngle", 0D);
    public static final SkinProperty<Double> PROP_WINGS_IDLE_SPEED = new SkinProperty<Double>("wingsIdleSpeed", 6000D);
    public static final SkinProperty<Double> PROP_WINGS_FLYING_SPEED = new SkinProperty<Double>("wingsFlyingSpeed", 350D);
    public static final SkinProperty<String> PROP_WINGS_MOVMENT_TYPE = new SkinProperty<String>("wingsMovmentType", MovementType.EASE.toString());

    private static final String TAG_SKIN_PROPS = "skinProps";

    // Properties for skin parts.
    public static final SkinProperty<String> PROP_PART_TARGET = new SkinProperty<String>("target", "");
    public static final SkinProperty<Boolean> PROP_PART_OVERRIDE = new SkinProperty<Boolean>("partOverride", false);
    public static final SkinProperty<Boolean> PROP_PART_HIDE_OVERLAY = new SkinProperty<Boolean>("partHideOverlay", false);
    public static final SkinProperty<Boolean> PROP_PART_LEGS_LIMIT_LIMB = new SkinProperty<Boolean>("limitLimb", false);
    
    // Legacy
    

    private final LinkedHashMap<String, Object> properties;

    public SkinProperties() {
        properties = new LinkedHashMap<String, Object>();
    }

    public SkinProperties(SkinProperties skinProps) {
        properties = (LinkedHashMap<String, Object>) skinProps.properties.clone();
    }

    public ArrayList<String> getPropertiesList() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < properties.size(); i++) {
            String key = (String) properties.keySet().toArray()[i];
            list.add(key + ":" + properties.get(key));
        }
        return list;
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

    @Override
    public void removeProperty(String key) {
        properties.remove(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public String getPropertyString(String key, String defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }

    @Override
    public int getPropertyInt(String key, int defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof Integer) {
            return (Integer) value;
        }
        return defaultValue;
    }

    @Override
    public double getPropertyDouble(String key, double defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof Double) {
            return (Double) value;
        }
        return defaultValue;
    }

    @Override
    public Boolean getPropertyBoolean(String key, Boolean defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    @Override
    public Object getProperty(String key, Object defaultValue) {
        Object value = properties.get(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    @Override
    public boolean haveProperty(String key) {
        return properties.containsKey(key);
    }

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

    private enum DataTypes {
        STRING, INT, DOUBLE, BOOLEAN
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey(TAG_SKIN_PROPS)) {
            return;
        }
        byte[] data = compound.getByteArray(TAG_SKIN_PROPS);

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(bais);
        try {
            readFromStream(dataInputStream, SkinSerializer.MAX_FILE_VERSION);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(dataInputStream);
            IOUtils.closeQuietly(bais);
        }
    }

    public void writeToNBT(NBTTagCompound compound) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(baos);
        try {
            writeToStream(dataOutputStream);
            dataOutputStream.flush();
            byte[] data = baos.toByteArray();
            compound.setByteArray(TAG_SKIN_PROPS, data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(dataOutputStream);
            IOUtils.closeQuietly(baos);
        }
    }
}
