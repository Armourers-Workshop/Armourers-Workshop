package moe.plushie.armourers_workshop.client.render;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.common.TextureHelper;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.utils.BitwiseUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class EntityTextureInfo {

    private static final int TEXURE_REPLACMENT_WIDTH = 64;
    private static final int TEXURE_REPLACMENT_HEIGHT = 32;

    /** Width of the entities texture. */
    private final int textureWidth;
    /** Height of the entities texture. */
    private final int textureHeight;
    /** The last texture entity had when the replacement texture was made. */
    private int lastEntityTextureHash;
    /** The last skin hashs the entity had when the replacement texture was made. */
    private int[] lastSkinHashs;
    /** The last dye hashs the entity had when the replacement texture was made. */
    private int[] lastDyeHashs;
    /** Skins that the entity has equipped. */
    private Skin[] skins;
    /** Dyes that the entity has on it's skins. */
    private ISkinDye[] dyes;
    /** The entities normal texture. */
    private ResourceLocation normalTexture;
    /** The entities replacement texture. */
    private ResourceLocation replacementTexture;
    /**
     * The last extra colours the entity had when the replacement texture was made.
     */
    private ExtraColours lastEntityColours;
    /** A buffered image of the entity texture. */
    private BufferedImage bufferedEntityImage;
    /** A buffered image of the entity replacement texture. */
    private BufferedImage bufferedEntitySkinnedImage;
    /** Does the texture need to be remade? */
    private boolean needsUpdate;
    /** Is this texture still loading? */
    private boolean loading;

    public EntityTextureInfo() {
        this(64, 64);
    }

    public EntityTextureInfo(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        lastEntityTextureHash = -1;
        lastSkinHashs = new int[5 * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE + 4];
        lastDyeHashs = new int[5 * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE + 4];
        normalTexture = null;
        replacementTexture = null;
        for (int i = 0; i < lastSkinHashs.length; i++) {
            lastSkinHashs[i] = -1;
        }
        for (int i = 0; i < lastDyeHashs.length; i++) {
            lastDyeHashs[i] = -1;
        }
        lastEntityColours = ExtraColours.EMPTY_COLOUR;
        needsUpdate = true;
        loading = false;
    }

    public boolean getNeedsUpdate() {
        return needsUpdate;
    }

    public void updateTexture(ResourceLocation resourceLocation) {
        if (lastEntityTextureHash != resourceLocation.hashCode()) {
            BufferedImage buff = TextureHelper.getBufferedImageSkin(resourceLocation);
            bufferedEntityImage = null;
            if (buff != null) {
                loading = false;
                lastEntityTextureHash = resourceLocation.hashCode();
                normalTexture = resourceLocation;
                bufferedEntityImage = buff;
                needsUpdate = true;
            }
        }

        if (bufferedEntityImage == null) {
            // Texture is most likely not downloaded yet.
            lastEntityTextureHash = DefaultPlayerSkin.getDefaultSkinLegacy().hashCode();
            bufferedEntityImage = TextureHelper.getBufferedImageSkin(DefaultPlayerSkin.getDefaultSkinLegacy());
            if (bufferedEntityImage != null & !loading) {
                loading = true;
                needsUpdate = true;
            }
        }
    }

    public void updateExtraColours(IExtraColours extraColours) {
        if (!lastEntityColours.equals(extraColours)) {
            lastEntityColours = new ExtraColours(extraColours);
            needsUpdate = true;
        }
    }

    public void updateSkins(Skin[] skins) {
        this.skins = skins;
        for (int i = 0; i < skins.length; i++) {
            if (skins[i] != null) {
                if (skins[i].lightHash() != lastSkinHashs[i]) {
                    lastSkinHashs[i] = skins[i].lightHash();
                    needsUpdate = true;
                }
            } else {
                if (lastSkinHashs[i] != -1) {
                    lastSkinHashs[i] = -1;
                    needsUpdate = true;
                }
            }
        }
    }

    public void updateDyes(ISkinDye[] dyes) {
        this.dyes = dyes;
        for (int i = 0; i < skins.length; i++) {
            if (dyes[i] != null) {
                if (dyes[i].hashCode() != lastDyeHashs[i]) {
                    lastDyeHashs[i] = dyes[i].hashCode();
                    needsUpdate = true;
                }
            } else {
                if (lastDyeHashs[i] != -1) {
                    lastDyeHashs[i] = -1;
                    needsUpdate = true;
                }
            }
        }
    }

    public void checkTexture() {
        if (needsUpdate) {
            buildTexture();
            needsUpdate = false;
        }
    }

    private void buildTexture() {
        // TODO check if the skins have a texture.
        applyPlayerToTexture();
        applySkinsToTexture();
        createReplacmentTexture();
    }

    private void applyPlayerToTexture() {
        bufferedEntitySkinnedImage = TextureHelper.deepCopyBufferedImage(bufferedEntityImage);
    }

    private void applySkinsToTexture() {
        // Paint the players texture.
        for (int i = 0; i < skins.length; i++) {
            Skin skin = skins[i];
            if (skin != null && skin.hasPaintData()) {
                paintTexture(skin, dyes[i], bufferedEntitySkinnedImage);
            }
        }

        // Set the players texture to blank.
        for (int i = 0; i < skins.length; i++) {
            Skin skin = skins[i];
            if (skin != null) {
                for (int partIndex = 0; partIndex < skin.getSkinType().getSkinParts().size(); partIndex++) {
                    ISkinPartType part = skin.getSkinType().getSkinParts().get(partIndex);
                    if (part instanceof ISkinPartTypeTextured) {
                        ISkinPartTypeTextured skinPartTex = ((ISkinPartTypeTextured) part);
                        makePartBlank(skinPartTex, bufferedEntitySkinnedImage, skin.getProperties());
                    }
                }
            }
        }
    }

    public void makePartBlank(ISkinPartTypeTextured skinPartTex, BufferedImage texture, SkinProperties skinProps) {
        Point posBase = skinPartTex.getTextureBasePos();
        Point posOverlay = skinPartTex.getTextureOverlayPos();

        int width = (skinPartTex.getTextureModelSize().getX() * 2) + (skinPartTex.getTextureModelSize().getZ() * 2);
        int height = skinPartTex.getTextureModelSize().getY() + skinPartTex.getTextureModelSize().getZ();

        for (int ix = 0; ix < width; ix++) {
            for (int iy = 0; iy < height; iy++) {
                if (skinPartTex.isModelOverridden(skinProps)) {
                    texture.setRGB(posBase.x + ix, posBase.y + iy, 0x00FFFFFF);
                }
                if (skinPartTex.isOverlayOverridden(skinProps)) {
                    texture.setRGB(posOverlay.x + ix, posOverlay.y + iy, 0x00FFFFFF);
                }
            }
        }
    }

    private void paintTexture(Skin skin, ISkinDye skinDye, BufferedImage texture) {
        for (int ix = 0; ix < TEXURE_REPLACMENT_WIDTH; ix++) {
            for (int iy = 0; iy < TEXURE_REPLACMENT_HEIGHT; iy++) {
                int paintColour = skin.getPaintData()[ix + (iy * textureWidth)];
                IPaintType paintType = PaintTypeRegistry.getInstance().getPaintTypeFromColour(paintColour);
                if (paintType == PaintTypeRegistry.PAINT_TYPE_NORMAL) {
                    paintTexture(bufferedEntitySkinnedImage, ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                } else if (paintType.getId() >= 1 && paintType.getId() <= 8) {
                    int dyeNumber = paintType.getId() - 1;
                    if (skinDye != null && skinDye.haveDyeInSlot(dyeNumber)) {
                        byte[] dye = skinDye.getDyeColour(dyeNumber);
                        int colour = dyeColour(dye, paintColour, paintType.getChannelIndex(), skin);
                        paintTexture(bufferedEntitySkinnedImage, ix, iy, colour);
                    } else {
                        paintTexture(bufferedEntitySkinnedImage, ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                    }
                } else if (paintType.getColourType() != null) {
                    int colour = dyeColour(lastEntityColours.getColourBytes(paintType.getColourType()), paintColour, paintType.getChannelIndex(), skin);
                    paintTexture(bufferedEntitySkinnedImage, ix, iy, colour);
                }
            }
        }
    }

    private void paintTexture(BufferedImage texture, int x, int y, int rgb) {
        texture.setRGB(x, y, rgb);
        // Paint left leg.
        if (x < 16 & y >= 16 & y < 32) {
            if (y >= 20) {
                if (x < 12) {
                    texture.setRGB(15 + (12 - x), y + 32, rgb);
                } else {
                    // Back face
                    texture.setRGB(27 + (4 - (x - 12)), y + 32, rgb);
                }
            } else {
                // Top and bottom
                if (x < 8) {
                    texture.setRGB(15 + (8 - (x - 4)), y + 32, rgb);
                } else {
                    texture.setRGB(23 + (8 - (x - 4)), y + 32, rgb);
                }

            }
        }

        // Paint left arm.
        if (x >= 40 & x < 56 & y >= 16 & y < 32) {
            if (y >= 20) {
                if (x < 52) {
                    texture.setRGB(12 - (x - 40) + 31, y + 32, rgb);
                } else {
                    // Back face
                    texture.setRGB(4 - (x - 52) + 43, y + 32, rgb);
                }
            } else {
                // Top and bottom
                if (x < 48) {
                    texture.setRGB((8 - (x - 40) + 4) + 31, y + 32, rgb);
                } else {
                    texture.setRGB((8 - (x - 48) + 4) + 31, y + 32, rgb);
                }

            }
        }
    }

    private void paintMirroredPart(BufferedImage texture, int x, int y, int rgb, int width, int height, int depth, int offsetX, int offsetY) {
        if (y >= 20) {
            if (x < 12) {
                texture.setRGB(15 + (12 - x), y + 32, rgb);
            } else {
                // Back face
                texture.setRGB(27 + (4 - (x - 12)), y + 32, rgb);
            }
        } else {
            // Top and bottom
            texture.setRGB(15 + (16 - x), y + 32, rgb);
        }
    }

    private int dyeColour(byte[] dye, int colour, int dyeIndex, Skin skin) {
        byte r = (byte) (colour >>> 16 & 0xFF);
        byte g = (byte) (colour >>> 8 & 0xFF);
        byte b = (byte) (colour & 0xFF);

        if (dye.length > 3) {
            IPaintType t = PaintTypeRegistry.getInstance().getPaintTypeFormByte(dye[3]);
            if (t.getColourType() != null) {
                dye = lastEntityColours.getColourBytes(t.getColourType());
            }
        }

        int[] average = { 127, 127, 127 };

        if (skin != null) {
            average = skin.getAverageDyeColour(dyeIndex);
        }
        dye = ColouredFace.dyeColour(r, g, b, dye, average);

        return (255 << 24) + ((dye[0] & 0xFF) << 16) + ((dye[1] & 0xFF) << 8) + (dye[2] & 0xFF);
    }

    @Override
    protected void finalize() throws Throwable {
        deleteTexture();
        super.finalize();
    }

    public void deleteTexture() {
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        if (replacementTexture != null) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {

                @Override
                public void run() {
                    renderEngine.deleteTexture(replacementTexture);
                }
            });
        }
    }

    private void createReplacmentTexture() {
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        if (replacementTexture != null) {
            renderEngine.deleteTexture(replacementTexture);
        }
        SkinTextureObject sto = new SkinTextureObject(bufferedEntitySkinnedImage);
        replacementTexture = new ResourceLocation(LibModInfo.ID.toLowerCase(), String.valueOf(bufferedEntitySkinnedImage.hashCode()));
        renderEngine.loadTexture(replacementTexture, sto);
    }

    public ResourceLocation preRender() {
        checkTexture();
        if (replacementTexture != null) {
            return replacementTexture;
        } else {
            return normalTexture;
        }
    }

    public ResourceLocation getReplacementTexture() {
        return replacementTexture;
    }

    public ResourceLocation postRender() {
        return normalTexture;
    }

    private class SkinTextureObject extends AbstractTexture {

        private final BufferedImage texture;

        public SkinTextureObject(BufferedImage texture) {
            this.texture = texture;
        }

        @Override
        public void loadTexture(IResourceManager resourceManager) throws IOException {
            getGlTextureId();
            TextureUtil.uploadTextureImage(glTextureId, texture);
        }
    }
}
