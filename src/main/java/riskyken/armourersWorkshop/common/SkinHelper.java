package riskyken.armourersWorkshop.common;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

public final class SkinHelper {
    /*
     * Based on @KitsuneKihira texture helper class.
     * https://github.com/kihira/FoxLib/blob/2946cd6033d3039151064ceccfb8d38612d0af02/src/main/scala/kihira/foxlib/client/TextureHelper.scala#L28
     */
    
    public static BufferedImage getBufferedImageSkin(AbstractClientPlayer player) {
        BufferedImage bufferedImage = null;
        ResourceLocation skinloc = AbstractClientPlayer.locationStevePng;
        InputStream inputStream = null;
        Minecraft mc = Minecraft.getMinecraft();
        skinloc = player.getLocationSkin();
        try {
            ITextureObject skintex = mc.getTextureManager().getTexture(skinloc);
            if (skintex instanceof ThreadDownloadImageData) {
                ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
                bufferedImage  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
            } else {
                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(skinloc).getInputStream();
                bufferedImage = ImageIO.read(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return bufferedImage;
    }
    
    public static BufferedImage getBufferedImageSkin(ResourceLocation resourceLocation) {
        Minecraft mc = Minecraft.getMinecraft();
        BufferedImage bufferedImage = null;
        InputStream inputStream = null;
        
        try {
            ITextureObject skintex = mc.getTextureManager().getTexture(resourceLocation);
            if (skintex instanceof ThreadDownloadImageData) {
                ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
                bufferedImage  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
            } else {
                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
                bufferedImage = ImageIO.read(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        return bufferedImage;
    }
    
    public static BufferedImage getBufferedImageSkin(GameProfile gameProfile) {
        BufferedImage bufferedImage = null;
        ResourceLocation skinloc = AbstractClientPlayer.locationStevePng;
        InputStream inputStream = null;
        Minecraft mc = Minecraft.getMinecraft();
        Map map = mc.func_152342_ad().func_152788_a(gameProfile);
        
        try {
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                skinloc = mc.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
                ITextureObject skintex = mc.getTextureManager().getTexture(skinloc);
                
                if (skintex instanceof ThreadDownloadImageData) {
                    ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
                    bufferedImage  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
                } else {
                    inputStream = Minecraft.getMinecraft().getResourceManager().getResource(skinloc).getInputStream();
                    bufferedImage = ImageIO.read(inputStream);
                }
            } else {
                //inputStream = Minecraft.getMinecraft().getResourceManager().getResource(skinloc).getInputStream();
                //bufferedImage = ImageIO.read(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        return bufferedImage;
    }
    
    public static void bindPlayersNormalSkin(GameProfile gameProfile) {
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        if (gameProfile != null) {
            resourcelocation = getSkinResourceLocation(gameProfile, MinecraftProfileTexture.Type.SKIN);
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(resourcelocation);
    }
    
    public static ResourceLocation getSkinResourceLocation(GameProfile gameProfile, MinecraftProfileTexture.Type type) {
        ResourceLocation skin = AbstractClientPlayer.locationStevePng;
        Minecraft mc = Minecraft.getMinecraft();
        Map<?, ?> map = mc.func_152342_ad().func_152788_a(gameProfile);
        if (map.containsKey(type)) {
            skin = mc.func_152342_ad().func_152792_a((MinecraftProfileTexture)map.get(type), type);
        }
        return skin;
    }
    
    public static BufferedImage deepCopyBufferedImage(BufferedImage bufferedImage) {
        ColorModel cm = bufferedImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bufferedImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
