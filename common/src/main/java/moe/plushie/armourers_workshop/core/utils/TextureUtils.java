package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.client.texture.EntityTextureLoader;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.init.ModTextures;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public final class TextureUtils {

    public static OpenResourceLocation getPlayerTextureLocation(EntityTextureDescriptor descriptor) {
        var texture = EntityTextureLoader.getInstance().loadTexture(descriptor);
        if (texture != null && texture.image() != null) {
            return texture.location();
        }
//        var player = Minecraft.getInstance().player;
//        if (player != null) {
//            return player.getSkinTextureLocation();
//        }
        return ModTextures.MANNEQUIN_DEFAULT;
    }

    @Nullable
    public static BakedEntityTexture getPlayerTextureModel(EntityTextureDescriptor descriptor) {
        var texture = getPlayerTextureLocation(descriptor);
        if (texture != null) {
            return EntityTextureLoader.getInstance().getTextureModel(texture);
        }
        return null;
    }

    public static SkinPaintColor getPlayerTextureModelColor(EntityTextureDescriptor descriptor, OpenVector2i texturePos) {
        var textureModel = getPlayerTextureModel(descriptor);
        if (textureModel != null) {
            return textureModel.getColor(texturePos);
        }
        return null;
    }
}
