package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractFireworkItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractFireworkItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("default").forGetter(AbstractFireworkItemTintSource::defaultColor)).apply(instance, AbstractFireworkItemTintSource::new));

    private final int defaultColor;

    public AbstractFireworkItemTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
//        FireworkExplosion fireworkExplosion = (FireworkExplosion) itemStack.get(DataComponents.FIREWORK_EXPLOSION);
//        IntList intList = fireworkExplosion != null ? fireworkExplosion.colors() : IntList.of();
//        int i = intList.size();
//        if (i == 0) {
//            return this.defaultColor;
//        } else if (i == 1) {
//            return ARGB.opaque(intList.getInt(0));
//        } else {
//            int j = 0;
//            int k = 0;
//            int l = 0;
//
//            for (int m = 0; m < i; ++m) {
//                int n = intList.getInt(m);
//                j += ARGB.red(n);
//                k += ARGB.green(n);
//                l += ARGB.blue(n);
//            }
//
//            return ARGB.color(j / i, k / i, l / i);
//        }
        return this.defaultColor;
    }

    @Override
    public IDataMapCodec<AbstractFireworkItemTintSource> type() {
        return MAP_CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}
