package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.nbt.CompoundTag;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.registry.IRegistryEntry;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinOptions;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public class OptionalAPI {

    public static boolean getOptionalBoolean(@This CompoundTag tag, String key, boolean defaultValue) {
        if (tag.contains(key, Constants.TagFlags.BYTE)) {
            return tag.getBoolean(key);
        }
        return defaultValue;
    }

    public static void putOptionalBoolean(@This CompoundTag tag, String key, boolean value, boolean defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putBoolean(key, value);
        }
    }

    public static int getOptionalInt(@This CompoundTag tag, String key, int defaultValue) {
        if (tag.contains(key, Constants.TagFlags.INT)) {
            return tag.getInt(key);
        }
        return defaultValue;
    }

    public static void putOptionalInt(@This CompoundTag tag, String key, int value, int defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putInt(key, value);
        }
    }

    public static float getOptionalFloat(@This CompoundTag tag, String key, float defaultValue) {
        if (tag.contains(key, Constants.TagFlags.FLOAT)) {
            return tag.getFloat(key);
        }
        return defaultValue;
    }

    public static void putOptionalFloat(@This CompoundTag tag, String key, float value, float defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putFloat(key, value);
        }
    }

    public static String getOptionalString(@This CompoundTag tag, String key, String defaultValue) {
        if (tag.contains(key, Constants.TagFlags.STRING)) {
            return tag.getString(key);
        }
        return defaultValue;
    }

    public static void putOptionalString(@This CompoundTag tag, String key, String value, String defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putString(key, value);
        }
    }

    public static <T extends IRegistryEntry> T getOptionalType(@This CompoundTag tag, String key, T defaultValue, Function<String, T> provider) {
        if (tag.contains(key, Constants.TagFlags.STRING)) {
            return provider.apply(tag.getString(key));
        }
        return defaultValue;
    }

    public static <T extends IRegistryEntry> void putOptionalType(@This CompoundTag tag, String key, T value, T defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putString(key, value.getRegistryName().toString());
        }
    }

    public static Vector3f getOptionalVector3f(@This CompoundTag tag, String key, Vector3f defaultValue) {
        ListTag listNBT = tag.getList(key, Constants.TagFlags.FLOAT);
        if (listNBT.size() >= 3) {
            return new Vector3f(listNBT.getFloat(0), listNBT.getFloat(1), listNBT.getFloat(2));
        }
        return defaultValue;
    }

    public static void putOptionalVector3f(@This CompoundTag tag, String key, Vector3f value, Vector3f defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            ListTag tags = new ListTag();
            tags.add(FloatTag.valueOf(value.getX()));
            tags.add(FloatTag.valueOf(value.getY()));
            tags.add(FloatTag.valueOf(value.getZ()));
            tag.put(key, tags);
        }
    }

    public static Vector3i getOptionalVector3i(@This CompoundTag tag, String key, Vector3i defaultValue) {
        ListTag listNBT = tag.getList(key, Constants.TagFlags.INT);
        if (listNBT.size() >= 3) {
            return new Vector3i(listNBT.getInt(0), listNBT.getInt(1), listNBT.getInt(2));
        }
        return defaultValue;
    }

    public static void putOptionalVector3i(@This CompoundTag tag, String key, Vector3i value, Vector3i defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            ListTag tags = new ListTag();
            tags.add(IntTag.valueOf(value.getX()));
            tags.add(IntTag.valueOf(value.getY()));
            tags.add(IntTag.valueOf(value.getZ()));
            tag.put(key, tags);
        }
    }

    public static Rectangle3i getOptionalRectangle3i(@This CompoundTag tag, String key, Rectangle3i defaultValue) {
        ListTag listTag = tag.getList(key, Constants.TagFlags.INT);
        if (listTag.size() >= 6) {
            return new Rectangle3i(listTag.getInt(0), listTag.getInt(1), listTag.getInt(2), listTag.getInt(3), listTag.getInt(4), listTag.getInt(5));
        }
        return defaultValue;
    }

    public static void putOptionalRectangle3i(@This CompoundTag tag, String key, Rectangle3i value, Rectangle3i defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            ListTag tags = new ListTag();
            tags.add(IntTag.valueOf(value.getX()));
            tags.add(IntTag.valueOf(value.getY()));
            tags.add(IntTag.valueOf(value.getZ()));
            tags.add(IntTag.valueOf(value.getWidth()));
            tags.add(IntTag.valueOf(value.getHeight()));
            tags.add(IntTag.valueOf(value.getDepth()));
            tag.put(key, tags);
        }
    }

    public static BlockPos getOptionalBlockPos(@This CompoundTag tag, String key, BlockPos defaultValue) {
        if (tag.contains(key, Constants.TagFlags.LONG)) {
            return BlockPos.of(tag.getLong(key));
        }
        return defaultValue;
    }

    public static void putOptionalBlockPos(@This CompoundTag tag, String key, BlockPos value, BlockPos defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putLong(key, value.asLong());
        }
    }

    public static Collection<BlockPos> getOptionalBlockPosArray(@This CompoundTag tag, String key) {
        ArrayList<BlockPos> elements = new ArrayList<>();
        if (tag.contains(key, Constants.TagFlags.LONG_ARRAY)) {
            for (long value : tag.getLongArray(key)) {
                elements.add(BlockPos.of(value));
            }
        }
        return elements;
    }

    public static void putOptionalBlockPosArray(@This CompoundTag tag, String key, Collection<BlockPos> elements) {
        if (_shouldPutValueArray(tag, key, elements)) {
            ArrayList<Long> list = new ArrayList<>(elements.size());
            for (BlockPos pos : elements) {
                list.add(pos.asLong());
            }
            tag.putLongArray(key, list);
        }
    }

    public static Rotations getOptionalRotations(@This CompoundTag tag, String key, Rotations defaultValue) {
        ListTag listTag = tag.getList(key, Constants.TagFlags.FLOAT);
        if (listTag.size() >= 3) {
            return new Rotations(listTag);
        }
        return defaultValue;
    }

    public static void putOptionalRotations(@This CompoundTag tag, String key, Rotations value, Rotations defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.save());
        }
    }

    public static GameProfile getOptionalGameProfile(@This CompoundTag tag, String key, GameProfile defaultValue) {
        CompoundTag profileTag = tag.getCompound(key);
        if (profileTag.isEmpty()) {
            return defaultValue;
        }
        GameProfile value = DataSerializers.readGameProfile(profileTag);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static void putOptionalGameProfile(@This CompoundTag tag, String key, GameProfile value, GameProfile defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            CompoundTag profileTag = new CompoundTag();
            DataSerializers.writeGameProfile(profileTag, value);
            if (!profileTag.isEmpty()) {
                tag.put(key, profileTag);
            }
        }
    }

    public static PlayerTextureDescriptor getOptionalTextureDescriptor(@This CompoundTag tag, String key, PlayerTextureDescriptor defaultValue) {
        if (tag.contains(key, Constants.TagFlags.COMPOUND)) {
            CompoundTag nbt1 = tag.getCompound(key);
            if (!nbt1.isEmpty()) {
                return new PlayerTextureDescriptor(nbt1);
            }
        }
        return defaultValue;
    }

    public static void putOptionalTextureDescriptor(@This CompoundTag tag, String key, PlayerTextureDescriptor value, PlayerTextureDescriptor defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.serializeNBT());
        }
    }

    @Nullable
    public static SkinPaintData getOptionalPaintData(@This CompoundTag tag, String key) {
        if (tag != null && tag.contains(key, Constants.TagFlags.BYTE_ARRAY)) {
            try {
                ByteBuf buffer = Unpooled.wrappedBuffer(tag.getByteArray(key));
                ByteBufInputStream bufferedStream = new ByteBufInputStream(buffer);
                java.util.zip.GZIPInputStream compressedStream = new GZIPInputStream(bufferedStream);
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

    public static void putOptionalPaintData(@This CompoundTag tag, String key, SkinPaintData paintData) {
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
                tag.putByteArray(key, Arrays.copyOf(buffer.array(), buffer.writerIndex()));
            } catch (Exception ignored) {
            }
        }
    }

    public static ColorScheme getOptionalColorScheme(@This CompoundTag tag, String key, ColorScheme defaultValue) {
        if (tag.contains(key, Constants.TagFlags.COMPOUND)) {
            return new ColorScheme(tag.getCompound(key));
        }
        return defaultValue;
    }

    public static void putOptionalColorScheme(@This CompoundTag tag, String key, ColorScheme value, ColorScheme defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.serializeNBT());
        }
    }

    public static IPaintColor getOptionalPaintColor(@This CompoundTag tag, String key, IPaintColor defaultValue) {
        if (tag != null && tag.contains(key, Constants.TagFlags.INT)) {
            return PaintColor.of(tag.getInt(key));
        }
        return defaultValue;
    }

    public static void putOptionalPaintColor(@This CompoundTag tag, String key, IPaintColor value, IPaintColor defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.putInt(key, value.getRawValue());
        }
    }

    public static SkinItemTransforms getOptionalItemTransforms(@This CompoundTag tag, String key, SkinItemTransforms defaultValue) {
        if (tag.contains(key, Constants.TagFlags.COMPOUND)) {
            return new SkinItemTransforms(tag.getCompound(key));
        }
        return defaultValue;
    }

    public static void putOptionalItemTransforms(@This CompoundTag tag, String key, SkinItemTransforms value, SkinItemTransforms defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.serializeNBT());
        }
    }

    public static BlockPaintColor getOptionalBlockPaintColor(@This CompoundTag tag, String key, BlockPaintColor defaultValue) {
        if (!tag.contains(key, Constants.TagFlags.COMPOUND)) {
            return defaultValue;
        }
        CompoundTag colorNBT = tag.getCompound(key);
        if (colorNBT.isEmpty()) {
            return defaultValue;
        }
        BlockPaintColor color = new BlockPaintColor();
        color.deserializeNBT(colorNBT);
        return color;
    }

    public static void putOptionalBlockPaintColor(@This CompoundTag tag, String key, BlockPaintColor value, BlockPaintColor defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.serializeNBT());
        }
    }

    public static SkinDescriptor getOptionalSkinDescriptor(@This CompoundTag tag, String key) {
        return getOptionalSkinDescriptor(tag, key, SkinDescriptor.EMPTY);
    }

    public static void putOptionalSkinDescriptor(@This CompoundTag tag, String key, SkinDescriptor value) {
        putOptionalSkinDescriptor(tag, key, value, SkinDescriptor.EMPTY);
    }

    public static SkinDescriptor getOptionalSkinDescriptor(@This CompoundTag tag, String key, SkinDescriptor defaultValue) {
        CompoundTag parsedTag = _parseCompoundTag(tag, key);
        if (parsedTag != null) {
            if (!parsedTag.isEmpty()) {
                return new SkinDescriptor(parsedTag);
            }
            return SkinDescriptor.EMPTY;
        }
        return defaultValue;
    }

    public static void putOptionalSkinDescriptor(@This CompoundTag tag, String key, SkinDescriptor value, SkinDescriptor defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.serializeNBT());
        }
    }

    public static SkinProperties getOptionalSkinProperties(@This CompoundTag tag, String key) {
        SkinProperties properties = new SkinProperties();
        if (tag.contains(key, Constants.TagFlags.COMPOUND)) {
            properties.readFromNBT(tag.getCompound(key));
        }
        return properties;
    }

    public static void putOptionalSkinProperties(@This CompoundTag tag, String key, SkinProperties properties) {
        if (_shouldPutValue(tag, key, properties, SkinProperties.EMPTY)) {
            CompoundTag propertiesTag = new CompoundTag();
            properties.writeToNBT(propertiesTag);
            tag.put(key, propertiesTag);
        }
    }

    public static SkinOptions getOptionalSkinOptions(@This CompoundTag tag, String key, SkinOptions defaultValue) {
        if (tag.contains(key, Constants.TagFlags.COMPOUND)) {
            CompoundTag nbt1 = tag.getCompound(key);
            if (!nbt1.isEmpty()) {
                return new SkinOptions(nbt1);
            }
        }
        return defaultValue;
    }

    public static void putOptionalSkinOptions(@This CompoundTag tag, String key, SkinOptions value, SkinOptions defaultValue) {
        if (_shouldPutValue(tag, key, value, defaultValue)) {
            tag.put(key, value.serializeNBT());
        }
    }

    public static Collection<SkinMarker> getOptionalSkinMarkerArray(@This CompoundTag tag, String key) {
        ArrayList<SkinMarker> elements = new ArrayList<>();
        if (tag.contains(key, Constants.TagFlags.LONG_ARRAY)) {
            for (long value : tag.getLongArray(key)) {
                elements.add(SkinMarker.of(value));
            }
        }
        return elements;
    }

    public static void putOptionalSkinMarkerArray(@This CompoundTag tag, String key, Collection<SkinMarker> elements) {
        if (_shouldPutValueArray(tag, key, elements)) {
            ArrayList<Long> list = new ArrayList<>(elements.size());
            for (SkinMarker marker : elements) {
                list.add(marker.asLong());
            }
            tag.putLongArray(key, list);
        }
    }

    private static <T> boolean _shouldPutValue(CompoundTag tag, String key, T value, T defaultValue) {
        if (tag == null || key == null) {
            return false;
        }
        if (value == null || value.equals(defaultValue)) {
            tag.remove(key);
            return false;
        }
        return true;
    }

    private static <T> boolean _shouldPutValueArray(CompoundTag tag, String key, Collection<T> value) {
        if (tag == null || key == null) {
            return false;
        }
        if (value == null || value.isEmpty()) {
            tag.remove(key);
            return false;
        }
        return true;
    }

    private static CompoundTag _parseCompoundTag(CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.COMPOUND)) {
            return tag.getCompound(key);
        }
        if (tag.contains(key, Constants.TagFlags.STRING)) {
            return SkinFileUtils.readNBT(tag.getString(key));
        }
        return null;
    }
}
