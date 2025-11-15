package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractMapColorItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractMapColorItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("default").forGetter(AbstractMapColorItemTintSource::defaultColor)).apply(instance, AbstractMapColorItemTintSource::new));

    private final int defaultColor;

    public AbstractMapColorItemTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
//        MapItemColor mapItemColor = (MapItemColor)itemStack.get(DataComponents.MAP_COLOR);
//        return mapItemColor != null ? ARGB.opaque(mapItemColor.rgb()) : ARGB.opaque(this.defaultColor);
        return this.defaultColor;
    }

    @Override
    public IDataMapCodec<AbstractMapColorItemTintSource> type() {
        return MAP_CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}
