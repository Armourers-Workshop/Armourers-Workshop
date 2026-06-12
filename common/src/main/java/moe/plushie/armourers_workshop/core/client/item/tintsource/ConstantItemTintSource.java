package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ConstantItemTintSource implements ItemTintSource {

    public static final IDataMapCodec<ConstantItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("value").forGetter(ConstantItemTintSource::value)).apply(instance, ConstantItemTintSource::new));

    private final int value;

    public ConstantItemTintSource(int i) {
        this.value = i | 0xff000000;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
        return value;
    }

    @Override
    public IDataMapCodec<ConstantItemTintSource> type() {
        return MAP_CODEC;
    }

    public int value() {
        return value;
    }
}

