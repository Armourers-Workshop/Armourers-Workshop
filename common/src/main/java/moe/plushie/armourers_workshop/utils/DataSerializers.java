package moe.plushie.armourers_workshop.utils;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractEntityDataSerializer;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.EntityCollisionShape;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintData;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.StreamUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("unused")
public class DataSerializers {

    public static final IDataCodec<IResourceLocation> RESOURCE_LOCATION = IDataCodec.STRING.xmap(OpenResourceLocation::parse, IResourceLocation::toString);
    public static final IDataCodec<OpenRectangle3f> BOUNDING_BOX = IDataCodec.FLOAT.listOf().xmap(OpenRectangle3f::new, OpenRectangle3f::toList);
    public static final IDataCodec<SkinPaintData> COMPRESSED_PAINT_DATA = IDataCodec.BYTE_BUFFER.xmap(DataSerializers::decompressPaintData, DataSerializers::compressPaintData);
    public static final IDataCodec<EntityTextureDescriptor.Model> ENTITY_TEXTURE_MODEL = IDataCodec.INT.xmap(DataSerializers::parseTextureModel, EntityTextureDescriptor.Model::ordinal);

    public static final IEntitySerializer<CompoundTag> COMPOUND_TAG = of(EntityDataSerializers.COMPOUND_TAG);
    public static final IEntitySerializer<Integer> INT = of(EntityDataSerializers.INT);
    public static final IEntitySerializer<String> STRING = of(EntityDataSerializers.STRING);
    public static final IEntitySerializer<Boolean> BOOLEAN = of(EntityDataSerializers.BOOLEAN);
    public static final IEntitySerializer<Float> FLOAT = of(EntityDataSerializers.FLOAT);

