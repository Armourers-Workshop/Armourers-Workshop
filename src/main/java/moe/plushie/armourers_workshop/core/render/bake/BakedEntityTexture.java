package moe.plushie.armourers_workshop.core.render.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.PaintColor;
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
public class BakedEntityTexture {

    private final ResourceLocation location;

    private final HashMap<Integer, PaintColor> allColors = new HashMap<>();
    private final HashMap<ISkinPartType, HashMap<Integer, PaintColor>> allParts = new HashMap<>();

    public BakedEntityTexture(ResourceLocation location) {
        this.location = location;
        this.parseColors();
    }

    private void parseColors() {
        BufferedImage bufferedImage;
        try {
            IResource resource = Minecraft.getInstance().getResourceManager().getResource(location);
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
        for (PlayerTexture model : PlayerTexture.getPlayerModels(width, height, slim)) {
            HashMap<Integer, PaintColor> part = allParts.computeIfAbsent(model.getPartType(), k -> new HashMap<>());
            model.forEach((u, v, x, y, z, dir) -> {
                int color = bufferedImage.getRGB(u, v);
                if ((color & 0xff000000) == 0) {
                    return; // ignore transparent color
                }
                PaintColor paintColor = PaintColor.of(color, SkinPaintTypes.NORMAL);
                part.put(getPosKey(x, y, z, dir), paintColor);
                allColors.put(getUVKey(u, v), paintColor);
            });
        }
    }


    public PaintColor getColor(int u, int v) {
        return allColors.get(getUVKey(u, v));
    }

    public PaintColor getColor(int x, int y, int z, Direction dir, ISkinPartType partType) {
        HashMap<Integer, PaintColor> part = allParts.get(partType);
        if (part == null) {
            return null;
        }
        return part.get(getPosKey(x, y, z, dir));
    }

    public ResourceLocation getLocation() {
        return location;
    }

    private int getPosKey(int x, int y, int z, Direction dir) {
        return (dir.get3DDataValue() & 0xff) << 24 | (z & 0xff) << 16 | (y & 0xff) << 8 | (x & 0xff);
    }

    private int getUVKey(int u, int v) {
        return (v & 0xffff) << 16 | (u & 0xffff);
    }
}
