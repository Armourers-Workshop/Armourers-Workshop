package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CustomModelDataItemTintSource implements ItemTintSource {

    public static final IDataMapCodec<CustomModelDataItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("index").forGetter(CustomModelDataItemTintSource::index), IDataCodec.INT.fieldOf("default").forGetter(CustomModelDataItemTintSource::defaultColor)).apply(instance, CustomModelDataItemTintSource::new));

    private final int index;
    private final int defaultColor;

    public CustomModelDataItemTintSource(int index, int defaultColor) {
        this.index = index;
        this.defaultColor = defaultColor;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
//        CustomModelData customModelData = (CustomModelData)itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
//        if (customModelData != null) {
//            Integer integer = customModelData.getColor(this.index);
//            if (integer != null) {
//                return ARGB.opaque(integer);
//            }
//        }
        return this.defaultColor;
    }

    @Override
    public IDataMapCodec<CustomModelDataItemTintSource> type() {
        return MAP_CODEC;
    }

    public int index() {
        return this.index;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}
