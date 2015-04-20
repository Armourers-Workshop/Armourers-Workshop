package riskyken.armourersWorkshop.client.render.block;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelBlockArmourer;
import riskyken.armourersWorkshop.common.Rectangle3D;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMiniArmourer extends TileEntitySpecialRenderer {
    
    private static final ResourceLocation guideImage = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/blocks/guide.png");
    private static final ModelBlockArmourer modelArmourer = new ModelBlockArmourer();
    
    public void renderTileEntityAt(TileEntityMiniArmourer tileEntity, double x, double y, double z, float tickTime) {
        float scale = 0.0625F;
        
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
        GL11.glScalef(-1, -1, 1);
        
        modelArmourer.render(tileEntity, tickTime, scale);
        
        GL11.glTranslated(0D, -0.5D, 0D);
        
        ISkinType skinType = tileEntity.getSkinType();
        if (skinType != null) {
            float rotation = (float)((double)System.currentTimeMillis() / 25 % 360);
            GL11.glRotatef(rotation, 0F, 1F, 0F);
            bindTexture(AbstractClientPlayer.locationStevePng);
            skinType.renderBuildingGuide(scale, true, false);
            
            //skinType.renderBuildingGrid(scale);
        }
        
        GL11.glPopMatrix();
    }
    
    public static void renderGuide(ISkinType type, float scale) {
        
        int heightOffset = 1;
        
        ArrayList<ISkinPart> skinParts = type.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            ISkinPart skinPart = skinParts.get(i);
            renderGuidePart(skinPart, scale);
        }
    }
    
    public static void renderGuidePart(ISkinPart part, float scale) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(guideImage);
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glPushMatrix();
        
        boolean debugRender = true;
        
        Rectangle3D buildRec = part.getBuildingSpace();
        Rectangle3D guideRec = part.getGuideSpace();
        
        GL11.glDisable(GL11.GL_LIGHTING);
        
        GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.5F);
        renderGuideBox(buildRec.x, buildRec.y, buildRec.z, buildRec.width, buildRec.height, buildRec.depth, scale);
        
        if (debugRender) {
            GL11.glColor4f(1F, 0F, 0F, 0.5F);
            renderGuideBox(guideRec.x, guideRec.y, guideRec.z, guideRec.width, guideRec.height, guideRec.depth, scale);
            
            GL11.glColor4f(0F, 1F, 0F, 0.5F);
            renderGuideBox(-0.5F, -0.5F, -0.5F, 1, 1, 1, scale);
        }

        
        GL11.glColor4f(1F, 1F, 1F, 1F);
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
    
    private static void renderGuideBox(double x, double y, double z, int width, int height, int depth, float scale) {
        renderGuideFace(ForgeDirection.DOWN, x, y, z, width, depth, scale);
        renderGuideFace(ForgeDirection.UP, x, y + height, z, width, depth, scale);
        renderGuideFace(ForgeDirection.EAST, x + width, y, z, depth, height, scale);
        renderGuideFace(ForgeDirection.WEST, x, y, z, depth, height, scale);
        renderGuideFace(ForgeDirection.NORTH, x, y, z, width, height, scale);
        renderGuideFace(ForgeDirection.SOUTH, x, y, z + depth, width, height, scale);
    }
    
    private static void renderGuideFace(ForgeDirection dir, double x, double y, double z, double sizeX, double sizeY, float scale) {
        RenderManager renderManager = RenderManager.instance;
        Tessellator tessellator = Tessellator.instance;
        
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        
        float scale1 = 0.999F;
        //GL11.glScalef(scale1, scale1, scale1);
        GL11.glTranslated(x * scale, y * scale, z * scale);
        
        
        
        
        switch (dir) {
        case EAST:
            GL11.glRotated(-90, 0, 1, 0);
            break;
        case WEST:
            GL11.glRotated(-90, 0, 1, 0);
            break;
        case UP:
            GL11.glRotated(90, 1, 0, 0);
            break;
        case DOWN:
            GL11.glRotated(90, 1, 0, 0);
            break;
        default:
            break;
        }
        
        tessellator.setBrightness(15728880);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, 0, 0, 0, 0);
        tessellator.addVertexWithUV(0, sizeY * scale, 0, sizeY, 0);
        tessellator.addVertexWithUV(sizeX * scale, sizeY * scale, 0, sizeY, sizeX);
        tessellator.addVertexWithUV(sizeX * scale, 0, 0, 0, sizeX);
        tessellator.draw();
        
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        renderTileEntityAt((TileEntityMiniArmourer)tileEntity, x, y, z, tickTime);
    }
}
