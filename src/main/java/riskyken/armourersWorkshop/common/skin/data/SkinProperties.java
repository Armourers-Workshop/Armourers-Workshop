package riskyken.armourersWorkshop.common.skin.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.io.IOUtils;

import net.minecraft.nbt.NBTTagCompound;

public class SkinProperties {
    
    private static final String TAG_SKIN_PROPS = "skinProps";
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
            stream.writeUTF((String) key);
            if (value instanceof String) {
                stream.writeByte(DataTypes.STRING.ordinal());
                stream.writeUTF((String) value);
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
    
    public void readFromStream(DataInputStream stream) throws IOException {
        int count = stream.readInt();
        for (int i = 0; i < count; i++) {
            String key = stream.readUTF();
            byte typeByte = stream.readByte();
            if (typeByte >= 0 & typeByte < DataTypes.values().length) {
                DataTypes type = DataTypes.values()[typeByte];
                Object value = null;
                switch (type) {
                case STRING:
                    value = stream.readUTF();
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
            } else {
                throw new IOException("Error loading skin properties.");
            }
        }
    }
    
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    public String getPropertyString(String key, String defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }
    
    public int getPropertyInt(String key, int defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof Integer) {
            return (Integer) value;
        }
        return defaultValue;
    }
    
    public double getPropertyDouble(String key, double defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof Double) {
            return (Double) value;
        }
        return defaultValue;
    }
    
    public Boolean getPropertyBoolean(String key, Boolean defaultValue) {
        Object value = properties.get(key);
        if (value != null && value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
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
        STRING,
        INT,
        DOUBLE,
        BOOLEAN
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (!compound.hasKey(TAG_SKIN_PROPS)) {
            return;
        }
        byte[] data = compound.getByteArray(TAG_SKIN_PROPS);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(bais);
        try {
            readFromStream(dataInputStream);
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
