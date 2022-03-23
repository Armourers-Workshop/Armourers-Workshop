package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class AWDataSerializers {

    public static final IDataSerializer<Vector3d> VECTOR_3D = new IDataSerializer<Vector3d>() {
        public void write(PacketBuffer buffer, Vector3d pos) {
            buffer.writeDouble(pos.x());
            buffer.writeDouble(pos.y());
            buffer.writeDouble(pos.z());
        }

        public Vector3d read(PacketBuffer buffer) {
            return new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        public Vector3d copy(Vector3d pos) {
            return pos;
        }
    };

    public static final IDataSerializer<Vector3f> VECTOR_3F = new IDataSerializer<Vector3f>() {
        public void write(PacketBuffer buffer, Vector3f pos) {
            buffer.writeFloat(pos.x());
            buffer.writeFloat(pos.y());
            buffer.writeFloat(pos.z());
        }

        public Vector3f read(PacketBuffer buffer) {
            return new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }

        public Vector3f copy(Vector3f pos) {
            return pos;
        }
    };

    public static final IDataSerializer<PaintColor> PAINT_COLOR = new IDataSerializer<PaintColor>() {
        public void write(PacketBuffer buffer, PaintColor pos) {
            buffer.writeInt(pos.getValue());
        }

        public PaintColor read(PacketBuffer buffer) {
            return PaintColor.of(buffer.readInt());
        }

        public PaintColor copy(PaintColor value) {
            return value;
        }
    };


    @SuppressWarnings("NullableProblems")
    public static final IDataSerializer<PlayerTextureDescriptor> PLAYER_TEXTURE = new IDataSerializer<PlayerTextureDescriptor>() {
        public void write(PacketBuffer buffer, PlayerTextureDescriptor descriptor) {
            buffer.writeNbt(descriptor.serializeNBT());
        }

        public PlayerTextureDescriptor read(PacketBuffer buffer) {
            return new PlayerTextureDescriptor(buffer.readNbt());
        }

        public PlayerTextureDescriptor copy(PlayerTextureDescriptor descriptor) {
            return descriptor;
        }
    };


    @Nullable
    public static IWorldPosCallable readWorldPos(PlayerEntity player, PacketBuffer buffer) {
        if (player.level == null) {
            return null;
        }
        BlockPos pos = buffer.readBlockPos();
        return IWorldPosCallable.create(player.level, pos);
    }

    public static void writeWorldPos(IWorldPosCallable callable, PacketBuffer buffer) {
        Optional<BlockPos> pos1 = callable.evaluate((world, pos) -> pos);
        buffer.writeBlockPos(pos1.orElse(BlockPos.ZERO));
    }

    @Nullable
    public static SkinWardrobe readEntityWardrobe(PlayerEntity player, PacketBuffer buffer) {
        if (player.level == null) {
            return null;
        }
        return SkinWardrobe.of(player.level.getEntity(buffer.readInt()));
    }

    public static void writeEntityWardrobe(SkinWardrobe wardrobe, PacketBuffer buffer) {
        buffer.writeInt(wardrobe.getId());
    }

    public static Vector3i getVector3i(CompoundNBT nbt, String key) {
        ListNBT listNBT = nbt.getList(key, Constants.NBT.TAG_INT);
        if (listNBT.size() >= 3) {
            return new Vector3i(listNBT.getInt(0), listNBT.getInt(1), listNBT.getInt(2));
        }
        return new Vector3i(0, 0, 0);
    }

    public static void putVector3i(CompoundNBT nbt, String key, Vector3i vector) {
        int x = vector.getX(), y = vector.getY(), z = vector.getZ();
        if (x == 0 && y == 0 && z == 0) {
            return;
        }
        ListNBT listnbt = new ListNBT();
        listnbt.add(IntNBT.valueOf(x));
        listnbt.add(IntNBT.valueOf(y));
        listnbt.add(IntNBT.valueOf(z));
        nbt.put(key, listnbt);
    }

    public static Vector3f getVector3f(CompoundNBT nbt, String key) {
        ListNBT listNBT = nbt.getList(key, Constants.NBT.TAG_FLOAT);
        if (listNBT.size() >= 3) {
            return new Vector3f(listNBT.getFloat(0), listNBT.getFloat(1), listNBT.getFloat(2));
        }
        return new Vector3f(0, 0, 0);
    }

    public static void putVector3f(CompoundNBT nbt, String key, Vector3f vector) {
        float x = vector.x(), y = vector.y(), z = vector.z();
        if (x == 0 && y == 0 && z == 0) {
            return;
        }
        ListNBT listnbt = new ListNBT();
        listnbt.add(FloatNBT.valueOf(x));
        listnbt.add(FloatNBT.valueOf(y));
        listnbt.add(FloatNBT.valueOf(z));
        nbt.put(key, listnbt);
    }

    public static Rectangle3i getRectangle3i(CompoundNBT nbt, String key, Rectangle3i defaultValue) {
        ListNBT listNBT = nbt.getList(key, Constants.NBT.TAG_INT);
        if (listNBT.size() >= 6) {
            return new Rectangle3i(listNBT.getInt(0), listNBT.getInt(1), listNBT.getInt(2), listNBT.getInt(3), listNBT.getInt(4), listNBT.getInt(5));
        }
        return defaultValue;
    }

    public static void putRectangle3i(CompoundNBT nbt, String key, Rectangle3i value, Rectangle3i defaultValue) {
        if (value.equals(defaultValue)) {
            return;
        }
        ListNBT listnbt = new ListNBT();
        listnbt.add(IntNBT.valueOf(value.getX()));
        listnbt.add(IntNBT.valueOf(value.getY()));
        listnbt.add(IntNBT.valueOf(value.getZ()));
        listnbt.add(IntNBT.valueOf(value.getWidth()));
        listnbt.add(IntNBT.valueOf(value.getHeight()));
        listnbt.add(IntNBT.valueOf(value.getDepth()));
        nbt.put(key, listnbt);
    }

    public static boolean getBoolean(CompoundNBT nbt, String key, boolean defaultValue) {
        if (nbt.contains(key, Constants.NBT.TAG_BYTE)) {
            return nbt.getBoolean(key);
        }
        return defaultValue;
    }

    public static void putBoolean(CompoundNBT nbt, String key, boolean value, boolean defaultValue) {
        if (defaultValue != value) {
            nbt.putBoolean(key, value);
        }
    }

    public static int getInt(CompoundNBT nbt, String key, int defaultValue) {
        if (nbt.contains(key, Constants.NBT.TAG_INT)) {
            return nbt.getInt(key);
        }
        return defaultValue;
    }

    public static void putInt(CompoundNBT nbt, String key, int value, int defaultValue) {
        if (defaultValue != value) {
            nbt.putInt(key, value);
        }
    }

    public static float getFloat(CompoundNBT nbt, String key, float defaultValue) {
        if (nbt.contains(key, Constants.NBT.TAG_FLOAT)) {
            return nbt.getFloat(key);
        }
        return defaultValue;
    }

    public static void putFloat(CompoundNBT nbt, String key, float value, float defaultValue) {
        if (defaultValue != value) {
            nbt.putFloat(key, value);
        }
    }

    public static void putRotations(CompoundNBT nbt, String key, Rotations value, Rotations defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.put(key, value.save());
        }
    }

    public static Rotations getRotations(CompoundNBT nbt, String key, Rotations defaultValue) {
        ListNBT listNBT = nbt.getList(key, Constants.NBT.TAG_FLOAT);
        if (listNBT.size() >= 3) {
            return new Rotations(listNBT);
        }
        return defaultValue;
    }

    public static void putSkinProperties(CompoundNBT nbt, String key, SkinProperties properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        CompoundNBT propertiesTag = new CompoundNBT();
        properties.writeToNBT(propertiesTag);
        nbt.put(key, propertiesTag);
    }

    public static SkinProperties getSkinProperties(CompoundNBT nbt, String key) {
        SkinProperties properties = new SkinProperties();
        if (nbt.contains(key, Constants.NBT.TAG_COMPOUND)) {
            properties.readFromNBT(nbt.getCompound(key));
        }
        return properties;
    }

    public static void putPaintColor(CompoundNBT nbt, String key, PaintColor value, PaintColor defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.putInt(key, value.getValue());
        } else {
            nbt.remove(key);
        }
    }

    public static PaintColor getPaintColor(CompoundNBT nbt, String key, PaintColor defaultValue) {
        if (nbt.contains(key, Constants.NBT.TAG_INT)) {
            return PaintColor.of(nbt.getInt(key));
        }
        return defaultValue;
    }


    public static void putSkinDescriptor(CompoundNBT nbt, String key, SkinDescriptor value, SkinDescriptor defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.put(key, value.serializeNBT());
        }
    }

    public static SkinDescriptor getSkinDescriptor(CompoundNBT nbt, String key, SkinDescriptor defaultValue) {
        if (nbt.contains(key, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT nbt1 = nbt.getCompound(key);
            if (!nbt1.isEmpty()) {
                return new SkinDescriptor(nbt1);
            }
        }
        return defaultValue;
    }

    public static void putTextureDescriptor(CompoundNBT nbt, String key, PlayerTextureDescriptor value, PlayerTextureDescriptor defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.put(key, value.serializeNBT());
        }
    }

    public static PlayerTextureDescriptor getTextureDescriptor(CompoundNBT nbt, String key, PlayerTextureDescriptor defaultValue) {
        if (nbt.contains(key, Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT nbt1 = nbt.getCompound(key);
            if (!nbt1.isEmpty()) {
                return new PlayerTextureDescriptor(nbt1);
            }
        }
        return defaultValue;
    }

    public static void putBlockPos(CompoundNBT nbt, String key, BlockPos value, BlockPos defaultValue) {
        if (!Objects.equals(value, defaultValue)) {
            nbt.putLong(key, value.asLong());
        } else {
            nbt.remove(key);
        }
    }

    public static BlockPos getBlockPos(CompoundNBT nbt, String key, BlockPos defaultValue) {
        if (nbt.contains(key, Constants.NBT.TAG_LONG)) {
            return BlockPos.of(nbt.getLong(key));
        }
        return defaultValue;
    }

    public static void putBlockPosList(CompoundNBT nbt, String key, Collection<BlockPos> elements) {
        if (elements.isEmpty()) {
            return;
        }
        ArrayList<Long> list = new ArrayList<>(elements.size());
        for (BlockPos pos : elements) {
            list.add(pos.asLong());
        }
        nbt.putLongArray(key, list);
    }

    public static Collection<BlockPos> getBlockPosList(CompoundNBT nbt, String key) {
        ArrayList<BlockPos> elements = new ArrayList<>();
        if (nbt.contains(key, Constants.NBT.TAG_LONG_ARRAY)) {
            for (long value : nbt.getLongArray(key)) {
                elements.add(BlockPos.of(value));
            }
        }
        return elements;
    }

    public static void putMarkerList(CompoundNBT nbt, String key, Collection<SkinMarker> elements) {
        if (elements.isEmpty()) {
            return;
        }
        ArrayList<Long> list = new ArrayList<>(elements.size());
        for (SkinMarker marker : elements) {
            list.add(marker.asLong());
        }
        nbt.putLongArray(key, list);
    }

    public static Collection<SkinMarker> getMarkerList(CompoundNBT nbt, String key) {
        ArrayList<SkinMarker> elements = new ArrayList<>();
        if (nbt.contains(key, Constants.NBT.TAG_LONG_ARRAY)) {
            for (long value : nbt.getLongArray(key)) {
                elements.add(SkinMarker.of(value));
            }
        }
        return elements;
    }

}
