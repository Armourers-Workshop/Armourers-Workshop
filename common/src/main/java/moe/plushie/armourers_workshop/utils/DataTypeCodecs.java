package moe.plushie.armourers_workshop.utils;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor.Options;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentAnimation;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentSettings;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.SkinPaintData;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DataTypeCodecs {

    public static final Codec<Boolean> BOOL = Codec.BOOL;

    public static final Codec<Byte> BYTE = Codec.BYTE;

    public static final Codec<Short> SHORT = Codec.SHORT;

    public static final Codec<Integer> INT = Codec.INT;

    public static final Codec<Long> LONG = Codec.LONG;

    public static final Codec<Float> FLOAT = Codec.FLOAT;

    public static final Codec<Double> DOUBLE = Codec.DOUBLE;

    public static final Codec<String> STRING = Codec.STRING;

    public static final Codec<UUID> UUID = INT.listOf().xmap(it -> {
        long l = (long) it[0] << 32 | (long) it[1] & 0xffffffffL;
        long m = (long) it[2] << 32 | (long) it[3] & 0xffffffffL;
        return new UUID(l, m);
    }, it -> {
        long l = it.getMostSignificantBits();
        long m = it.getLeastSignificantBits();
        return Lists.newArrayList((int) (l >> 32), (int) l, (int) (m >> 32), (int) m);
    });

    public static final Codec<Vector3f> VECTOR_3F = FLOAT.listOf().xmap(Vector3f::new, Vector3f::toList);
    public static final Codec<Vector3i> VECTOR_3I = INT.listOf().xmap(Vector3i::new, Vector3i::toList);

    public static final Codec<Rectangle3i> RECTANGLE_3I = INT.listOf().xmap(Rectangle3i::new, Rectangle3i::toList);
    public static final Codec<Rectangle3f> RECTANGLE_3F = FLOAT.listOf().xmap(Rectangle3f::new, Rectangle3f::toList);

    public static final Codec<CompoundTag> COMPOUND_TAG = CompoundTag.CODEC;

    public static final Codec<ItemStack> ITEM_STACK = ItemStack.CODEC;

    public static final Codec<BlockPos> BLOCK_POS = withAlternative(BlockPos.CODEC, LONG, BlockPos::of);

    public static final Codec<ISkinType> SKIN_TYPE = STRING.xmap(SkinTypes::byName, ISkinType::getName);

    public static final Codec<ISkinPartType> SKIN_PART_TYPE = STRING.xmap(SkinPartTypes::byName, ISkinPartType::getName);

    public static final Codec<SkinDocumentType> SKIN_DOCUMENT_TYPE = STRING.xmap(SkinDocumentTypes::byName, SkinDocumentType::getName);

    public static final Codec<SkinDocumentNode> SKIN_DOCUMENT_NODE = COMPOUND_TAG.xmap(SkinDocumentNode::new, SkinDocumentNode::serializeNBT);

    public static final Codec<SkinDocumentSettings> SKIN_DOCUMENT_SETTINGS = COMPOUND_TAG.xmap(SkinDocumentSettings::new, SkinDocumentSettings::serializeNBT);

    public static final Codec<SkinDocumentAnimation> SKIN_DOCUMENT_ANIMATION = COMPOUND_TAG.xmap(SkinDocumentAnimation::new, SkinDocumentAnimation::serializeNBT);

    public static final Codec<SkinDescriptor.Options> SKIN_OPTIONS = COMPOUND_TAG.xmap(SkinDescriptor.Options::new, SkinDescriptor.Options::serializeNBT);

    public static final Codec<SkinMarker> SKIN_MARKER = LONG.xmap(SkinMarker::of, SkinMarker::asLong);

    public static final Codec<SkinProperties> SKIN_PROPERTIES = COMPOUND_TAG.xmap(SkinProperties::new, SkinProperties::serializeNBT);

    public static final Codec<SkinDescriptor> SKIN_DESCRIPTOR = withAlternative(COMPOUND_TAG, STRING, SkinFileUtils::readNBT).xmap(SkinDescriptor::new, SkinDescriptor::serializeNBT);

    public static final Codec<SkinPaintData> SKIN_PAINT_DATA = Codec.BYTE_BUFFER.xmap(SkinPaintData::uncompress, SkinPaintData::compress);

    public static final Codec<PlayerTextureDescriptor> TEXTURE_DESCRIPTOR = COMPOUND_TAG.xmap(PlayerTextureDescriptor::new, PlayerTextureDescriptor::serializeNBT);

    public static final Codec<Holiday> HOLIDAY = STRING.xmap(ModHolidays::byName, Holiday::getName);

    public static final Codec<IPaintColor> PAINT_COLOR = withAlternative(INT, STRING, ColorUtils::parseColor).xmap(PaintColor::of, IPaintColor::getRawValue);

    public static final Codec<ColorScheme> COLOR_SCHEME = COMPOUND_TAG.xmap(ColorScheme::new, ColorScheme::serializeNBT);


    public static <T, U> Codec<T> withAlternative(final Codec<T> primary, final Codec<U> alternative, final Function<U, T> converter) {
        return Codec.either(primary, alternative).xmap(either -> either.map(v -> v, converter), Either::left);
    }
}
