package moe.plushie.armourers_workshop.common.tileentities.property;

import java.util.HashMap;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector.PowerMode;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.util.Constants.NBT;

public final class TilePropertyManager {

    public static TilePropertyManager INSTANCE = new TilePropertyManager();

    private final HashMap<String, ITypeSerializer<NBTTagCompound>> typeSerializers;

    private TilePropertyManager() {
        typeSerializers = new HashMap<String, ITypeSerializer<NBTTagCompound>>();
        typeSerializers.put(Boolean.class.getName(), new BooleanSerializer());
        typeSerializers.put(Integer.class.getName(), new IntegerSerializer());
        typeSerializers.put(Float.class.getName(), new FloatSerializer());
        typeSerializers.put(String.class.getName(), new StringSerializer());
        typeSerializers.put(GameProfile.class.getName(), new GameProfileSerializer());
        typeSerializers.put(BipedRotations.class.getName(), new BipedRotationsSerializer());
        typeSerializers.put(ExtraColours.class.getName(), new ExtraColoursSerializer());
        typeSerializers.put(TextureType.class.getName(), new TextureTypeSerializer());
        typeSerializers.put(PowerMode.class.getName(), new PowerModeSerializer());
    }

    public NBTTagCompound writePropToCompound(TileProperty<?> tileProperty, NBTTagCompound compound) {
        if (tileProperty.get() != null) {
            String typeKey = tileProperty.getType().getName();
            ITypeSerializer<NBTTagCompound> serializer = typeSerializers.get(typeKey);
            if (serializer != null) {
                serializer.writeType(tileProperty, compound);
            } else {
                ModLogger.log(Level.ERROR, "Could not find TypeSerializer when writing for type: " + typeKey);
            }
        }
        return compound;
    }

    public void readPropFromCompound(TileProperty<?> tileProperty, NBTTagCompound compound) {
        String typeKey = tileProperty.getType().getName();
        ITypeSerializer<NBTTagCompound> serializer = typeSerializers.get(typeKey);
        if (serializer != null) {
            serializer.readType(tileProperty, compound);
        } else {
            ModLogger.log(Level.ERROR, "Could not find TypeSerializer when reading for type: " + typeKey);
        }
    }

    private static class BooleanSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_BYTE)) {
                tileProperty.loadType(source.getBoolean(tileProperty.getKey()));
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            target.setBoolean(tileProperty.getKey(), (Boolean) tileProperty.get());
        }
    }

    private static class IntegerSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_INT)) {
                tileProperty.loadType(source.getInteger(tileProperty.getKey()));
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            target.setInteger(tileProperty.getKey(), (Integer) tileProperty.get());
        }
    }

    private static class FloatSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_FLOAT)) {
                tileProperty.loadType(source.getFloat(tileProperty.getKey()));
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            target.setFloat(tileProperty.getKey(), (Float) tileProperty.get());
        }
    }

    private static class StringSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_STRING)) {
                tileProperty.loadType(source.getString(tileProperty.getKey()));
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            target.setString(tileProperty.getKey(), (String) tileProperty.get());
        }
    }

    private static class GameProfileSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_COMPOUND)) {
                GameProfile gameProfile = NBTUtil.readGameProfileFromNBT(source.getCompoundTag(tileProperty.getKey()));
                tileProperty.loadType(gameProfile);
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            NBTTagCompound compound = NBTUtil.writeGameProfile(new NBTTagCompound(), (GameProfile) tileProperty.get());
            target.setTag(tileProperty.getKey(), compound);
        }
    }

    private static class BipedRotationsSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_COMPOUND)) {
                BipedRotations rotations = (BipedRotations) tileProperty.get();
                rotations.loadNBTData(source.getCompoundTag(tileProperty.getKey()));
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            BipedRotations rotations = (BipedRotations) tileProperty.get();
            target.setTag(tileProperty.getKey(), rotations.saveNBTData(new NBTTagCompound()));
        }
    }

    private static class ExtraColoursSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_COMPOUND)) {
                NBTTagCompound compound = source.getCompoundTag(tileProperty.getKey());
                ExtraColours extraColours = (ExtraColours) tileProperty.get();
                for (ExtraColourType colourType : ExtraColourType.values()) {
                    extraColours.setColour(colourType, compound.getInteger(colourType.name().toLowerCase()));
                }
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            NBTTagCompound compound = new NBTTagCompound();
            ExtraColours extraColours = (ExtraColours) tileProperty.get();
            for (ExtraColourType colourType : ExtraColourType.values()) {
                compound.setInteger(colourType.name().toLowerCase(), extraColours.getColour(colourType));
            }
            target.setTag(tileProperty.getKey(), compound);
        }
    }

    private static class TextureTypeSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_STRING)) {
                try {
                    TextureType tt = TextureType.valueOf(source.getString(tileProperty.getKey()));
                    tileProperty.loadType(tt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            TextureType tt = (TextureType) tileProperty.get();
            target.setString(tileProperty.getKey(), tt.name());
        }
    }

    private static class PowerModeSerializer implements ITypeSerializer<NBTTagCompound> {

        @Override
        public void readType(TileProperty<?> tileProperty, NBTTagCompound source) {
            if (source.hasKey(tileProperty.getKey(), NBT.TAG_STRING)) {
                try {
                    PowerMode pm = PowerMode.valueOf(source.getString(tileProperty.getKey()));
                    tileProperty.loadType(pm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void writeType(TileProperty<?> tileProperty, NBTTagCompound target) {
            PowerMode pm = (PowerMode) tileProperty.get();
            target.setString(tileProperty.getKey(), pm.name());
        }
    }

    private static interface ITypeSerializer<TAR_TYPE> {

        public void readType(TileProperty<?> tileProperty, TAR_TYPE source);

        public void writeType(TileProperty<?> tileProperty, TAR_TYPE target);
    }
}
