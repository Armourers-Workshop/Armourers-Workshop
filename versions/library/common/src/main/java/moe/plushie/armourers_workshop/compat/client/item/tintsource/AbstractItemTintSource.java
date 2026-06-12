package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AbstractItemTintSource extends AbstractItemTintSourceImpl {

    private final ItemTintSource[] sources;

    public AbstractItemTintSource(Collection<? extends ItemTintSource> sources) {
        this.sources = sources.toArray(new ItemTintSource[0]);
    }

    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity, int index) {
        if (index < sources.length) {
            var source = sources[index];
            if (source != null) {
                return source.calculate(stack, level, entity);
            }
        }
        return -1;
    }
}
