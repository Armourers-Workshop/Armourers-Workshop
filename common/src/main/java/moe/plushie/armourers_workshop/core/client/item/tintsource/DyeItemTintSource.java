package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DyeItemTintSource implements ItemTintSource {

    public static final IDataMapCodec<DyeItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("defaultColor").forGetter(DyeItemTintSource::defaultColor)).apply(instance, DyeItemTintSource::new));

    private final int defaultColor;

    public DyeItemTintSource(int i) {
        this.defaultColor = i;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
        //return DyedItemColor.getOrDefault(itemStack, this.defaultColor);
        return defaultColor;
    }

    @Override
    public IDataMapCodec<DyeItemTintSource> type() {
        return MAP_CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}

