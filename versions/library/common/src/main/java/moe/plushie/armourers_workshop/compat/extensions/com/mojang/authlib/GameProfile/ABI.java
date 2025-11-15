package moe.plushie.armourers_workshop.compat.extensions.com.mojang.authlib.GameProfile;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import moe.plushie.armourers_workshop.api.annotation.Available;

import java.util.UUID;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.22)")
@Extension
public class ABI {

    public static UUID id(@This GameProfile profile) {
        return profile.getId();
    }

    public static String name(@This GameProfile profile) {
        return profile.getName();
    }

    public static PropertyMap properties(@This GameProfile profile) {
        return profile.getProperties();
    }
}
