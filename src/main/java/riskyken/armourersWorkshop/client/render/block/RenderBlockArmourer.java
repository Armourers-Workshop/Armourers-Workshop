package riskyken.armourersWorkshop.client.render.block;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.client.render.SkinRenderHelper;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockArmourer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        float scale = 0.0625F;
        
        TileEntityArmourerBrain te = (TileEntityArmourerBrain) tileEntity;
        ISkinType skinType = te.getSkinType();
        
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        PlayerSkinInfo skinInfo = null;
        
        SkinHelper.bindPlayersNormalSkin(te.getGameProfile());
        
        GL11.glPushMatrix();
        GL11.glColor3f(0.77F, 0.77F, 0.77F);
        ModRenderHelper.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslated(x, y, z);
        
        if (te.getDirection() != null) {
            switch (te.getDirection()) {
            case EAST:
                GL11.glRotatef(270, 0, 1, 0);
                break;
            case SOUTH:
                GL11.glRotatef(180, 0, 1, 0);
                break; 
            case WEST:
                GL11.glRotatef(90, 0, 1, 0);
                break;
            default:
                break;
            }
        }
        
        GL11.glTranslated(0, te.getHeightOffset(), 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glScalef(16, 16, 16);
        
        if (skinType != null) {
            SkinRenderHelper.renderBuildingGuide(skinType, scale, te.isShowOverlay(), false);
            if (te.isShowGuides()) {
                SkinRenderHelper.renderBuildingGrid(skinType, scale);
            }
        }
        
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        
        ModRenderHelper.enableLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
