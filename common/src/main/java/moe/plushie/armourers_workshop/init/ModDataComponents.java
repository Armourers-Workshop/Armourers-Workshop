package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataComponentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IDataComponentTypeBuilder;
import moe.plushie.armourers_workshop.core.data.TypedEntityData;
import moe.plushie.armourers_workshop.core.holiday.Holiday;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class ModDataComponents {

    public static final IRegistryHolder<IDataComponentType<SkinDescriptor>> SKIN = normal(SkinDescriptor.CODEC).tag("ArmourersWorkshop").build("skin");

    public static final IRegistryHolder<IDataComponentType<Holiday>> HOLIDAY = normal(Holiday.CODEC).tag("Holiday").build("holiday");

    public static final IRegistryHolder<IDataComponentType<GlobalPos>> LINKED_POS = normal(ExtraCodecs.GLOBAL_POS).tag("LinkedPos").build("linked_pos");

    public static final IRegistryHolder<IDataComponentType<TypedEntityData<IEntityType<?>>>> ENTITY_DATA = normal(TypedEntityData.codec(Registries.ENTITY_TYPES)).tag("EntityTag").build("entity_data");
    public static final IRegistryHolder<IDataComponentType<TypedEntityData<IBlockEntityType<?>>>> BLOCK_ENTITY_DATA = normal(TypedEntityData.codec(Registries.BLOCK_ENTITY_TYPES)).tag("BlockEntityTag").build("block_entity_data");

    public static final IRegistryHolder<IDataComponentType<ItemStack>> GIFT = normal(ExtraCodecs.ITEM_STACK).tag("Gift").build("gift");

    public static final IRegistryHolder<IDataComponentType<Integer>> GIFT_COLOR_BG = normal(IDataCodec.INT).tag("Color1").build("color1");
    public static final IRegistryHolder<IDataComponentType<Integer>> GIFT_COLOR_FG = normal(IDataCodec.INT).tag("Color2").build("color2");

    public static final IRegistryHolder<IDataComponentType<SkinPaintColor>> TOOL_COLOR = normal(SkinPaintColor.CODEC).tag("Color").build("color");

    public static final IRegistryHolder<IDataComponentType<Integer>> TOOL_FLAGS = normal(IDataCodec.INT).tag("Flags").build("tool_flags");
    public static final IRegistryHolder<IDataComponentType<CompoundTag>> TOOL_OPTIONS = normal(ExtraCodecs.COMPOUND_TAG).tag("Options").build("tool_options");

    private static <T> IDataComponentTypeBuilder<T> normal(IDataCodec<T> codec) {
        return BuilderManager.getInstance().createDataComponentTypeBuilder(codec);
    }

    public static void init() {
    }
}
