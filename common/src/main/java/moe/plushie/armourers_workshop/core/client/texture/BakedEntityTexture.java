package moe.plushie.armourers_workshop.core.client.texture;

import moe.plushie.armourers_workshop.core.data.color.TexturedPaintColor;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3i;
import moe.plushie.armourers_workshop.core.math.OpenVector2i;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenNativeImage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BakedEntityTexture {

    private final HashMap<Integer, SkinPaintColor> allColors = new HashMap<>();
    private final HashMap<SkinPartType, HashMap<Integer, SkinPaintColor>> allParts = new HashMap<>();
    private final HashMap<SkinPartType, OpenRectangle3i> allBounds = new HashMap<>();

    private String modelType;
    private OpenResourceLocation resourceLocation;

    private boolean isSlimModel = false;
    private boolean isLoaded = false;

    public BakedEntityTexture() {
    }

    public BakedEntityTexture(OpenResourceLocation resourceLocation, boolean slim) {
        this.isSlimModel = slim;
        this.resourceLocation = resourceLocation;
        BufferedImage bufferedImage;
        try {
            var resourceManager = EnvironmentManager.getResourceManager();
            bufferedImage = ImageIO.read(resourceManager.readResource(resourceLocation).inputStream());
            if (bufferedImage != null) {
//                slim = (bufferedImage.getRGB(54, 20) & 0xff000000) == 0;
                this.loadColors(bufferedImage.getWidth(), bufferedImage.getHeight(), slim, bufferedImage::getRGB);
            }
        } catch (IOException ignored) {
        }
    }

    public void loadImage(OpenNativeImage image, boolean slim) {
        this.loadColors(image.width(), image.height(), slim, image::getPixel);
    }

    private void loadColors(int width, int height, boolean slim, IColorAccessor accessor) {
        for (var entry : EntityTextureModel.of(width, height, slim).entrySet()) {
            var box = entry.getValue();
            var part = allParts.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
            allBounds.put(entry.getKey(), box.bounds());
            box.forEach((texture, x, y, z, dir) -> {
                int color = accessor.getRGB(texture.x(), texture.y());
                if (SkinPaintColor.isOpaque(color)) {
                    var paintColor = TexturedPaintColor.of(color, SkinPaintTypes.NORMAL);
                    part.put(getPosKey(x, y, z, dir), paintColor);
                    allColors.put(getUVKey(texture.x(), texture.y()), paintColor);
                }
            });
        }
        this.isLoaded = true;
    }

    public SkinPaintColor getColor(OpenVector2i texturePos) {
        return getColor(texturePos.x(), texturePos.y());
    }

    public SkinPaintColor getColor(int u, int v) {
        return allColors.get(getUVKey(u, v));
    }

    public SkinPaintColor getColor(int x, int y, int z, OpenDirection dir, SkinPartType partType) {
        var part = allParts.get(partType);
        var bounds = allBounds.get(partType);
        if (part == null || bounds == null) {
            return null;
        }
        x = OpenMath.clamp(x, bounds.minX(), bounds.maxX() - 1);
        y = OpenMath.clamp(y, bounds.minY(), bounds.maxY() - 1);
        z = OpenMath.clamp(z, bounds.minZ(), bounds.maxZ() - 1);
        return part.get(getPosKey(x, y, z, dir));
    }

    private int getPosKey(int x, int y, int z, OpenDirection dir) {
        return (dir.get3DDataValue() & 0xff) << 24 | (z & 0xff) << 16 | (y & 0xff) << 8 | (x & 0xff);
    }

    private int getUVKey(int u, int v) {
        return (v & 0xffff) << 16 | (u & 0xffff);
    }

    public OpenResourceLocation location() {
        return resourceLocation;
    }

    public void setResourceLocation(OpenResourceLocation location) {
        this.resourceLocation = location;
    }

    public String modelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
        this.isSlimModel = Objects.equals(modelType, "slim");
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
