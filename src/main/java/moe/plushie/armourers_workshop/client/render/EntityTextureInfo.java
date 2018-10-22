package moe.plushie.armourers_workshop.client.render;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import moe.plushie.armourers_workshop.api.common.IPoint3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.common.SkinHelper;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.PaintRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
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
    /** The last extra colours the entity had when the replacement texture was made. */
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
        lastSkinHashs = new int[4 * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE];
        lastDyeHashs = new int[4 * EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE];
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
            BufferedImage buff = SkinHelper.getBufferedImageSkin(resourceLocation);
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
            //Texture is most likely not downloaded yet.
            lastEntityTextureHash = DefaultPlayerSkin.getDefaultSkinLegacy().hashCode();
            bufferedEntityImage = SkinHelper.getBufferedImageSkin(DefaultPlayerSkin.getDefaultSkinLegacy());
            if (bufferedEntityImage != null & !loading) {
                loading = true;
                needsUpdate = true;
            }
        }
    }
    
    public void updateExtraColours(ExtraColours extraColours) {
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
        //TODO check if the skins have a texture.
        applyPlayerToTexture();
        applySkinsToTexture();
        createReplacmentTexture();
    }
    
    private void applyPlayerToTexture() {
        bufferedEntitySkinnedImage = SkinHelper.deepCopyBufferedImage(bufferedEntityImage);
    }
    
    private void paintTexture(BufferedImage texture, int x, int y, int rgb) {
        texture.setRGB(x, y, rgb);
        // Paint left leg.
        if (x < 16 & y >=16 & y < 32) {
            texture.setRGB(x + 16, y + 32, rgb);
        }
        
        // Paint right arm.
        if (x >= 40 & x < 56 & y >=16 & y < 32) {
            texture.setRGB(x - 8, y + 32, rgb);
        }
    }
    
    private void applySkinsToTexture() {
        for (int i = 0; i < skins.length; i++) {
            Skin skin = skins[i];
            if (skin != null && skin.hasPaintData()) {
                for (int ix = 0; ix < TEXURE_REPLACMENT_WIDTH; ix++) {
                    for (int iy = 0; iy < TEXURE_REPLACMENT_HEIGHT; iy++) {
                        int paintColour = skin.getPaintData()[ix + (iy * textureWidth)];
                        PaintType paintType = PaintRegistry.getPaintTypeFromColour(paintColour);
                        
                        if (paintType == PaintRegistry.PAINT_TYPE_NORMAL) {
                            paintTexture(bufferedEntitySkinnedImage, ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                        } else if (paintType.getId() >= 1 && paintType.getId() <= 8) {
                            int dyeNumber = paintType.getId() - 1;
                            if (dyes != null && dyes[i] != null && dyes[i].haveDyeInSlot(dyeNumber)) {
                                byte[] dye = dyes[i].getDyeColour(dyeNumber);
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
        }
        
        for (int i = 0; i < skins.length; i++) {
            Skin skin = skins[i];
            if (skin!= null && SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skin.getProperties())) {
                for (int j = 0; j < skin.getPartCount(); j++) {
                    SkinPart skinPart = skin.getParts().get(j);
                    if (skinPart.getPartType() instanceof ISkinPartTypeTextured) {
                        ISkinPartTypeTextured typeTextured = (ISkinPartTypeTextured) skinPart.getPartType();
                        Point texLoc = typeTextured.getTextureLocation();
                        IPoint3D texSize = typeTextured.getTextureModelSize();
                        
                        

                        
                        for (int ix = 0; ix < texSize.getZ() * 2 + texSize.getX() * 2; ix++) {
                            for (int iy = 0; iy < texSize.getZ() + texSize.getY(); iy++) {
                                if (skin.getSkinType() == SkinTypeRegistry.skinLegs) {
                                    if (iy >= 12) {
                                        continue;
                                    }
                                    if (iy < 4 & ix > 7 & ix < 12) {
                                        continue;
                                    }
                                }
                                if (skin.getSkinType() == SkinTypeRegistry.skinFeet) {
                                    if (iy < 12) {
                                        if (!(iy < 4 & ix > 7 & ix < 12)) {
                                            continue;
                                        }
                                        
                                    }
                                }
                                // Make the players texture blank.
                                paintTexture(bufferedEntitySkinnedImage, (int)texLoc.getX() + ix, (int)texLoc.getY() + iy, 0x00FFFFFF);
                            }
                        }
                        
                        
                        
                        
                    }
                }
            }
        }
    }
    
    private int dyeColour(int dye, int colour, int dyeIndex, Skin skin) {
        byte[] dyeArray = new byte[3];
        dyeArray[0] = (byte) (dye >>> 16 & 0xFF);
        dyeArray[1] = (byte) (dye >>> 8 & 0xFF);
        dyeArray[2] = (byte) (dye & 0xFF);
        return  dyeColour(dyeArray, colour, dyeIndex, skin);
    }
    
    private int dyeColour(byte[] dye, int colour, int dyeIndex, Skin skin) {
        byte r = (byte) (colour >>> 16 & 0xFF);
        byte g = (byte) (colour >>> 8 & 0xFF);
        byte b = (byte) (colour & 0xFF);
        
        if (dye.length > 3) {
            PaintType t = PaintRegistry.getPaintTypeFormByte(dye[3]);
            if (t.getColourType() != null) {
                dye = lastEntityColours.getColourBytes(t.getColourType());
            }
        }
        
        int[] average = {127, 127, 127};
        
        if (skin != null) {
            average = skin.getAverageDyeColour(dyeIndex);
        }
        dye = ColouredFace.dyeColour(r, g, b, dye, average);
        
        return (255 << 24) + ((dye[0] & 0xFF) << 16) + ((dye[1] & 0xFF) << 8) + (dye[2]  & 0xFF);
    }
    
    @Override
    protected void finalize() throws Throwable {
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        if (replacementTexture != null) {
            renderEngine.deleteTexture(replacementTexture);
        }
        super.finalize();
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
