package riskyken.armourersWorkshop.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookPage implements IBookPage {
    
    private static final ResourceLocation bookPageTexture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/guideBookPage.png");
    private static final int TEXT_COLOUR = 0xFF000000;
    
    public static final int PAGE_WIDTH = 104;
    public static final int PAGE_HEIGHT = 130;
    private final ArrayList<String> lines;
    
    public BookPage(ArrayList<String> lines) {
        this.lines = lines;
    }
    
    public List<String> getLines() {
        return lines;
    }

    private static Framebuffer fbo;
    
    @Override
    public void renderPage(FontRenderer fontRenderer, int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        if (fbo == null) {
            fbo = new Framebuffer(512, 512, false);
        }
        int currentFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        
        
        ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        
        double scaleWidth = (double)mc.displayWidth / reso.getScaledWidth_double();
        double scaleHeight = (double)mc.displayHeight / reso.getScaledHeight_double();
        //ModLogger.log("Start FBO:" + GL30.GL_FRAMEBUFFER_BINDING);
        
        //fbo.createFramebuffer(100, 100);
        RenderItem itemRender = new RenderItem();
        ItemStack stack = new ItemStack(Blocks.stone);
        
        //fbo.createFramebuffer(256, 256);
        //fbo.createFramebuffer(512, 512);
        
        OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, fbo.framebufferObject);
        GL11.glClear(16384);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0D, 256D, 256D, 0D, 1000D, 3000.0D);
        GL11.glViewport(0, 0, 512, 512);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawRec(0, 0, 256, 256);
        
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        itemRender.renderItemIntoGUI(fontRenderer, mc.getTextureManager(), stack, 128, 128);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        
        Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());
        drawTexRec(0, 10, 128, 64);
        
        for (int i = 0; i < lines.size(); i++) {
            fontRenderer.drawString(lines.get(i), 0, 0 + i * 9, TEXT_COLOUR);
        }
        
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        //GL11.glOrtho(0.0D, mc.displayWidth, mc.displayHeight, 0D, 0D, 1.0D);
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
        
        OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, currentFBO);
        
        //Draw FBO
        GL11.glPushMatrix();
        float rotation = (float)((double)System.currentTimeMillis() / 25 % 360);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslatef(0, 128, 0);
        GL11.glRotatef(rotation, 1, 0, 0);
        GL11.glTranslatef(0, -128, 0);
        fbo.bindFramebufferTexture();
        drawFboRec(reso.getScaledWidth() - 256, 0, 256, 256);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        
        GL11.glPopMatrix();
        
        drawRec(0, 0, 256, 256);
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(1, 1, 1);
        itemRender.renderItemIntoGUI(fontRenderer, mc.getTextureManager(), stack, 128, 128);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        
        mc.renderEngine.bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());
        drawTexRec(0, 10, 128, 64);
        
        for (int i = 0; i < lines.size(); i++) {
            fontRenderer.drawString(lines.get(i), 0, 0 + i * 9, TEXT_COLOUR);
        }
        
        //ModLogger.log("End FBO:" + GL30.GL_FRAMEBUFFER_BINDING);
    }
    
    private void drawRec(int x, int y, int width, int height) {
        double zLevel = 0D;
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Tessellator tess = new Tessellator().instance;
        tess.startDrawingQuads();
        
        //Bottom Left
        tess.setColorRGBA_F(0F, 1F, 0F, 1F);
        tess.addVertex(x, y + height, zLevel);
        
        //Bottom Right
        tess.setColorRGBA_F(0F, 0F, 1F, 1F);
        tess.addVertex(x + width, y + height, zLevel);
        
        
        //Top Right
        tess.setColorRGBA_F(1F, 0F, 0F, 1F);
        tess.addVertex(x + width, y, zLevel);
        
        //Top Left
        tess.setColorRGBA_F(1F, 1F, 0F, 1F);
        tess.addVertex(x, y, zLevel);
        
        tess.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    private void drawTexRec(int x, int y, int width, int height) {
        double zLevel = 0D;
        Tessellator tess = new Tessellator().instance;
        ModRenderHelper.enableAlphaBlend();
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1F, 1F, 1F, 1F);
        //Bottom Left
        tess.addVertexWithUV(x, y + height, zLevel, 0, 1);
        //Bottom Right
        tess.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        //Top Right
        tess.addVertexWithUV(x + width, y, zLevel, 1, 0);
        //Top Left
        tess.addVertexWithUV(x, y, zLevel, 0, 0);
        tess.draw();
    }
    
    private void drawFboRec(int x, int y, int width, int height) {
        double zLevel = 1D;
        Tessellator tess = new Tessellator().instance;
        ModRenderHelper.enableAlphaBlend();
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1F, 1F, 1F, 1F);
        
        //Bottom Left
        tess.addVertexWithUV(x, y + height, zLevel, 0, 0);
        //Bottom Right
        tess.addVertexWithUV(x + width, y + height, zLevel, 1, 0);
        //Top Right
        tess.addVertexWithUV(x + width, y, zLevel, 1, 1);
        //Top Left
        tess.addVertexWithUV(x, y, zLevel, 0, 1);
        
        tess.draw();
    }
}
