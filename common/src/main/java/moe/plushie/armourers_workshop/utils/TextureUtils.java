package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.math.TexturePos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public final class TextureUtils {


    public static ResourceLocation getTexture(Entity entity) {
        if (entity instanceof AbstractClientPlayer) {
            return ((AbstractClientPlayer) entity).getSkinTextureLocation();
        }
        return DefaultPlayerSkin.getDefaultSkin();
    }

    public static ResourceLocation getPlayerTextureLocation(PlayerTextureDescriptor descriptor) {
        PlayerTexture bakedTexture = PlayerTextureLoader.getInstance().loadTexture(descriptor);
        if (bakedTexture != null && bakedTexture.isDownloaded()) {
            return bakedTexture.getLocation();
        }
//        ClientPlayer player = Minecraft.getInstance().player;
//        if (player != null) {
//            return player.getSkinTextureLocation();
//        }
        return DefaultPlayerSkin.getDefaultSkin();
    }

    @Nullable
    public static BakedEntityTexture getPlayerTextureModel(PlayerTextureDescriptor descriptor) {
        ResourceLocation texture = getPlayerTextureLocation(descriptor);
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
