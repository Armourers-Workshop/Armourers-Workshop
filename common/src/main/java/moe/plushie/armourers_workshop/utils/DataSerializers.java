package moe.plushie.armourers_workshop.utils;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.common.IContainerLevelAccess;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.Strings;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataSerializers {

    public static final IEntitySerializer<CompoundTag> COMPOUND_TAG = of(EntityDataSerializers.COMPOUND_TAG);
    public static final IEntitySerializer<Integer> INT = of(EntityDataSerializers.INT);
    public static final IEntitySerializer<String> STRING = of(EntityDataSerializers.STRING);
    public static final IEntitySerializer<Boolean> BOOLEAN = of(EntityDataSerializers.BOOLEAN);
    public static final IEntitySerializer<Float> FLOAT = of(EntityDataSerializers.FLOAT);

    public static final IEntitySerializer<Vec3> VECTOR_3D = new IEntitySerializer<Vec3>() {
        @Override
        public void write(FriendlyByteBuf buffer, Vec3 pos) {
            buffer.writeDouble(pos.x());
            buffer.writeDouble(pos.y());
            buffer.writeDouble(pos.z());
        }

        @Override
        public Vec3 read(FriendlyByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
    };

    public static final IEntitySerializer<Vector3f> VECTOR_3F = new IEntitySerializer<Vector3f>() {
        @Override
        public void write(FriendlyByteBuf buffer, Vector3f pos) {
            buffer.writeFloat(pos.getX());
            buffer.writeFloat(pos.getY());
            buffer.writeFloat(pos.getZ());
        }

        @Override
        public Vector3f read(FriendlyByteBuf buffer) {
            return new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };

    public static final IEntitySerializer<IPaintColor> PAINT_COLOR = new IEntitySerializer<IPaintColor>() {
        @Override
        public void write(FriendlyByteBuf buffer, IPaintColor color) {
            buffer.writeInt(color.getRawValue());
        }

        @Override
        public IPaintColor read(FriendlyByteBuf buffer) {
            return PaintColor.of(buffer.readInt());
        }
    };

    public static final IEntitySerializer<PlayerTextureDescriptor> PLAYER_TEXTURE = new IEntitySerializer<PlayerTextureDescriptor>() {

        @Override
        public void write(FriendlyByteBuf buffer, PlayerTextureDescriptor descriptor) {
            buffer.writeNbt(descriptor.serializeNBT());
        }

        @Override
        public PlayerTextureDescriptor read(FriendlyByteBuf buffer) {
            return new PlayerTextureDescriptor(buffer.readNbt());
        }
    };

    public static final IEntitySerializer<Exception> EXCEPTION = new IEntitySerializer<Exception>() {

        public void write(FriendlyByteBuf buffer, Exception exception) {
            OutputStream outputStream = null;
            ObjectOutputStream objectOutputStream = null;
            try {
                boolean compress = ModConfig.Common.enableServerCompressesSkins;
                buffer.writeBoolean(compress);
                outputStream = createOutputStream(buffer, compress);
                objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(exception);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            } finally {
                StreamUtils.closeQuietly(objectOutputStream, outputStream);
            }
        }

        public Exception read(FriendlyByteBuf buffer) {
            InputStream inputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                boolean compress = buffer.readBoolean();
                inputStream = createInputStream(buffer, compress);
                objectInputStream = new ObjectInputStream(inputStream);
                return (Exception) objectInputStream.readObject();
            } catch (Exception exception) {
                return exception;
            } finally {
                StreamUtils.closeQuietly(objectInputStream, inputStream);
            }
        }

        public Exception copy(Exception value) {
            return value;
        }

        private InputStream createInputStream(FriendlyByteBuf buffer, boolean compress) throws Exception {
            InputStream inputStream = new ByteBufInputStream(buffer);
            if (compress) {
                return new GZIPInputStream(inputStream);
            }
            return inputStream;
        }

        private OutputStream createOutputStream(FriendlyByteBuf buffer, boolean compress) throws Exception {
            ByteBufOutputStream outputStream = new ByteBufOutputStream(buffer);
            if (compress) {
                return new GZIPOutputStream(outputStream);
            }
            return outputStream;
        }
    };

    public static final IPlayerDataSerializer<SkinWardrobe> ENTITY_WARDROBE = new IPlayerDataSerializer<SkinWardrobe>() {
        public void write(FriendlyByteBuf buffer, Player player, SkinWardrobe wardrobe) {
            buffer.writeInt(wardrobe.getId());
            buffer.writeResourceLocation(wardrobe.getProfile().getRegistryName());
        }

        public SkinWardrobe read(FriendlyByteBuf buffer, Player player) {
            if (player == null || player.getLevel() == null) {
                return null;
            }
            int entityId = buffer.readInt();
            Entity entity = player.getLevel().getEntity(entityId);
            if (entity == null) {
                for (Player player1 : player.getLevel().players()) {
                    if (player1.getId() == entityId) {
                        entity = player1;
                        break;
                    }
                }
            }
            SkinWardrobe wardrobe = SkinWardrobe.of(entity);
            EntityProfile serverProfile = ModEntityProfiles.getProfile(buffer.readResourceLocation());
            if (wardrobe != null && serverProfile != null) {
                // we need to maintain consistency of the entity profile,
                // some strange mods(e.g.: taterzens) deliberately make the
                // entity type inconsistent by server side and client side
                wardrobe.setProfile(serverProfile);
            }
            return wardrobe;
        }
    };

    public static final IPlayerDataSerializer<IContainerLevelAccess> WORLD_POS = new IPlayerDataSerializer<IContainerLevelAccess>() {
        public void write(FriendlyByteBuf buffer, Player player, IContainerLevelAccess callable) {
            Optional<BlockPos> pos1 = callable.evaluate((world, pos) -> pos);
            buffer.writeBlockPos(pos1.orElse(BlockPos.ZERO));
            // buffer.writeNbt(callable.extraData());
        }

        public IContainerLevelAccess read(FriendlyByteBuf buffer, Player player) {
            if (player == null || player.getLevel() == null) {
                return null;
            }
            BlockPos blockPos = buffer.readBlockPos();
            // CompoundTag extraNBT = buffer.readNbt(); 
            return IContainerLevelAccess.create(player.getLevel(), blockPos, null);
        }
    };

    public static final IPlayerDataSerializer<ISkinType> SKIN_TYPE = new IPlayerDataSerializer<ISkinType>() {
        public void write(FriendlyByteBuf buffer, Player player, ISkinType value) {
            buffer.writeUtf(value.getRegistryName().toString());
        }

        public ISkinType read(FriendlyByteBuf buffer, Player player) {
            return SkinTypes.byName(buffer.readUtf(Short.MAX_VALUE));
        }
    };

    public static final IPlayerDataSerializer<SkinProperties> SKIN_PROPERTIES = new IPlayerDataSerializer<SkinProperties>() {
        public void write(FriendlyByteBuf buffer, Player player, SkinProperties value) {
            CompoundTag nbt = new CompoundTag();
            value.writeToNBT(nbt);
            buffer.writeNbt(nbt);
        }

        public SkinProperties read(FriendlyByteBuf buffer, Player player) {
            SkinProperties properties = new SkinProperties();
            CompoundTag nbt = buffer.readNbt();
            if (nbt != null) {
                properties.readFromNBT(nbt);
            }
            return properties;
        }
    };

    private static final Random RANDOM = new Random();

    public static <T> IEntitySerializer<T> of(EntityDataSerializer<T> serializer) {
        return new IEntitySerializer<T>() {
            @Override
            public T read(FriendlyByteBuf buffer) {
                return serializer.read(buffer);
            }

            @Override
            public void write(FriendlyByteBuf buffer, T descriptor) {
                serializer.write(buffer, descriptor);
            }
        };
    }

    public static void mirrorRotations(CompoundTag source, String sourceKey, Rotations sourceDefaultValue, CompoundTag target, String targetKey, Rotations targetDefaultValue) {
        Rotations rot = source.getOptionalRotations(sourceKey, sourceDefaultValue);
        rot = new Rotations(rot.getX(), -rot.getY(), -rot.getZ());
        target.putOptionalRotations(targetKey, rot, targetDefaultValue);
    }

    public static GameProfile readGameProfile(CompoundTag tag) {
        String name = null;
        UUID id = null;
        if (tag.contains("Name", 8)) {
            name = tag.getString("Name");
        }
        if (tag.hasUUID("Id")) {
            id = tag.getUUID("Id");
        }
        try {
            return new GameProfile(id, name);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static CompoundTag writeGameProfile(CompoundTag tag, GameProfile value) {
        if (value == null) {
            return tag;
        }
        if (Strings.isNotBlank(value.getName())) {
            tag.putString("Name", value.getName());
        }
        if (value.getId() != null) {
            tag.putUUID("Id", value.getId());
        }
        return tag;
    }

    public static void dropContents(Level level, BlockPos blockPos, Container container) {
        dropContents(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), container);
    }

    public static void dropContents(Level level, Entity entity, Container container) {
        dropContents(level, entity.getX(), entity.getY(), entity.getZ(), container);
    }

    private static void dropContents(Level level, double x, double y, double z, Container container) {
        for (int i = 0; i < container.getContainerSize(); ++i) {
            dropItemStack(level, x, y, z, container.getItem(i));
        }
    }

    public static void dropContents(Level level, BlockPos blockPos, NonNullList<ItemStack> itemStacks) {
        itemStacks.forEach(itemStack -> dropItemStack(level, blockPos, itemStack));
    }

    public static void dropItemStack(Level level, BlockPos blockPos, ItemStack itemStack) {
        dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
    }

    public static void dropItemStack(Level level, double x, double y, double z, ItemStack itemStack) {
        double d0 = EntityType.ITEM.getWidth();
        double d1 = 1.0D - d0;
        double d2 = d0 / 2.0D;
        double d3 = Math.floor(x) + RANDOM.nextDouble() * d1 + d2;
        double d4 = Math.floor(y) + RANDOM.nextDouble() * d1;
        double d5 = Math.floor(z) + RANDOM.nextDouble() * d1 + d2;

        while (!itemStack.isEmpty()) {
            ItemEntity itementity = new ItemEntity(level, d3, d4, d5, itemStack.split(RANDOM.nextInt(21) + 10));
            float f = 0.05F;
            itementity.setDeltaMovement(RANDOM.nextGaussian() * (double) 0.05F, RANDOM.nextGaussian() * (double) 0.05F + (double) 0.2F, RANDOM.nextGaussian() * (double) 0.05F);
            level.addFreshEntity(itementity);
        }
    }
}
