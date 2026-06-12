package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MapColorItemTintSource implements ItemTintSource {

    public static final IDataMapCodec<MapColorItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("default").forGetter(MapColorItemTintSource::defaultColor)).apply(instance, MapColorItemTintSource::new));

    private final int defaultColor;

    public MapColorItemTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
//        MapItemColor mapItemColor = (MapItemColor)itemStack.get(DataComponents.MAP_COLOR);
//        return mapItemColor != null ? ARGB.opaque(mapItemColor.rgb()) : ARGB.opaque(this.defaultColor);
        return this.defaultColor;
    }

    @Override
    public IDataMapCodec<MapColorItemTintSource> type() {
        return MAP_CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}
