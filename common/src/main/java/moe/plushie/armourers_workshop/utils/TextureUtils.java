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

//
//    /*
//     * Based on @KitsuneKihira texture helper class.
//     * https://github.com/kihira/FoxLib/blob/2946cd6033d3039151064ceccfb8d38612d0af02/src/main/scala/kihira/foxlib/client/TextureHelper.scala#L28
//     */
//
//    public static BufferedImage getBufferedImageSkin(AbstractClientPlayer player) {
//        BufferedImage bufferedImage = null;
//        ResourceLocation skinloc = DefaultPlayerSkin.getDefaultSkin();
//        InputStream inputStream = null;
//        Minecraft mc = Minecraft.getInstance();
//        skinloc = player.getSkinTextureLocation();
//        try {
//            Texture skintex = mc.getTextureManager().getTexture(skinloc);
////            if (skintex instanceof ThreadDownloadImageData) {
////                ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
////                bufferedImage  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
////            } else {
//            inputStream = Minecraft.getInstance().getResourceManager().getResource(skinloc).getInputStream();
//            bufferedImage = ImageIO.read(inputStream);
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            StreamUtils.closeQuietly(inputStream);
//        }
//        return bufferedImage;
//    }
//
//    public static BufferedImage getBufferedImageSkin(ResourceLocation resourceLocation) {
//        BufferedImage bufferedImage = null;
//        InputStream inputStream = null;
//
//        try {
//            Texture texture = Minecraft.getInstance().getTextureManager().getTexture(resourceLocation);
////            if (skintex instanceof ThreadDownloadImageData) {
////                ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
////                bufferedImage  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
////            } else {
//            inputStream = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).getInputStream();
//            bufferedImage = ImageIO.read(inputStream);
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            StreamUtils.closeQuietly(inputStream);
//        }
//
//        return bufferedImage;
//    }
//
//    public static BufferedImage getBufferedImageSkin(GameProfile gameProfile) {
//        BufferedImage bufferedImage = null;
//        ResourceLocation skinloc = DefaultPlayerSkin.getDefaultSkin();
//        InputStream inputStream = null;
//        Minecraft mc = Minecraft.getInstance();
//        Map map = mc.getSkinManager().loadSkinFromCache(gameProfile);
//
//        try {
//            if (map.containsKey(Type.SKIN)) {
//                skinloc = mc.getSkinManager().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
//                ITextureObject skintex = mc.getTextureManager().getTexture(skinloc);
//
//                if (skintex instanceof ThreadDownloadImageData) {
//                    ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
//                    bufferedImage  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
//                } else {
//                    inputStream = Minecraft.getMinecraft().getResourceManager().getResource(skinloc).getInputStream();
//                    bufferedImage = ImageIO.read(inputStream);
//                }
//            } else {
//                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(skinloc).getInputStream();
//                bufferedImage = ImageIO.read(inputStream);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            StreamUtils.closeQuietly(inputStream);
//        }
//
//        return bufferedImage;
//    }

//    public static BufferedImage getBufferedImageSkinNew(GameProfile gameProfile) {
//        BufferedImage bufferedImage = null;
//        ResourceLocation rl = DefaultPlayerSkin.getDefaultSkin();
//
//        if (gameProfile != null) {
//            rl = ClientPlayer.getSkinLocation(gameProfile.getName());
////            AbstractClientPlayer.getDownloadImageSkin(rl, gameProfile.getName());
//        }
//        bufferedImage = getBuffFromResourceLocation(rl);
//
//        if (bufferedImage == null) {
//            bufferedImage = getBuffFromResourceLocation(DefaultPlayerSkin.getDefaultSkin());
//        }
//        return bufferedImage;
//    }
//
//    private static BufferedImage getBuffFromResourceLocation(ResourceLocation rl) {
//        BufferedImage bi = null;
//        InputStream inputStream = null;
//        try {
//            Texture skintex = Minecraft.getInstance().getTextureManager().getTexture(rl);
////            if (skintex instanceof ThreadDownloadImageData) {
////                ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
////                bi  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
////            } else {
//            inputStream = Minecraft.getInstance().getResourceManager().getResource(rl).getInputStream();
//            bi = ImageIO.read(inputStream);
////            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            StreamUtils.closeQuietly(inputStream);
//        }
//        return bi;
//    }
//
//    public static void bindPlayersNormalSkin(GameProfile gameProfile) {
//        ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkin();
//        if (gameProfile != null) {
//            resourcelocation = getSkinResourceLocation(gameProfile, Type.SKIN);
//        }
//        Minecraft.getInstance().getTextureManager().bind(resourcelocation);
//    }
//
//    public static ResourceLocation getSkinResourceLocation(GameProfile gameProfile, Type type) {
//        ResourceLocation skin = DefaultPlayerSkin.getDefaultSkin();
////        if (gameProfile != null) {
////            Minecraft mc = Minecraft.getInstance();
////            Map<?, ?> map = mc.getSkinManager().loadSkinFromCache(gameProfile);
////            if (map.containsKey(type)) {
////                skin = mc.getSkinManager().loadSkin((MinecraftProfileTexture)map.get(type), type);
////            }
////        }
//        return skin;
//    }
//
//    public static BufferedImage deepCopyBufferedImage(BufferedImage bufferedImage) {
//        ColorModel cm = bufferedImage.getColorModel();
//        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
//        WritableRaster raster = bufferedImage.copyData(null);
//        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
//    }

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
        BakedEntityTexture textureModel = TextureUtils.getPlayerTextureModel(descriptor);
        if (textureModel != null) {
            return textureModel.getColor(texturePos);
        }
        return null;
    }
}
