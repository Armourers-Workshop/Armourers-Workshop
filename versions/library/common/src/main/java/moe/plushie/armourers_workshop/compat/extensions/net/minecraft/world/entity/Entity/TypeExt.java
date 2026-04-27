package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[16, 26)")
public class TypeExt {

    public static boolean isBoat(@This Entity entity) {
        return entity instanceof Boat;
    }

    public static boolean isMinecart(@This Entity entity) {
        return entity instanceof AbstractMinecart;
    }

    public static boolean isArrow(@This Entity entity) {
        return entity instanceof AbstractArrow;
    }

    public static boolean isSpectralArrow(@This Entity entity) {
        return entity instanceof SpectralArrow;
    }

    public static boolean isThrownTrident(@This Entity entity) {
        return entity instanceof ThrownTrident;
    }

    public static boolean isFishingHook(@This Entity entity) {
        return entity instanceof FishingHook;
    }

    public static boolean isHorse(@This Entity entity) {
        return entity instanceof AbstractHorse;
    }

    public static boolean isPig(@This Entity entity) {
        return entity instanceof Pig;
    }

}
