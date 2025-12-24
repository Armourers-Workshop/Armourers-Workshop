package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.player.Player;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.player.Player;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.26)")
@Extension
public class NameAndID {

    public static GameProfile nameAndId(@This Player player) {
        return player.getGameProfile();
    }
}
