package moe.plushie.armourers_workshop.common.property;

import java.util.HashMap;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.nbt.NBTTagCompound;

public final class TilePropertyManager {
    
    public static TilePropertyManager INSTANCE = new TilePropertyManager();
    
    private final HashMap<String, ITypeSerializer<NBTTagCompound>> typeSerializers;
    
    private TilePropertyManager() {
        typeSerializers = new HashMap<String, ITypeSerializer<NBTTagCompound>>();
        typeSerializers.put(Boolean.class.getName(), new BooleanSerializer());
        typeSerializers.put(Integer.class.getName(), new IntegerSerializer());
        typeSerializers.put(String.class.getName(), new StringSerializer());
    }
    
    public NBTTagCompound writePropToCompound(TileProperty<?> tileProperty, NBTTagCompound compound) {
        if (tileProperty.get() != null) {
            String typeKey = tileProperty.get().getClass().getName();
            ITypeSerializer<NBTTagCompound> serializer = typeSerializers.get(typeKey);
            if (serializer != null) {
                serializer.writeType(tileProperty, compound);
            } else {
                ModLogger.log(Level.ERROR, "Could not find TypeSerializer for type: " + typeKey);
            }
        }
        return compound;
    }
    
    public void readPropFromCompound(TileProperty<?> tileProperty, NBTTagCompound compound) {
        String typeKey = tileProperty.getDefault().getClass().getName();
        ITypeSerializer<NBTTagCompound> serializer = typeSerializers.get(typeKey);
        if (serializer != null) {
            serializer.readType(tileProperty, compound);
        } else {
            ModLogger.log(Level.ERROR, "Could not find TypeSerializer for type: " + typeKey);
        }
    }
    
    private static class BooleanSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            tileProperty.loadType(source.getBoolean(tileProperty.getKey()));
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            target.setBoolean(tileProperty.getKey(), (Boolean) tileProperty.get());
        }
    }
    
    private static class IntegerSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            tileProperty.loadType(source.getInteger(tileProperty.getKey()));
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            target.setInteger(tileProperty.getKey(), (Integer) tileProperty.get());
        }
    }
    
    private static class StringSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            tileProperty.loadType(source.getString(tileProperty.getKey()));
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            target.setString(tileProperty.getKey(), (String) tileProperty.get());
        }
    }
    
    private static interface ITypeSerializer<TAR_TYPE> {
        
        public void readType(TileProperty<?> tileProperty, TAR_TYPE source);
        
        public void writeType(TileProperty<?> tileProperty, TAR_TYPE target);
    }
}
