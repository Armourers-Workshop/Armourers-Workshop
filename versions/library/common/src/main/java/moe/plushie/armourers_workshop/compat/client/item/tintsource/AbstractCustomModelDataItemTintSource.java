package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractCustomModelDataItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractCustomModelDataItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("index").forGetter(AbstractCustomModelDataItemTintSource::index), IDataCodec.INT.fieldOf("default").forGetter(AbstractCustomModelDataItemTintSource::defaultColor)).apply(instance, AbstractCustomModelDataItemTintSource::new));

    private final int index;
    private final int defaultColor;

    public AbstractCustomModelDataItemTintSource(int index, int defaultColor) {
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
    public IDataMapCodec<AbstractCustomModelDataItemTintSource> type() {
        return MAP_CODEC;
    }

    public int index() {
        return this.index;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}
