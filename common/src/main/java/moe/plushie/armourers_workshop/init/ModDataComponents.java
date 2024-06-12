package moe.plushie.armourers_workshop.init;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.common.IDataComponentType;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class ModDataComponents {

    public static final IRegistryHolder<IDataComponentType<SkinDescriptor>> SKIN = create(DataTypeCodecs.SKIN_DESCRIPTOR).tag("ArmourersWorkshop").build("skin");

    public static final IRegistryHolder<IDataComponentType<Holiday>> HOLIDAY = create(DataTypeCodecs.HOLIDAY).tag("Holiday").build("holiday");

    public static final IRegistryHolder<IDataComponentType<BlockPos>> LINKED_POS = create(DataTypeCodecs.BLOCK_POS).tag("LinkedPos").build("linked_pos");

    public static final IRegistryHolder<IDataComponentType<CompoundTag>> ENTITY_DATA = create(DataTypeCodecs.COMPOUND_TAG).tag("EntityTag").build("entity_data");
    public static final IRegistryHolder<IDataComponentType<CompoundTag>> BLOCK_ENTITY_DATA = create(DataTypeCodecs.COMPOUND_TAG).tag("BlockEntityTag").build("block_entity_data");


    public static final IRegistryHolder<IDataComponentType<ItemStack>> GIFT = create(DataTypeCodecs.ITEM_STACK).tag("Gift").build("gift");

    public static final IRegistryHolder<IDataComponentType<Integer>> GIFT_COLOR_BG = create(DataTypeCodecs.INT).tag("Color1").build("color1");
    public static final IRegistryHolder<IDataComponentType<Integer>> GIFT_COLOR_FG = create(DataTypeCodecs.INT).tag("Color2").build("color2");

    public static final IRegistryHolder<IDataComponentType<IPaintColor>> TOOL_COLOR = create(DataTypeCodecs.PAINT_COLOR).tag("Color").build("color");

    public static final IRegistryHolder<IDataComponentType<Integer>> TOOL_FLAGS = create(DataTypeCodecs.INT).tag("Flags").build("tool_flags");
    public static final IRegistryHolder<IDataComponentType<CompoundTag>> TOOL_OPTIONS = create(DataTypeCodecs.COMPOUND_TAG).tag("Options").build("tool_options");


    public static void init() {
    }

    private static <T> IDataComponentTypeBuilder<T> create(Codec<T> codec) {
        return BuilderManager.getInstance().createDataComponentTypeBuilder(codec);
    }
}
