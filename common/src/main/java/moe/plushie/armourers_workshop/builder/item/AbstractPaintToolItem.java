package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.core.item.ConfigurableToolItem;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPaintToolItem extends ConfigurableToolItem implements IItemSoundProvider, IItemParticleProvider {

    public AbstractPaintToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void playSound(UseOnContext context) {
        IRegistryHolder<SoundEvent> soundEvent = getItemSoundEvent(context);
        if (soundEvent == null) {
            return;
        }
        if (ModHolidays.APRIL_FOOLS.isHolidayActive()) {
            soundEvent = ModSounds.BOI;
        }
        float pitch = getItemSoundPitch(context);
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (level.isClientSide()) {
            level.playSound(context.getPlayer(), clickedPos, soundEvent.get(), SoundSource.BLOCKS, 1.0f, pitch);
        } else {
            level.playSound(null, clickedPos, soundEvent.get(), SoundSource.BLOCKS, 1.0f, pitch);
        }
    }

    @Override
    public void playParticle(UseOnContext context) {
    }

    public float getItemSoundPitch(UseOnContext context) {
        return context.getLevel().getRandom().nextFloat() * 0.1F + 0.9F;
    }

    @Nullable
    public IRegistryHolder<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return null;
    }
}
