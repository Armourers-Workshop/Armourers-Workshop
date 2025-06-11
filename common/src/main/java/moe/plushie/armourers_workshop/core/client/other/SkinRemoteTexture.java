package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.HttpTexture;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Environment(EnvType.CLIENT)
public class SkinRemoteTexture extends HttpTexture {

    public SkinRemoteTexture(String url, File path, OpenResourceLocation placeholder, boolean bl, @Nullable Runnable runnable) {
        super(path, url, placeholder.toLocation(), bl, runnable);
    }
}
