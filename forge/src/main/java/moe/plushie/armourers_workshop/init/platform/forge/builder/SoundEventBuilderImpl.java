package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeRegistries;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilderImpl<T extends SoundEvent> implements ISoundEventBuilder<T> {

    @Override
    public IRegistryKey<T> build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(registryName);
        AbstractForgeRegistries.SOUND_EVENTS.register(name, () -> event);
        return TypedRegistry.Entry.cast(registryName, () -> event);
    }
}
