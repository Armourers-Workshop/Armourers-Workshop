package riskyken.armourersWorkshop.client.render;

import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;

public class EntityTextureInfo {
    
    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 32;
    public static final int TEXTURE_SIZE = TEXTURE_WIDTH * TEXTURE_HEIGHT;
    
    /** The last texture entity had when the replacement texture was made. */
    private int lastEntityTextureHash;
    /** The last skin hashs the entity had when the replacement texture was made. */
    private int[] lastSkinHashs;
    /** Skins that the entity has equipped. */
    private Skin[] skins;
    /** The entities normal texture. */
    private ResourceLocation normalTexture;
    /** The entities replacement texture. */
    private ResourceLocation replacementTexture;
    /** The last skin colour the entity had when the replacement texture was made. */
    private int lastEntitySkinColour;
    /** The last hair colour the entity had when the replacement texture was made. */
    private int lastEntityHairColour;
    /** A buffered image of the entity texture. */
    private BufferedImage bufferedEntityImage;
    /** A buffered image of the entity replacement texture. */
    private BufferedImage bufferedEntitySkinnedImage;
    /** Does the texture need to be remade? */
    private boolean needsUpdate;
    
    public EntityTextureInfo() {
        lastEntityTextureHash = -1;
        lastSkinHashs = new int[4];
        normalTexture = null;
        replacementTexture = null;
        for (int i = 0; i < lastSkinHashs.length; i++) {
            lastSkinHashs[i] = -1;
        }
        lastEntitySkinColour = -1;
        lastEntityHairColour = -1;
        bufferedEntitySkinnedImage = new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        needsUpdate = true;
    }
    
    
    public void updateTexture(ResourceLocation resourceLocation) {
        if (lastEntityTextureHash != resourceLocation.hashCode()) {
            lastEntityTextureHash = resourceLocation.hashCode();
            normalTexture = resourceLocation;
            bufferedEntityImage = SkinHelper.getBufferedImageSkin(resourceLocation);
            needsUpdate = true;
        }
    }
    
    public void updateSkinColour(int colour) {
        if (lastEntitySkinColour != colour) {
            lastEntitySkinColour = colour;
            needsUpdate = true;
        }
    }
    
    public void updateHairColour(int colour) {
        if (lastEntityHairColour != colour) {
            lastEntityHairColour = colour;
            needsUpdate = true;
        }
    }
    
    public void updateSkins(Skin[] skins) {
        this.skins = skins;
        for (int i = 0; i < 4; i++) {
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
    
    public void checkTexture() {
        if (needsUpdate) {
            buildTexture();
            ModLogger.log("texture build needed");
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
        for (int ix = 0; ix < TEXTURE_WIDTH; ix++) {
            for (int iy = 0; iy < TEXTURE_HEIGHT; iy++) {
                if (bufferedEntityImage == null) {
                    ModLogger.log("null entity image");
                    break;
                }
                bufferedEntitySkinnedImage.setRGB(ix, iy, bufferedEntityImage.getRGB(ix, iy));
            }
        }
    }
    
    private void applySkinsToTexture() {
        for (int i = 0; i < skins.length; i++) {
            if (skins[i] != null && skins[i].hasPaintData()) {
                for (int ix = 0; ix < TEXTURE_WIDTH; ix++) {
                    for (int iy = 0; iy < TEXTURE_HEIGHT; iy++) {
                        int paintColour = skins[i].getPaintData()[ix + (iy * TEXTURE_WIDTH)];
                        if (paintColour >>> 24 == 255) {
                            bufferedEntitySkinnedImage.setRGB(ix, iy, skins[i].getPaintData()[ix + (iy * TEXTURE_WIDTH)]);
                        } else if (paintColour >>> 24 == 254) {
                            bufferedEntitySkinnedImage.setRGB(ix, iy, lastEntitySkinColour);
                        } else if (paintColour >>> 24 == 253) {
                            bufferedEntitySkinnedImage.setRGB(ix, iy, lastEntityHairColour);
                        }
                    }
                }
            }
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
    
    public ResourceLocation postRender() {
        return normalTexture;
    }
    
    private class SkinTextureObject implements ITextureObject {
        
        private int textureId;
        private final BufferedImage texture;
        
        public SkinTextureObject(BufferedImage texture) {
            this.texture = texture;
        }
        
        @Override
        public void loadTexture(IResourceManager resourceManager) throws IOException {
            textureId = TextureUtil.glGenTextures();
            TextureUtil.uploadTextureImage(textureId, texture);
        }

        @Override
        public int getGlTextureId() {
            return this.textureId;
        }
    }
}
