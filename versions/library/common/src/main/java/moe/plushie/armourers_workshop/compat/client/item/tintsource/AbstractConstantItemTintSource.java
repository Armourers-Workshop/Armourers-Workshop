package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractConstantItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractConstantItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("value").forGetter(AbstractConstantItemTintSource::value)).apply(instance, AbstractConstantItemTintSource::new));

    private final int value;

    public AbstractConstantItemTintSource(int i) {
        this.value = i | 0xff000000;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
        return value;
    }

    @Override
    public IDataMapCodec<AbstractConstantItemTintSource> type() {
        return MAP_CODEC;
    }

    public int value() {
        return value;
    }
}

