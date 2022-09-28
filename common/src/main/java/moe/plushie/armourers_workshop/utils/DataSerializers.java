package moe.plushie.armourers_workshop.utils;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataSerializers {

    public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = EntityDataSerializers.COMPOUND_TAG;
    public static final EntityDataSerializer<Integer> INT = EntityDataSerializers.INT;
    public static final EntityDataSerializer<String> STRING = EntityDataSerializers.STRING;
    public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializers.BOOLEAN;
    public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializers.FLOAT;

    public static final EntityDataSerializer<Vec3> VECTOR_3D = new EntityDataSerializer<Vec3>() {
        public void write(FriendlyByteBuf buffer, Vec3 pos) {
            buffer.writeDouble(pos.x());
            buffer.writeDouble(pos.y());
            buffer.writeDouble(pos.z());
        }

        public Vec3 read(FriendlyByteBuf buffer) {
            return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }

        public Vec3 copy(Vec3 pos) {
            return pos;
        }
    };

    public static final EntityDataSerializer<Vector3f> VECTOR_3F = new EntityDataSerializer<Vector3f>() {
        public void write(FriendlyByteBuf buffer, Vector3f pos) {
            buffer.writeFloat(pos.getX());
            buffer.writeFloat(pos.getY());
            buffer.writeFloat(pos.getZ());
        }

        public Vector3f read(FriendlyByteBuf buffer) {
            return new Vector3f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }

        public Vector3f copy(Vector3f pos) {
            return pos;
        }
    };

    public static final EntityDataSerializer<IPaintColor> PAINT_COLOR = new EntityDataSerializer<IPaintColor>() {
        public void write(FriendlyByteBuf buffer, IPaintColor color) {
            buffer.writeInt(color.getRawValue());
        }

        public IPaintColor read(FriendlyByteBuf buffer) {
            return PaintColor.of(buffer.readInt());
        }

        public IPaintColor copy(IPaintColor value) {
            return value;
        }
    };

    public static final EntityDataSerializer<PlayerTextureDescriptor> PLAYER_TEXTURE = new EntityDataSerializer<PlayerTextureDescriptor>() {
        public void write(FriendlyByteBuf buffer, PlayerTextureDescriptor descriptor) {
            buffer.writeNbt(descriptor.serializeNBT());
        }

        public PlayerTextureDescriptor read(FriendlyByteBuf buffer) {
            return new PlayerTextureDescriptor(buffer.readNbt());
        }

        public PlayerTextureDescriptor copy(PlayerTextureDescriptor descriptor) {
            return descriptor;
        }
    };

    public static final IPlayerDataSerializer<SkinWardrobe> ENTITY_WARDROBE = new IPlayerDataSerializer<SkinWardrobe>() {
        public void write(FriendlyByteBuf buffer, Player player, SkinWardrobe wardrobe) {
            buffer.writeInt(wardrobe.getId());
        }

        public SkinWardrobe read(FriendlyByteBuf buffer, Player player) {
            if (player == null || player.level == null) {
                return null;
            }
            int entityId = buffer.readInt();
            Entity entity = player.level.getEntity(entityId);
            if (entity == null) {
                for (Player player1 : player.level.players()) {
                    if (player1.getId() == entityId) {
                        entity = player1;
                        break;
                    }
                }
            }
            return SkinWardrobe.of(entity);
        }
    };

    public static final IPlayerDataSerializer<ContainerLevelAccess> WORLD_POS = new IPlayerDataSerializer<ContainerLevelAccess>() {
        public void write(FriendlyByteBuf buffer, Player player, ContainerLevelAccess callable) {
            Optional<BlockPos> pos1 = callable.evaluate((world, pos) -> pos);
            buffer.writeBlockPos(pos1.orElse(BlockPos.ZERO));
        }

        public ContainerLevelAccess read(FriendlyByteBuf buffer, Player player) {
            if (player == null || player.level == null) {
                return null;
            }
            return ContainerLevelAccess.create(player.level, buffer.readBlockPos());
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

    public static Vector3i getVector3i(CompoundTag nbt, String key) {
        ListTag listNBT = nbt.getList(key, Constants.TagFlags.INT);
        if (listNBT.size() >= 3) {
            return new Vector3i(listNBT.getInt(0), listNBT.getInt(1), listNBT.getInt(2));
        }
        return Vector3i.ZERO;
    }

    public static void putVector3i(CompoundTag nbt, String key, Vector3i vector) {
        int x = vector.getX(), y = vector.getY(), z = vector.getZ();
        if (x == 0 && y == 0 && z == 0) {
            return;
        }
        ListTag tags = new ListTag();
        tags.add(IntTag.valueOf(x));
        tags.add(IntTag.valueOf(y));
        tags.add(IntTag.valueOf(z));
        nbt.put(key, tags);
    }

    public static Vector3f getVector3f(CompoundTag nbt, String key) {
        ListTag listNBT = nbt.getList(key, Constants.TagFlags.FLOAT);
        if (listNBT.size() >= 3) {
            return new Vector3f(listNBT.getFloat(0), listNBT.getFloat(1), listNBT.getFloat(2));
        }
        return new Vector3f(0, 0, 0);
    }

    public static void putVector3f(CompoundTag nbt, String key, Vector3f vector) {
        float x = vector.getX(), y = vector.getY(), z = vector.getZ();
        if (x == 0 && y == 0 && z == 0) {
            return;
        }
        ListTag tags = new ListTag();
        tags.add(FloatTag.valueOf(x));
        tags.add(FloatTag.valueOf(y));
        tags.add(FloatTag.valueOf(z));
        nbt.put(key, tags);
    }

    public static Rectangle3i getRectangle3i(CompoundTag nbt, String key, Rectangle3i defaultValue) {
        ListTag listNBT = nbt.getList(key, Constants.TagFlags.INT);
        if (listNBT.size() >= 6) {
            return new Rectangle3i(listNBT.getInt(0), listNBT.getInt(1), listNBT.getInt(2), listNBT.getInt(3), listNBT.getInt(4), listNBT.getInt(5));
        }
        return defaultValue;
    }

    public static void putRectangle3i(CompoundTag nbt, String key, Rectangle3i value, Rectangle3i defaultValue) {
        if (value.equals(defaultValue)) {
            return;
        }
        ListTag tags = new ListTag();
        tags.add(IntTag.valueOf(value.getX()));
        tags.add(IntTag.valueOf(value.getY()));
        tags.add(IntTag.valueOf(value.getZ()));
        tags.add(IntTag.valueOf(value.getWidth()));
        tags.add(IntTag.valueOf(value.getHeight()));
        tags.add(IntTag.valueOf(value.getDepth()));
        nbt.put(key, tags);
    }

    public static boolean getBoolean(CompoundTag nbt, String key, boolean defaultValue) {
        if (nbt.contains(key, Constants.TagFlags.BYTE)) {
            return nbt.getBoolean(key);
        }
        return defaultValue;
    }

    public static void putBoolean(CompoundTag nbt, String key, boolean value, boolean defaultValue) {
        if (defaultValue != value) {
            nbt.putBoolean(key, value);
        }
    }

    public static int getInt(CompoundTag nbt, String key, int defaultValue) {
        if (nbt != null && nbt.contains(key, Constants.TagFlags.INT)) {
            return nbt.getInt(key);
        }
        return defaultValue;
    }

    public static void putInt(CompoundTag nbt, String key, int value, int defaultValue) {
        if (defaultValue != value) {
            nbt.putInt(key, value);
        }
    }

    @Nullable
    public static SkinPaintData getPaintData(CompoundTag nbt, String key) {
        if (nbt != null && nbt.contains(key, Constants.TagFlags.BYTE_ARRAY)) {
            try {
                ByteBuf buffer = Unpooled.wrappedBuffer(nbt.getByteArray(key));
                ByteBufInputStream bufferedStream = new ByteBufInputStream(buffer);
                GZIPInputStream compressedStream = new GZIPInputStream(bufferedStream);
                DataInputStream dataStream = new DataInputStream(compressedStream);
                SkinPaintData paintData = SkinPaintData.v2();
                int length = dataStream.readInt();
                int[] colors = paintData.getData();
                for (int i = 0; i < length; ++i) {
                    if (i < colors.length) {
                        colors[i] = dataStream.readInt();
                    }
                }
                StreamUtils.closeQuietly(dataStream, compressedStream, bufferedStream);
                return paintData;
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    public static void putPaintData(CompoundTag nbt, String key, SkinPaintData paintData) {
        if (paintData != null) {
            try {
                int[] colors = paintData.getData();
                ByteBuf buffer = Unpooled.buffer();
                ByteBufOutputStream bufferedStream = new ByteBufOutputStream(buffer);
                GZIPOutputStream compressedStream = new GZIPOutputStream(bufferedStream);
                DataOutputStream dataStream = new DataOutputStream(compressedStream);
                dataStream.writeInt(colors.length);
                for (int color : colors) {
                    dataStream.writeInt(color);
                }
                StreamUtils.closeQuietly(dataStream, compressedStream, bufferedStream);
                nbt.putByteArray(key, Arrays.copyOf(buffer.array(), buffer.writerIndex()));
            } catch (Exception ignored) {
            }
        }
    }

    public static float getFloat(CompoundTag nbt, String key, float defaultValue) {
        if (nbt.contains(key, Constants.TagFlags.FLOAT)) {
            return nbt.getFloat(key);
        }
        return defaultValue;
    }

    public static void putFloat(CompoundTag nbt, String key, float value, float defaultValue) {
        if (defaultValue != value) {
            nbt.putFloat(key, value);
        }
    }

    public static String getString(CompoundTag nbt, String key, String defaultValue) {
        if (nbt != null && nbt.contains(key, Constants.TagFlags.STRING)) {
            return nbt.getString(key);
        }
        return defaultValue;
    }

    public static void putString(CompoundTag nbt, String key, String value, String defaultValue) {
        if (defaultValue != value) {
            nbt.putString(key, value);
        }
    }

    public static void putRotations(CompoundTag nbt, String key, Rotations value, Rotations defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.put(key, value.save());
        }
    }

    public static Rotations getRotations(CompoundTag nbt, String key, Rotations defaultValue) {
        ListTag listNBT = nbt.getList(key, Constants.TagFlags.FLOAT);
        if (listNBT.size() >= 3) {
            return new Rotations(listNBT);
        }
        return defaultValue;
    }

    public static void putSkinProperties(CompoundTag nbt, String key, SkinProperties properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        CompoundTag propertiesTag = new CompoundTag();
        properties.writeToNBT(propertiesTag);
        nbt.put(key, propertiesTag);
    }

    public static SkinProperties getSkinProperties(CompoundTag nbt, String key) {
        SkinProperties properties = new SkinProperties();
        if (nbt.contains(key, Constants.TagFlags.COMPOUND)) {
            properties.readFromNBT(nbt.getCompound(key));
        }
        return properties;
    }

    public static void putPaintColor(CompoundTag nbt, String key, IPaintColor value, IPaintColor defaultValue) {
        if (value != null && !value.equals(defaultValue)) {
            nbt.putInt(key, value.getRawValue());
        } else {
            nbt.remove(key);
        }
    }

    public static IPaintColor getPaintColor(CompoundTag nbt, String key, IPaintColor defaultValue) {
        if (nbt != null && nbt.contains(key, Constants.TagFlags.INT)) {
            return PaintColor.of(nbt.getInt(key));
        }
        return defaultValue;
    }

    public static void putBlockPaintColor(CompoundTag nbt, String key, BlockPaintColor value, BlockPaintColor defaultValue) {
        if (value != null && !value.equals(defaultValue)) {
            nbt.put(key, value.serializeNBT());
        } else {
            nbt.remove(key);
        }
    }

    public static BlockPaintColor getBlockPaintColor(CompoundTag nbt, String key, BlockPaintColor defaultValue) {
        if (!nbt.contains(key, Constants.TagFlags.COMPOUND)) {
            return defaultValue;
        }
        CompoundTag colorNBT = nbt.getCompound(key);
        if (colorNBT.isEmpty()) {
            return defaultValue;
        }
        BlockPaintColor color = new BlockPaintColor();
        color.deserializeNBT(colorNBT);
        return color;
    }

    public static ColorScheme getColorScheme(CompoundTag nbt, String key, ColorScheme defaultValue) {
        if (nbt.contains(key, Constants.TagFlags.COMPOUND)) {
            return new ColorScheme(nbt.getCompound(key));
        }
        return defaultValue;
    }

    public static void putColorScheme(CompoundTag nbt, String key, ColorScheme value, ColorScheme defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.put(key, value.serializeNBT());
        }
    }

    public static void putSkinDescriptor(CompoundTag nbt, String key, SkinDescriptor value, SkinDescriptor defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.put(key, value.serializeNBT());
        }
    }

    public static SkinDescriptor getSkinDescriptor(CompoundTag nbt, String key, SkinDescriptor defaultValue) {
        if (nbt.contains(key, Constants.TagFlags.COMPOUND)) {
            CompoundTag nbt1 = nbt.getCompound(key);
            if (!nbt1.isEmpty()) {
                return new SkinDescriptor(nbt1);
            }
        }
        return defaultValue;
    }

    public static void putTextureDescriptor(CompoundTag nbt, String key, PlayerTextureDescriptor value, PlayerTextureDescriptor defaultValue) {
        if (!value.equals(defaultValue)) {
            nbt.put(key, value.serializeNBT());
        }
    }

    public static PlayerTextureDescriptor getTextureDescriptor(CompoundTag nbt, String key, PlayerTextureDescriptor defaultValue) {
        if (nbt.contains(key, Constants.TagFlags.COMPOUND)) {
            CompoundTag nbt1 = nbt.getCompound(key);
            if (!nbt1.isEmpty()) {
                return new PlayerTextureDescriptor(nbt1);
            }
        }
        return defaultValue;
    }

    public static void putBlockPos(CompoundTag nbt, String key, BlockPos value, BlockPos defaultValue) {
        if (!Objects.equals(value, defaultValue)) {
            nbt.putLong(key, value.asLong());
        } else {
            nbt.remove(key);
        }
    }

    public static BlockPos getBlockPos(CompoundTag nbt, String key, BlockPos defaultValue) {
        if (nbt.contains(key, Constants.TagFlags.LONG)) {
            return BlockPos.of(nbt.getLong(key));
        }
        return defaultValue;
    }

    public static void putBlockPosList(CompoundTag nbt, String key, Collection<BlockPos> elements) {
        if (elements.isEmpty()) {
            return;
        }
        ArrayList<Long> list = new ArrayList<>(elements.size());
        for (BlockPos pos : elements) {
            list.add(pos.asLong());
        }
        nbt.putLongArray(key, list);
    }

    public static Collection<BlockPos> getBlockPosList(CompoundTag nbt, String key) {
        ArrayList<BlockPos> elements = new ArrayList<>();
        if (nbt.contains(key, Constants.TagFlags.LONG_ARRAY)) {
            for (long value : nbt.getLongArray(key)) {
                elements.add(BlockPos.of(value));
            }
        }
        return elements;
    }

    public static void putMarkerList(CompoundTag nbt, String key, Collection<SkinMarker> elements) {
        if (elements.isEmpty()) {
            return;
        }
        ArrayList<Long> list = new ArrayList<>(elements.size());
        for (SkinMarker marker : elements) {
            list.add(marker.asLong());
        }
        nbt.putLongArray(key, list);
    }

    public static Collection<SkinMarker> getMarkerList(CompoundTag nbt, String key) {
        ArrayList<SkinMarker> elements = new ArrayList<>();
        if (nbt.contains(key, Constants.TagFlags.LONG_ARRAY)) {
            for (long value : nbt.getLongArray(key)) {
                elements.add(SkinMarker.of(value));
            }
        }
        return elements;
    }

    public static CompoundTag saveBlockTag(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return null;
        }
        //#if MC >= 11800
        return blockEntity.saveWithFullMetadata();
        //#else
        //# return blockEntity.save(new CompoundTag());
        //#endif
    }

    public static void loadBlockTag(BlockEntity blockEntity, CompoundTag tag) {
        if (blockEntity == null) {
            return;
        }
        //#if MC >= 11800
        blockEntity.load(tag);
        //#else
        //# blockEntity.load(blockEntity.getBlockState(), tag);
        //#endif
    }

    @Nullable
    public static GameProfile readGameProfile(CompoundTag compoundTag) {
        String name = null;
        UUID id = null;
        if (compoundTag.contains("Name", 8)) {
            name = compoundTag.getString("Name");
        }
        if (compoundTag.hasUUID("Id")) {
            id = compoundTag.getUUID("Id");
        }
        try {
            return new GameProfile(id, name);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static CompoundTag writeGameProfile(CompoundTag compoundTag, GameProfile gameProfile) {
        if (Strings.isNotBlank(gameProfile.getName())) {
            compoundTag.putString("Name", gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            compoundTag.putUUID("Id", gameProfile.getId());
        }
        return compoundTag;
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
