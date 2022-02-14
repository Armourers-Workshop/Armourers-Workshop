package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.painting.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class BakedSkinTexture {

    HashMap<ISkinPartType, HashMap<Integer, PaintColor>> allParts = new HashMap<>();

    public BakedSkinTexture(ResourceLocation skinLocation) {
        BufferedImage bufferedImage;
        try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(skinLocation);
            bufferedImage = ImageIO.read(resource.getInputStream());
            if (bufferedImage == null) {
                return;
            }
        } catch (IOException e) {
            return;
        }
        boolean slim = (bufferedImage.getRGB(54, 20) & 0xff000000) == 0;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for (SkinTexture model : SkinTexture.getPlayerModels(width, height, slim)) {
            HashMap<Integer, PaintColor> part = allParts.computeIfAbsent(model.getPartType(), k -> new HashMap<>());
            model.forEach((u, v, x, y, z, dir) -> {
                int color = bufferedImage.getRGB(u, v);
                if ((color & 0xff000000) == 0) {
                    return;
                }
                part.put(getKey(x, y, z, dir), new PaintColor(color, SkinPaintTypes.NORMAL));
            });
        }
    }

    private static int getKey(int x, int y, int z, Direction dir) {
        return (dir.get3DDataValue() & 0xff) << 24 | (z & 0xff) << 16 | (y & 0xff) << 8 | (x & 0xff);
    }

    public PaintColor getColor(int x, int y, int z, Direction dir, ISkinPartType partType) {
        HashMap<Integer, PaintColor> part = allParts.get(partType);
        if (part == null) {
            return null;
        }
        return part.get(getKey(x, y, z, dir));
    }
}
