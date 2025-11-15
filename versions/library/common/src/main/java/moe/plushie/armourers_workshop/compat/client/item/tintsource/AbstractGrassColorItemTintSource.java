package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractGrassColorItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractGrassColorItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.FLOAT.fieldOf("temperature").forGetter(AbstractGrassColorItemTintSource::temperature), IDataCodec.FLOAT.fieldOf("downfall").forGetter(AbstractGrassColorItemTintSource::downfall)).apply(instance, AbstractGrassColorItemTintSource::new));

    private final float temperature;
    private final float downfall;

    public AbstractGrassColorItemTintSource(float temperature, float downfall) {
        this.temperature = temperature;
        this.downfall = downfall;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
        //return GrassColor.get(this.temperature, this.downfall);
        return 0;
    }

    @Override
    public IDataMapCodec<? extends IItemTintSource> type() {
        return MAP_CODEC;
    }

    public float temperature() {
        return this.temperature;
    }

    public float downfall() {
        return this.downfall;
    }
}
