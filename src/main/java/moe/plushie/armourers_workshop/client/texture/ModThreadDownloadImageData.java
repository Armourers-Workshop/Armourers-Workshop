package moe.plushie.armourers_workshop.client.texture;

import java.awt.image.BufferedImage;
import java.io.File;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

public class ModThreadDownloadImageData extends ThreadDownloadImageData {

    private PlayerTexture playerTexture;
    
    public ModThreadDownloadImageData(File file, String imageUrl, ResourceLocation resourceLocation, IImageBuffer imageBuffer, PlayerTexture playerTexture) {
        super(file, imageUrl, resourceLocation, imageBuffer);
        this.playerTexture = playerTexture;
    }
    
    @Override
    public void setBufferedImage(BufferedImage bufferedImage) {
        super.setBufferedImage(bufferedImage);
        boolean slimModel = false;
        if (bufferedImage != null) {
            int rgb = bufferedImage.getRGB(54, 20);
            if (rgb == -16777216) {
                slimModel = true;
            }
        }
        playerTexture.textureDownloaded(slimModel);
    }
}
