package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.player.Player;

import com.mojang.authlib.GameProfile;

import manifold.ext.rt.api.Extension;

import manifold.ext.rt.api.This;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.player.Player;

@Available("[1.16, 1.22)")
@Extension
public class NameAndID {

    public static GameProfile nameAndId(@This Player player) {
        return player.getGameProfile();
    }
}
