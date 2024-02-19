package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.resources.SkinManager;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;

import java.util.function.Consumer;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.21, )")
public class ABI {

    public static void loadCustomSkin(@This SkinManager skinManager, GameProfile profile, Consumer<PlayerSkin> handler) {
        skinManager.getOrLoad(profile).thenAcceptAsync(handler);
    }
}
