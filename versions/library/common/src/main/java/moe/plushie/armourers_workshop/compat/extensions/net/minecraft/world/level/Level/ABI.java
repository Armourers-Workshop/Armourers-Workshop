package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.level.Level;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.level.Level;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[16, 26)")
public class ABI {

    public static int moonPhase(@This Level level) {
        return level.getMoonPhase();
    }
}
