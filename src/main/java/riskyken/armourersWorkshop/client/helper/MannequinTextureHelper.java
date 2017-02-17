package riskyken.armourersWorkshop.client.helper;

import java.io.File;
import java.util.HashSet;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin.TextureType;

public final class MannequinTextureHelper {
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_IMAGE_URL = "imageUrl";
    
    private static long lastSkinDownload = 0;
    private static final HashSet<String> downloadedSkins = new HashSet<String>();
    
    private MannequinTextureHelper() {}

    public static ResourceLocation getMannequinResourceLocation(ItemStack itemStack) {
        ResourceLocation resourceLocation = AbstractClientPlayer.locationStevePng;
        
        GameProfile gameProfile = null;
        String imageUrl = null;
        
        if (itemStack.hasTagCompound()) {
            NBTTagCompound compound = itemStack.getTagCompound();
            if (compound.hasKey(TAG_OWNER, 10)) {
                gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
            }
            if (compound.hasKey(TAG_IMAGE_URL, Constants.NBT.TAG_STRING)) {
                imageUrl = compound.getString(TAG_IMAGE_URL);
            }
        }
        
        if (gameProfile != null) {
            resourceLocation = AbstractClientPlayer.getLocationSkin(gameProfile.getName());
            AbstractClientPlayer.getDownloadImageSkin(resourceLocation, gameProfile.getName());
        }
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            resourceLocation = downloadImageUrl(resourceLocation, imageUrl);
        }
        return resourceLocation;
    }
    
    public static ResourceLocation getMannequinResourceLocation(TileEntityMannequin tileEntity) {
        ResourceLocation resourceLocation = AbstractClientPlayer.locationStevePng;
        
        
        if (tileEntity.getGameProfile() != null && tileEntity.getTextureType() == TextureType.USER) {
            String name = tileEntity.getGameProfile().getName();
            if (downloadedSkins.contains(name)) {
                resourceLocation = AbstractClientPlayer.getLocationSkin(name);
                AbstractClientPlayer.getDownloadImageSkin(resourceLocation, name);
            } else {
                if (lastSkinDownload + 100L < System.currentTimeMillis()) {
                    lastSkinDownload = System.currentTimeMillis();
                    resourceLocation = AbstractClientPlayer.getLocationSkin(name);
                    AbstractClientPlayer.getDownloadImageSkin(resourceLocation, name);
                    downloadedSkins.add(name);
                }
            }
        }
        
        if (!StringUtils.isNullOrEmpty(tileEntity.getImageUrl()) &&  tileEntity.getTextureType() == TextureType.URL) {
            resourceLocation = downloadImageUrl(resourceLocation, tileEntity.getImageUrl());
        }
        
        
        return resourceLocation;
    }
    
    private static ResourceLocation downloadImageUrl(ResourceLocation rl, String imageUrl) {
        if (downloadedSkins.contains(imageUrl)) {
            rl = new ResourceLocation(LibModInfo.ID.toLowerCase(), StringUtils.stripControlCodes(imageUrl));
            downloadImage(rl, imageUrl);
        } else {
            if (lastSkinDownload + 100L < System.currentTimeMillis()) {
                lastSkinDownload = System.currentTimeMillis();
                rl = new ResourceLocation(LibModInfo.ID.toLowerCase(), StringUtils.stripControlCodes(imageUrl));
                downloadImage(rl, imageUrl);
                downloadedSkins.add(imageUrl);
            }
        }
        return rl;
    }
    
    private static ThreadDownloadImageData downloadImage(ResourceLocation resourceLocation, String imageUrl) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        Object object = texturemanager.getTexture(resourceLocation);
        if (object == null) {
            object = new ThreadDownloadImageData((File)null, imageUrl, AbstractClientPlayer.locationStevePng, new ImageBufferDownload());
            texturemanager.loadTexture(resourceLocation, (ITextureObject)object);
        }
        return (ThreadDownloadImageData)object;
    }
}
