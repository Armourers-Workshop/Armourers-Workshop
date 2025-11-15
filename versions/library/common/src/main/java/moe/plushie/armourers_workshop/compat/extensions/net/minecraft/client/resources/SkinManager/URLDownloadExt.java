package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.resources.SkinManager;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.texture.EntityTextureDownloader;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.resources.SkinManager;

import java.io.File;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.22)")
@Extension
public class URLDownloadExt {

    public static void downloadAndRegisterSkin(@This SkinManager manager, String url, boolean processLegacySkin, File file, OpenResourceLocation location, OpenResourceLocation placeholder, EntityTextureDownloader.Callback callback) {
        var textureManager = Minecraft.getInstance().getTextureManager();
        var downloadingTexture = new HttpTexture(file, url, placeholder.toLocation(), processLegacySkin, () -> {
            callback.apply(location, url, null);
        });
        textureManager.register(location.toLocation(), downloadingTexture);
    }
}
