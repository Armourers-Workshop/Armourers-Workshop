package moe.plushie.armourers_workshop.core.texture;

import com.mojang.blaze3d.platform.NativeImage;
import moe.plushie.armourers_workshop.api.core.IResourceManager;
import moe.plushie.armourers_workshop.api.math.ITexturePos;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.color.TexturedPaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.texture.PlayerTextureModel;
import moe.plushie.armourers_workshop.utils.texture.SkyBox;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BakedEntityTexture {

    private final HashMap<Integer, PaintColor> allColors = new HashMap<>();
    private final HashMap<ISkinPartType, HashMap<Integer, PaintColor>> allParts = new HashMap<>();
    private final HashMap<ISkinPartType, Rectangle3i> allBounds = new HashMap<>();

    private String model;
    private IResourceLocation resourceLocation;

    private boolean isSlimModel = false;
    private boolean isLoaded = false;

    public BakedEntityTexture() {
    }

    public BakedEntityTexture(IResourceLocation resourceLocation, boolean slim) {
        this.isSlimModel = slim;
        this.resourceLocation = resourceLocation;
        BufferedImage bufferedImage;
        try {
            IResourceManager resourceManager = Minecraft.getInstance().getResourceManager().asResourceManager();
            bufferedImage = ImageIO.read(resourceManager.readResource(resourceLocation).getInputStream());
            if (bufferedImage != null) {
//                slim = (bufferedImage.getRGB(54, 20) & 0xff000000) == 0;
                this.loadColors(bufferedImage.getWidth(), bufferedImage.getHeight(), slim, bufferedImage::getRGB);
            }
        } catch (IOException ignored) {
        }
    }

    public void loadImage(NativeImage image, boolean slim) {
        this.loadColors(image.getWidth(), image.getHeight(), slim, (x, y) -> {
            int color = image.getPixelRGBA(x, y);
            int red = (color << 16) & 0xff0000;
            int blue = (color >> 16) & 0x0000ff;
            return (color & 0xff00ff00) | red | blue;
        });
    }

    private void loadColors(int width, int height, boolean slim, IColorAccessor accessor) {
        for (Map.Entry<ISkinPartType, SkyBox> entry : PlayerTextureModel.of(width, height, slim).entrySet()) {
            SkyBox box = entry.getValue();
            HashMap<Integer, PaintColor> part = allParts.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
            allBounds.put(entry.getKey(), box.getBounds());
            box.forEach((texture, x, y, z, dir) -> {
                int color = accessor.getRGB(texture.getU(), texture.getV());
                if (PaintColor.isOpaque(color)) {
                    PaintColor paintColor = TexturedPaintColor.of(color, SkinPaintTypes.NORMAL);
                    part.put(getPosKey(x, y, z, dir), paintColor);
                    allColors.put(getUVKey(texture.getU(), texture.getV()), paintColor);
                }
            });
        }
        this.isLoaded = true;
    }

    public PaintColor getColor(ITexturePos texturePos) {
        return getColor(texturePos.getU(), texturePos.getV());
    }

    public PaintColor getColor(int u, int v) {
        return allColors.get(getUVKey(u, v));
    }

    public PaintColor getColor(int x, int y, int z, Direction dir, ISkinPartType partType) {
        HashMap<Integer, PaintColor> part = allParts.get(partType);
        Rectangle3i bounds = allBounds.get(partType);
        if (part == null || bounds == null) {
            return null;
        }
        x = MathUtils.clamp(x, bounds.getMinX(), bounds.getMaxX() - 1);
        y = MathUtils.clamp(y, bounds.getMinY(), bounds.getMaxY() - 1);
        z = MathUtils.clamp(z, bounds.getMinZ(), bounds.getMaxZ() - 1);
        return part.get(getPosKey(x, y, z, dir));
    }

    private int getPosKey(int x, int y, int z, Direction dir) {
        return (dir.get3DDataValue() & 0xff) << 24 | (z & 0xff) << 16 | (y & 0xff) << 8 | (x & 0xff);
    }

    private int getUVKey(int u, int v) {
        return (v & 0xffff) << 16 | (u & 0xffff);
    }

    public IResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(IResourceLocation location) {
        this.resourceLocation = location;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
        this.isSlimModel = Objects.equals(model, "slim");
    }

    public boolean isSlimModel() {
        return isSlimModel;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    interface IColorAccessor {
        int getRGB(int x, int y);
    }
}
