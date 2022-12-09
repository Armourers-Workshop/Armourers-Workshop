package extensions.net.minecraft.sounds.SoundEvent;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.ThisClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@Extension
public class SoundEventExt {

    public static @Self SoundEvent createVariableRangeEvent(@ThisClass Class<?> clazz, ResourceLocation registryName) {
        return new SoundEvent(registryName);
    }
}
