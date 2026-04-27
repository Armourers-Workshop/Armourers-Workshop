package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.sounds.SoundEvent;

public class AbstractSoundEventBuilder<T extends SoundEvent> {

    public T build(OpenResourceKey registryName) {
        return Objects.unsafeCast(create(registryName));
    }

    protected SoundEvent create(OpenResourceKey registryName) {
        return SoundEvent.createVariableRangeEvent(registryName.get());
    }
}
