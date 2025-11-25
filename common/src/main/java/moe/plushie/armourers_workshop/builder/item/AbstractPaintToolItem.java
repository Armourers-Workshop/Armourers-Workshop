package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.common.IItemParticleProvider;
import moe.plushie.armourers_workshop.api.common.IItemSoundProvider;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.core.item.ConfigurableToolItem;
import moe.plushie.armourers_workshop.init.ModHolidays;
import moe.plushie.armourers_workshop.init.ModSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPaintToolItem extends ConfigurableToolItem implements IItemSoundProvider, IItemParticleProvider {

    public AbstractPaintToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public void playSound(IUseOnContext context) {
        var soundEvent = getItemSoundEvent(context);
        if (soundEvent == null) {
            return;
        }
        if (ModHolidays.APRIL_FOOLS.isHolidayActive()) {
            soundEvent = ModSoundEvents.BOI;
        }
        var pitch = getItemSoundPitch(context);
        var level = context.level();
        var clickedPos = context.clickedPos();
        if (level.isClientSide()) {
            level.playSound(context.player(), clickedPos, soundEvent.get(), SoundSource.BLOCKS, 1.0f, pitch);
        } else {
            level.playSound(null, clickedPos, soundEvent.get(), SoundSource.BLOCKS, 1.0f, pitch);
        }
    }

    @Override
    public void playParticle(IUseOnContext context) {
    }

    public float getItemSoundPitch(IUseOnContext context) {
        return context.level().getRandom().nextFloat() * 0.1F + 0.9F;
    }

    @Nullable
    public IRegistryHolder<SoundEvent> getItemSoundEvent(IUseOnContext context) {
        return null;
    }
}
