package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class AbstractSoundEventBuilder<T extends SoundEvent> {

    public T build(OpenResourceLocation registryName) {
        return Objects.unsafeCast(create(registryName));
    }

    protected SoundEvent create(OpenResourceLocation registryName) {
        return SoundEvent.createVariableRangeEvent(registryName.toLocation());
    }
}
