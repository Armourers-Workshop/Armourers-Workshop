package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractPotionItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractPotionItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("default").forGetter(AbstractPotionItemTintSource::defaultColor)).apply(instance, AbstractPotionItemTintSource::new));

    private final int defaultColor;

    public AbstractPotionItemTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
//        PotionContents potionContents = (PotionContents)itemStack.get(DataComponents.POTION_CONTENTS);
//        return potionContents != null ? ARGB.opaque(potionContents.getColorOr(this.defaultColor)) : ARGB.opaque(this.defaultColor);
        return this.defaultColor;
    }

    @Override
    public IDataMapCodec<AbstractPotionItemTintSource> type() {
        return MAP_CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}
