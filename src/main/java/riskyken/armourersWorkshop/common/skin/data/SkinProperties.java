package riskyken.armourersWorkshop.common.skin.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class SkinProperties {
    
    private final HashMap<String, Object> properties;
    
    public SkinProperties() {
        properties = new HashMap<String, Object>();
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
        }
    }
    
    public void readFromStream(DataInputStream stream) throws IOException {
        int count = stream.readInt();
        for (int i = 0; i < count; i++) {
            String key = stream.readUTF();
            DataTypes type = DataTypes.values()[stream.readByte()];
            Object value = null;
            switch (type) {
            case STRING:
                value = stream.readUTF();
                break;
            case INT:
                value = stream.readInt();
                break;
            }
            properties.put(key, value);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((properties == null) ? 0 : properties.hashCode());
        return result;
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
        INT
    }
}
