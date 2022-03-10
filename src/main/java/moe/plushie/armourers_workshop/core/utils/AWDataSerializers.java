package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;

public class AWDataSerializers {

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
        if (!defaultValue.equals(value)) {
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

    public static void putTextureDescriptor(CompoundNBT nbt, String key, PlayerTextureDescriptor value, PlayerTextureDescriptor defaultValue) {
        if (!defaultValue.equals(value)) {
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
}
