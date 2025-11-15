package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractDyeItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractDyeItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("defaultColor").forGetter(AbstractDyeItemTintSource::defaultColor)).apply(instance, AbstractDyeItemTintSource::new));

    private final int defaultColor;

    public AbstractDyeItemTintSource(int i) {
        this.defaultColor = i;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
        //return DyedItemColor.getOrDefault(itemStack, this.defaultColor);
        return defaultColor;
    }

    @Override
    public IDataMapCodec<AbstractDyeItemTintSource> type() {
        return MAP_CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

