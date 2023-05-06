package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.api.registry.ISoundEventBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistryEntry;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventBuilderImpl<T extends SoundEvent> implements ISoundEventBuilder<T> {

    @Override
    public IRegistryKey<T> build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(registryName);
        Registry.registerSoundEventFA(name, () -> event);
        return AbstractFabricRegistryEntry.cast(registryName, event);
    }
}
