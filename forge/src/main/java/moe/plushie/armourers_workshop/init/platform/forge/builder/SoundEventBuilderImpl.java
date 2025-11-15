package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.compat.builder.AbstractSoundEventBuilder;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilderImpl<T extends SoundEvent> implements ISoundEventBuilder<T> {

    private final AbstractSoundEventBuilder<T> builder = new AbstractSoundEventBuilder<>();

    @Override
    public IRegistryHolder<T> build(String name) {
        return Registries.SOUND_EVENTS.register(name, builder::build);
    }
}
