package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.level.GameRules;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.level.GameRules;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[16, 26)")
public class ABI {

    public static boolean get(@This GameRules rules, GameRules.Key<GameRules.BooleanValue> key) {
        return rules.getBoolean(key);
    }
}
