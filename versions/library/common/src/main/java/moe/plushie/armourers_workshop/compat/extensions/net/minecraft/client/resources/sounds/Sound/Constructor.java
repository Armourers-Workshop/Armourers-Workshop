package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.resources.sounds.Sound;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractSimpleSound;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.util.valueproviders.ConstantFloat;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class Constructor {

    public static @Self Sound create(@ThisClass Class<?> clazz, AbstractSimpleSound sound) {
        var location = sound.id();
        var volume = ConstantFloat.of(sound.volume());
        var pitch = ConstantFloat.of(sound.pitch());
        var weight = sound.weight();
        var attenuationDistance = sound.attenuationDistance();
        return new Sound(location, volume, pitch, weight, Sound.Type.FILE, false, false, attenuationDistance);
    }
}
