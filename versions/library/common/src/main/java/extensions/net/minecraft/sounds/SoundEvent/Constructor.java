package extensions.net.minecraft.sounds.SoundEvent;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.ThisClass;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@Extension
@Available("[1.16, 1.19.4)")
public abstract class Constructor {

    public static @Self SoundEvent createVariableRangeEvent(@ThisClass Class<?> clazz, ResourceLocation registryName) {
        return new SoundEvent(registryName);
    }
}
