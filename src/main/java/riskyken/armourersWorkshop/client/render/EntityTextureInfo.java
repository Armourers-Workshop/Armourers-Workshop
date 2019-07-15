package riskyken.armourersWorkshop.client.render;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.BitwiseUtils;

public class EntityTextureInfo {
    
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
    /** Is this texture still loading? */
    private boolean loading;
    
    public EntityTextureInfo() {
        this(64, 32);
    }
    
    public EntityTextureInfo(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        lastEntityTextureHash = -1;
        lastSkinHashs = new int[5 * ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE];
        lastDyeHashs = new int[5 * ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE];
        normalTexture = null;
        replacementTexture = null;
        for (int i = 0; i < lastSkinHashs.length; i++) {
            lastSkinHashs[i] = -1;
        }
        for (int i = 0; i < lastDyeHashs.length; i++) {
            lastDyeHashs[i] = -1;
        }
        lastEntitySkinColour = -1;
        lastEntityHairColour = -1;
        bufferedEntitySkinnedImage = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
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
            lastEntityTextureHash = AbstractClientPlayer.locationStevePng.hashCode();
            bufferedEntityImage = SkinHelper.getBufferedImageSkin(AbstractClientPlayer.locationStevePng);
            if (bufferedEntityImage != null & !loading) {
                loading = true;
                needsUpdate = true;
            }
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
        for (int ix = 0; ix < textureWidth; ix++) {
            for (int iy = 0; iy < textureHeight; iy++) {
                if (bufferedEntityImage == null) {
                    break;
                }
                bufferedEntitySkinnedImage.setRGB(ix, iy, bufferedEntityImage.getRGB(ix, iy));
            }
        }
    }
    
    private void applySkinsToTexture() {
        for (int i = 0; i < skins.length; i++) {
            Skin skin = skins[i];
            if (skin != null && skin.hasPaintData()) {
                for (int ix = 0; ix < textureWidth; ix++) {
                    for (int iy = 0; iy < textureHeight; iy++) {
                        
                        int paintColour = skin.getPaintData()[ix + (iy * textureWidth)];
                        PaintType paintType = PaintType.getPaintTypeFromColour(paintColour);
                        
                        if (paintType == PaintType.NORMAL) {
                            bufferedEntitySkinnedImage.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                        }
                        if (paintType == PaintType.HAIR) {
                            int colour = dyeColour(lastEntityHairColour, paintColour, 9, skin);
                            bufferedEntitySkinnedImage.setRGB(ix, iy, colour);
                        }
                        if (paintType == PaintType.SKIN) {
                            int colour = dyeColour(lastEntitySkinColour, paintColour, 8, skin);
                            bufferedEntitySkinnedImage.setRGB(ix, iy, colour);
                        }
                        if (paintType.getKey() >= 1 && paintType.getKey() <= 8) {
                            int dyeNumber = paintType.getKey() - 1;
                            if (dyes != null && dyes[i] != null && dyes[i].haveDyeInSlot(dyeNumber)) {
                                byte[] dye = dyes[i].getDyeColour(dyeNumber);
                                int colour = dyeColour(dye, paintColour, dyeNumber, skin);
                                bufferedEntitySkinnedImage.setRGB(ix, iy, colour);
                            } else {
                                bufferedEntitySkinnedImage.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                            }
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
                                bufferedEntitySkinnedImage.setRGB((int)texLoc.getX() + ix, (int)texLoc.getY() + iy, 0x00FFFFFF);
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
            byte t = dye[3];
            if ((t & 0xFF) == PaintType.HAIR.getKey()) {
                byte dyeR = (byte) (lastEntityHairColour >>> 16 & 0xFF);
                byte dyeG = (byte) (lastEntityHairColour >>> 8 & 0xFF);
                byte dyeB = (byte) (lastEntityHairColour & 0xFF);
                dye = new byte[] {dyeR, dyeG, dyeB};
            }
            if ((t & 0xFF) == PaintType.SKIN.getKey()) {
                byte dyeR = (byte) (lastEntitySkinColour >>> 16 & 0xFF);
                byte dyeG = (byte) (lastEntitySkinColour >>> 8 & 0xFF);
                byte dyeB = (byte) (lastEntitySkinColour & 0xFF);
                dye = new byte[] {dyeR, dyeG, dyeB};
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
