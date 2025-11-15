package moe.plushie.armourers_workshop.compat.client.item.tintsource;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AbstractTeamColorItemTintSource implements IItemTintSource {

    public static final IDataMapCodec<AbstractTeamColorItemTintSource> MAP_CODEC = IDataMapCodec.create((instance) -> instance.group(IDataCodec.INT.fieldOf("default").forGetter(AbstractTeamColorItemTintSource::defaultColor)).apply(instance, AbstractTeamColorItemTintSource::new));

    private final int defaultColor;

    public AbstractTeamColorItemTintSource(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    @Override
    public int calculate(ItemStack stack, @Nullable Level level, @Nullable LivingEntity entity) {
//        if (livingEntity != null) {
//            Team team = livingEntity.getTeam();
//            if (team != null) {
//                ChatFormatting chatFormatting = team.getColor();
//                if (chatFormatting.getColor() != null) {
//                    return ARGB.opaque(chatFormatting.getColor());
//                }
//            }
//        }
        return this.defaultColor;
    }

    @Override
    public IDataMapCodec<AbstractTeamColorItemTintSource> type() {
        return MAP_CODEC;
    }

    public int defaultColor() {
        return this.defaultColor;
    }
}