    public static final IEntitySerializer<Vec3> VECTOR_3D = new IEntitySerializer<Vec3>() {
        @Override
        public void write(IFriendlyByteBuf buffer, Vec3 pos) {
            buffer.writeDouble(pos.x());
            buffer.writeDouble(pos.y());
            buffer.writeDouble(pos.z());
        }

        @Override
        public Vec3 read(IFriendlyByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
    };

    public static final IEntitySerializer<OpenVector3f> VECTOR_3F = new IEntitySerializer<OpenVector3f>() {
        @Override
        public void write(IFriendlyByteBuf buffer, OpenVector3f pos) {
            buffer.writeFloat(pos.x());
            buffer.writeFloat(pos.y());
            buffer.writeFloat(pos.z());
        }

        @Override
        public OpenVector3f read(IFriendlyByteBuf buffer) {
            return new OpenVector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };

    public static final IEntitySerializer<SkinPaintColor> PAINT_COLOR = new IEntitySerializer<SkinPaintColor>() {
        @Override
        public void write(IFriendlyByteBuf buffer, SkinPaintColor color) {
            buffer.writeInt(color.rawValue());
        }

        @Override
        public SkinPaintColor read(IFriendlyByteBuf buffer) {
            return SkinPaintColor.of(buffer.readInt());
        }
    };

    public static final IEntitySerializer<EntityTextureDescriptor> PLAYER_TEXTURE = new IEntitySerializer<EntityTextureDescriptor>() {

        @Override
        public void write(IFriendlyByteBuf buffer, EntityTextureDescriptor descriptor) {
            buffer.writeNbtWithCodec(EntityTextureDescriptor.CODEC, descriptor);
        }

        @Override
        public EntityTextureDescriptor read(IFriendlyByteBuf buffer) {
            return buffer.readNbtWithCodec(EntityTextureDescriptor.CODEC);
        }
    };

    public static final IEntitySerializer<EntityTextureDescriptor.Model> PLAYER_TEXTURE_MODEL = new IEntitySerializer<EntityTextureDescriptor.Model>() {

        @Override
        public void write(IFriendlyByteBuf buffer, EntityTextureDescriptor.Model descriptor) {
            buffer.writeInt(descriptor.ordinal());
        }

        @Override
        public EntityTextureDescriptor.Model read(IFriendlyByteBuf buffer) {
            return EntityTextureDescriptor.Model.values()[buffer.readInt()];
        }
    };


    public static final IEntitySerializer<EntityCollisionShape> COLLISION_SHAPE_OPT = new IEntitySerializer<EntityCollisionShape>() {
        @Override
        public EntityCollisionShape read(IFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            if (size == 0) {
                return null;
            }
            var values = new ArrayList<Float>();
            for (int i = 0; i < size; ++i) {
                values.add(buffer.readFloat());
            }
            return new EntityCollisionShape(values);
        }

        @Override
        public void write(IFriendlyByteBuf buffer, EntityCollisionShape shape) {
            var values = Objects.flatMap(shape, EntityCollisionShape::toList);
            if (values == null) {
                values = Collections.emptyList();
            }
            buffer.writeVarInt(values.size());
            for (var value : values) {
                buffer.writeFloat(value);
            }
        }
    };

    public static final IEntitySerializer<Exception> EXCEPTION = new IEntitySerializer<Exception>() {

        public void write(IFriendlyByteBuf buffer, Exception exception) {
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

        public Exception read(IFriendlyByteBuf buffer) {
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

        private InputStream createInputStream(IFriendlyByteBuf buffer, boolean compress) throws Exception {
            var inputStream = new ByteBufInputStream(buffer.asByteBuf());
            if (compress) {
                return new GZIPInputStream(inputStream);
            }
            return inputStream;
        }

        private OutputStream createOutputStream(IFriendlyByteBuf buffer, boolean compress) throws Exception {
            var outputStream = new ByteBufOutputStream(buffer.asByteBuf());
            if (compress) {
                return new GZIPOutputStream(outputStream);
            }
            return outputStream;
        }
    };

    public static final IMenuSerializer<SkinWardrobe> ENTITY_WARDROBE = new IMenuSerializer<SkinWardrobe>() {
        public void write(IFriendlyByteBuf buffer, Player player, SkinWardrobe wardrobe) {
            buffer.writeInt(wardrobe.id());
            buffer.writeResourceLocation(wardrobe.profile().registryName());
        }

        public SkinWardrobe read(IFriendlyByteBuf buffer, Player player) {
            if (player == null || player.getLevel() == null) {
                return null;
            }
            var entityId = buffer.readInt();
            var entity = player.getLevel().getEntity(entityId);
            if (entity == null) {
                for (Player player1 : player.getLevel().players()) {
                    if (player1.getId() == entityId) {
                        entity = player1;
                        break;
                    }
                }
            }
            var wardrobe = SkinWardrobe.of(entity);
            var serverProfile = ModEntityProfiles.getProfile(buffer.readResourceLocation());
            if (wardrobe != null && serverProfile != null) {
                // we need to maintain consistency of the entity profile,
                // some strange mods(e.g.: taterzens) deliberately make the
                // entity type inconsistent by server side and client side
                wardrobe.setProfile(serverProfile);
            }
            return wardrobe;
        }
    };

    public static final IMenuSerializer<IGlobalPos> GLOBAL_POS = new IMenuSerializer<IGlobalPos>() {
        public void write(IFriendlyByteBuf buffer, Player player, IGlobalPos callable) {
            var pos1 = callable.evaluate((world, pos) -> pos);
            buffer.writeBlockPos(pos1.orElse(BlockPos.ZERO));
        }

        public IGlobalPos read(IFriendlyByteBuf buffer, Player player) {
            if (player == null || player.getLevel() == null) {
                return null;
            }
            var blockPos = buffer.readBlockPos();
            return IGlobalPos.create(player.getLevel(), blockPos);
        }
    };

    public static final IPlayerDataSerializer<SkinType> SKIN_TYPE = new IPlayerDataSerializer<SkinType>() {

        @Override
        public void write(IFriendlyByteBuf buffer, Player player, SkinType value) {
            buffer.writeUtf(value.registryName().toString());
        }

        @Override
        public SkinType read(IFriendlyByteBuf buffer, Player player) {
            return SkinTypes.byName(buffer.readUtf());
        }
    };

    public static final IPlayerDataSerializer<SkinProperties> SKIN_PROPERTIES = new IPlayerDataSerializer<SkinProperties>() {

        @Override
        public void write(IFriendlyByteBuf buffer, Player player, SkinProperties value) {
            var nbt = new CompoundTag();
            value.writeToNBT(nbt);
            buffer.writeNbt(nbt);
        }

        @Override
        public SkinProperties read(IFriendlyByteBuf buffer, Player player) {
            var properties = new SkinProperties();
            var nbt = buffer.readNbt();
            if (nbt != null) {
                properties.readFromNBT(nbt);
            }
            return properties;
        }
    };

    private static final Random RANDOM = new Random();

    public static <T> IEntitySerializer<T> of(EntityDataSerializer<T> serializer) {
        return AbstractEntityDataSerializer.wrap(serializer);
    }

    public static GameProfile readGameProfile(CompoundTag tag) {
        try {
            var name = tag.getOptionalString("Name").orElse(null);
            var id = tag.getOptionalUUID("Id").orElse(null);
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

    public static void dropContents(Level level, BlockPos blockPos, List<ItemStack> itemStacks) {
        itemStacks.forEach(itemStack -> dropItemStack(level, blockPos, itemStack));
    }

    public static void dropItemStack(Level level, BlockPos blockPos, ItemStack itemStack) {
        dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
    }

    public static void dropItemStack(Level level, double x, double y, double z, ItemStack itemStack) {
        double d0 = EntityType.ITEM.getWidth();
        double d1 = 1.0 - d0;
        double d2 = d0 / 2.0;
        double d3 = Math.floor(x) + RANDOM.nextDouble() * d1 + d2;
        double d4 = Math.floor(y) + RANDOM.nextDouble() * d1;
        double d5 = Math.floor(z) + RANDOM.nextDouble() * d1 + d2;

        while (!itemStack.isEmpty()) {
            ItemEntity itementity = new ItemEntity(level, d3, d4, d5, itemStack.split(RANDOM.nextInt(21) + 10));
            float f = 0.05F;
            itementity.setDeltaMovement(RANDOM.nextGaussian() * 0.05f, RANDOM.nextGaussian() * 0.05f + 0.2f, RANDOM.nextGaussian() * 0.05f);
            level.addFreshEntity(itementity);
        }
    }

    public static SkinPaintData decompressPaintData(ByteBuffer buffer) {
        var inputStream = new ByteArrayInputStream(buffer.array());
        try (var dataStream = new DataInputStream(new GZIPInputStream(inputStream))) {
            var paintData = SkinPaintData.v2(false);
            var length = dataStream.readInt();
            var colors = paintData.bytes();
            for (int i = 0; i < length; ++i) {
                if (i < colors.length) {
                    colors[i] = dataStream.readInt();
                }
            }
            return paintData;
        } catch (IOException exception) {
            return null;
        }
    }

    public static ByteBuffer compressPaintData(SkinPaintData paintData) {
        var outputStream = new ByteArrayOutputStream();
        try (var dataStream = new DataOutputStream(new GZIPOutputStream(outputStream))) {
            var colors = paintData.bytes();
            dataStream.writeInt(colors.length);
            for (int color : colors) {
                dataStream.writeInt(color);
            }
            dataStream.close();
            return ByteBuffer.wrap(outputStream.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    public static EntityTextureDescriptor.Model parseTextureModel(int index) {
        var values = EntityTextureDescriptor.Model.values();
        if (index < values.length) {
            return values[index];
        }
        return EntityTextureDescriptor.Model.STEVE;
    }
}
