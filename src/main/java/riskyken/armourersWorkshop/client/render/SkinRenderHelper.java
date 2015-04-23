package riskyken.armourersWorkshop.client.render;

import javax.vecmath.Point3i;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class SkinRenderHelper {
    
    private static final ResourceLocation guideImage = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/blocks/guide.png");
    
    public static void renderBuildingGuide(ISkinType skinType, float scale, boolean showSkinOverlay, boolean showHelper) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPart = skinType.getSkinParts().get(i);
            Point3i partOffset = skinPart.getOffset();
            GL11.glTranslated(partOffset.x * scale, partOffset.y * scale, partOffset.z * scale);
            skinPart.renderBuildingGuide(scale, showSkinOverlay, showHelper);
            GL11.glTranslated(-partOffset.x * scale, -partOffset.y * scale, -partOffset.z * scale);
        }
    }
    
    public static void renderBuildingGrid(ISkinType skinType, float scale) {
        for (int i = 0; i < skinType.getSkinParts().size(); i++) {
            ISkinPartType skinPartType = skinType.getSkinParts().get(i);
            Point3i partOffset = skinPartType.getOffset();
            GL11.glTranslated(partOffset.x * scale, partOffset.y * scale, partOffset.z * scale);
            renderBuildingGrid(skinPartType, scale);
            GL11.glTranslated(-partOffset.x * scale, -partOffset.y * scale, -partOffset.z * scale);
        }
    }
    
    private static void renderBuildingGrid(ISkinPartType skinPartType, float scale) {
        GL11.glTranslated(0, skinPartType.getBuildingSpace().y * scale, 0);
        GL11.glScalef(-1, -1, 1);
        SkinRenderHelper.renderGuidePart(skinPartType, scale);
        GL11.glScalef(-1, -1, 1);
        GL11.glTranslated(0, -skinPartType.getBuildingSpace().y * scale, 0);
    }
    
    public static void renderGuidePart(ISkinPartType part, float scale) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(guideImage);
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glPushMatrix();
        
        boolean debugRender = false;
        
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
        ModRenderHelper.enableAlphaBlend();
        
        
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
        
        ModRenderHelper.disableAlphaBlend();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}
