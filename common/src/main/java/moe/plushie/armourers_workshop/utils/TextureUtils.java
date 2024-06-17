package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.client.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class TextureUtils {

    public static IResourceLocation registerTexture(String key, DynamicTexture texture) {
        var location = Minecraft.getInstance().getTextureManager().register(key, texture);
        return OpenResourceLocation.create(location);
    }

    public static IResourceLocation getTexture(Entity entity) {
        if (entity instanceof AbstractClientPlayer player) {
            var location = player.getSkin().texture();
            return OpenResourceLocation.create(location);
        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    public static IResourceLocation getPlayerTextureLocation(PlayerTextureDescriptor descriptor) {
        PlayerTexture bakedTexture = PlayerTextureLoader.getInstance().loadTexture(descriptor);
        if (bakedTexture != null && bakedTexture.isDownloaded()) {
            return bakedTexture.getLocation();
        }
//        ClientPlayer player = Minecraft.getInstance().player;
//        if (player != null) {
//            return player.getSkinTextureLocation();
//        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    @Nullable
    public static BakedEntityTexture getPlayerTextureModel(PlayerTextureDescriptor descriptor) {
        IResourceLocation texture = getPlayerTextureLocation(descriptor);
        if (texture != null) {
            return PlayerTextureLoader.getInstance().getTextureModel(texture);
        }
        return null;
    }

    public static IPaintColor getPlayerTextureModelColor(PlayerTextureDescriptor descriptor, TexturePos texturePos) {
        BakedEntityTexture textureModel = getPlayerTextureModel(descriptor);
        if (textureModel != null) {
            return textureModel.getColor(texturePos);
        }
        return null;
    }
}
