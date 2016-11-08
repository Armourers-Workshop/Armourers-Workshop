package riskyken.armourersWorkshop.client.render.tileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinRenderHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class RenderBlockArmourer extends TileEntitySpecialRenderer {
    
    public void renderTileEntityAt(TileEntityArmourer te, double x, double y, double z, float tickTime) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.mcProfiler.startSection("armourersArmourer");
        float scale = 0.0625F;
        
        ISkinType skinType = te.getSkinType();
        
        mc.mcProfiler.startSection("textureBind");
        if (te.skinTexture == null) {
            te.skinTexture = new SkinTexture();
        }
        te.skinTexture.updateGameProfile(te.getGameProfile());
        te.skinTexture.updatePaintData(te.getPaintData());
        te.skinTexture.bindTexture();
        mc.mcProfiler.endSection();
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
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
            mc.mcProfiler.startSection("modelRender");
            boolean hidden = false;
            if (skinType.getVanillaArmourSlotId() != -1) {
                if (te.getSkinProps().getPropertyBoolean(Skin.KEY_ARMOUR_OVERRIDE, false)) {
                    hidden = true;
                }
            }
            GL11.glPolygonOffset(3F, 3F);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            if (!hidden) {
                SkinRenderHelper.renderBuildingGuide(skinType, scale, te.isShowOverlay(), te.isShowHelper());
            }
            
            GL11.glPolygonOffset(-3F, -3F);
            mc.mcProfiler.endSection();
            mc.mcProfiler.startSection("renderGuideGrid");
            SkinRenderHelper.renderBuildingGrid(skinType, scale, te.isShowGuides(), hidden);
            mc.mcProfiler.endSection();
            GL11.glPolygonOffset(0F, 0F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
        
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        
        ModRenderHelper.enableLighting();
        GL11.glEnable(GL11.GL_LIGHTING);
        mc.mcProfiler.endSection();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        renderTileEntityAt((TileEntityArmourer)tileEntity, x, y, z, tickTime);
    }
}
