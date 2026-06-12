package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GrassColorItemTintSource implements ItemTintSource {

    public static final IDataMapCodec<GrassColorItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.FLOAT.fieldOf("temperature").forGetter(GrassColorItemTintSource::temperature), IDataCodec.FLOAT.fieldOf("downfall").forGetter(GrassColorItemTintSource::downfall)).apply(instance, GrassColorItemTintSource::new));

    private final float temperature;
    private final float downfall;

    public GrassColorItemTintSource(float temperature, float downfall) {
        this.temperature = temperature;
        this.downfall = downfall;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
        //return GrassColor.get(this.temperature, this.downfall);
        return 0;
    }

    @Override
    public IDataMapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }

    public float temperature() {
        return this.temperature;
    }

    public float downfall() {
        return this.downfall;
    }
}
