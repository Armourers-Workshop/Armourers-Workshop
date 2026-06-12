package moe.plushie.armourers_workshop.core.item.tintsource;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DefaultItemTintSource implements ItemTintSource {

    public static final IDataMapCodec<DefaultItemTintSource> MAP_CODEC = IDataMapCodec.create(it -> it.group(IDataCodec.INT.fieldOf("index").forGetter(DefaultItemTintSource::layerIndex)).apply(it, DefaultItemTintSource::new));

    private final int layerIndex;

    public DefaultItemTintSource(int layerIndex) {
        this.layerIndex = layerIndex;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
        // AbstractItem or AbstractBlockItem
        if (stack.getItem() instanceof AbstractItemHandler handler) {
            return handler.getModelTintColor(stack, level, entity, layerIndex);
        }
        return 0xffffffff;
    }

    public int layerIndex() {
        return layerIndex;
    }

    @Override
    public IDataMapCodec<? extends DefaultItemTintSource> type() {
        return MAP_CODEC;
    }
}
