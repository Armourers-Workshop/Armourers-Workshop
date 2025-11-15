package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractPlayerSkin;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;

@Available("[1.21, )")
@Extension
public class PlayerSkinProvider {

    public static AbstractPlayerSkin skin(@This Entity entity) {
        if (entity instanceof AbstractClientPlayer player) {
            return AbstractPlayerSkin.of(player.getSkin());
        }
        return AbstractPlayerSkin.getDefaultSkin();
    }
}
