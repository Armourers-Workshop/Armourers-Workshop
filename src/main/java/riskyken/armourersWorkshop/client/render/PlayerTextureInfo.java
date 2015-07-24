package riskyken.armourersWorkshop.client.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.skin.PlayerEquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.ModLogger;

public class PlayerTextureInfo {
    
    private PlayerEquipmentWardrobeData equipmentWardrobeData;
    
    /** The last texture player had when the replacement texture was made. */
    private int lastPlayerTextureHash = -1;
    /** The last head skin the player had when the replacement texture was made. */
    private int lastHeadSkinHash = -1;
    /** The last skin colour the player had when the replacement texture was made. */
    private int lastPlayerSkinColour = -1;
    /** The last hair colour the player had when the replacement texture was made. */
    private int lastPlayerHairColour = -1;
    /** A buffered image of the players texture. */
    private BufferedImage bufferedPlayerImage;
    /** A buffered image of the players replacement texture. */
    private BufferedImage bufferedSkinImage;
    
    public PlayerTextureInfo(PlayerEquipmentWardrobeData equipmentWardrobeData) {
        this.equipmentWardrobeData = equipmentWardrobeData;
    }
    
    public void setSkinInfo(PlayerEquipmentWardrobeData equipmentWardrobeData) {
        this.equipmentWardrobeData = equipmentWardrobeData;
    }
    
    public PlayerEquipmentWardrobeData getEquipmentWardrobeData() {
        return equipmentWardrobeData;
    }

    public int autoColourHair(AbstractClientPlayer player) {
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(player);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 3));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
    
    public int autoColourSkin(AbstractClientPlayer player) {
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(player);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 13));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
    
    public void preRender(AbstractClientPlayer player, RenderPlayer renderer) {
        //If the players texture is downloaded.
        if (player.func_152123_o()) {
            //TODO Make and set the texture from equiped skins,
            Skin skin = EquipmentModelRenderer.INSTANCE.getPlayerCustomArmour(player, SkinTypeRegistry.skinHead);
            if(skin != null && skin.hasPaintData()) {
                ModLogger.log("head paint found");
            }
            
        }
    	renderer.modelBipedMain.bipedHeadwear.isHidden = this.equipmentWardrobeData.headOverlay;
    }
    
    public void postRender(AbstractClientPlayer player, RenderPlayer renderer) {
      //If the players texture is downloaded.
        if (player.func_152123_o()) {
            //TODO Restore the plays texture.
        }
    	renderer.modelBipedMain.bipedHeadwear.isHidden = false;
    }
    
    private class SkinTextureObject implements ITextureObject {
        
        private final int textureId;
        
        public SkinTextureObject(BufferedImage texture) {
            textureId = TextureUtil.glGenTextures();
            TextureUtil.uploadTextureImage(textureId, texture);
        }
        
        @Override
        public void loadTexture(IResourceManager p_110551_1_) throws IOException {
            //NO-OP
        }

        @Override
        public int getGlTextureId() {
            return this.textureId;
        }
    }
}
