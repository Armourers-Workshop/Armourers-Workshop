package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.User;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.21, )")
public class ABI {

    public static String getUuid(@This User user) {
        return user.getProfileId().toString();
    }

    public static GameProfile getGameProfile(@This User user) {
        return Minecraft.getInstance().getGameProfile();
    }
}
