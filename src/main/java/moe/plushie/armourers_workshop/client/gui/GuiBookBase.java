package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import moe.plushie.armourers_workshop.client.gui.controls.GuiBookButton;
import moe.plushie.armourers_workshop.client.guidebook.BookPage;
import moe.plushie.armourers_workshop.client.guidebook.BookPageBase;
import moe.plushie.armourers_workshop.client.guidebook.IBook;
import moe.plushie.armourers_workshop.client.guidebook.IBookPage;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiBookBase extends GuiScreen {
    
    protected static final ResourceLocation bookTexture = new ResourceLocation(LibGuiResources.GUI_GUIDE_BOOK);
    
    protected final int guiWidth;
    protected final int guiHeight;
    protected int guiLeft;
    protected int guiTop;
    
    protected final IBook book;
    
    private int pagePanelLeft;
    private int pagePanelRight;
    
    private GuiBookButton buttonBack;
    private GuiBookButton buttonForward;
    
    public GuiBookBase(IBook book, int guiWidth, int guiHeight) {
        this.book = book;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        pagePanelLeft = 1;
        pagePanelRight = 2;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        
        buttonList.clear();
        
        buttonBack = new GuiBookButton(0, this.guiLeft - 20, this.guiTop + 156, 3, 207, bookTexture);
        buttonForward = new GuiBookButton(1, this.guiLeft + 258, this.guiTop + 156, 3, 194, bookTexture);
        
        buttonList.add(buttonBack);
        buttonList.add(buttonForward);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            startPageTurnRight();
        }
        if (button.id == 1) {
            startPageTurnLeft();
        }
        
        if (button.id > 1) {
            //setGoToChapter(button.id - 2);
        }
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) throws IOException {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.player.closeScreen();
        }
    }
    
    private void startPageTurnRight() {
        pagePanelLeft -= 2;
        pagePanelRight -= 2;
    }
    
    private void startPageTurnLeft() {
        pagePanelLeft += 2;
        pagePanelRight += 2;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partickTime) {
        super.drawScreen(mouseX, mouseY, partickTime);
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(bookTexture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.guiWidth, this.guiHeight);
        buttonBack.visible = pagePanelLeft != 1;
        buttonForward.visible = pagePanelRight < book.getTotalNumberOfPages();
        renderPageText(pagePanelLeft, 0, mouseX, mouseY, false);
        renderPageText(pagePanelRight, BookPage.PAGE_TEXTURE_WIDTH, mouseX, mouseY, false);
        //renderTurningPage();
    }
    
    private void renderPageText(int page, int left, int mouseX, int mouseY, boolean turning) {
        if (page < 1 | page > book.getTotalNumberOfPages()) {
            return;
        }
        
        IBookPage bookPage = book.getPageNumber(page);
        if (bookPage != null) {
            GL11.glPushMatrix();
            int xOffset = guiLeft + left + BookPageBase.PAGE_MARGIN_LEFT;
            int yOffset = guiTop + BookPageBase.PAGE_MARGIN_TOP;
            //yOffset -= 50;
            GL11.glTranslatef(xOffset, yOffset, 0);
            bookPage.renderPage(fontRenderer, mouseX, mouseY, turning, page);
            GL11.glPopMatrix();
        } else {
            ModLogger.log("page was null");
        }
    }
    
    float turnAmount = 0F;
    int turningPageNumber = 0;
    
    private void renderTurningPage() {
        turningPageNumber = pagePanelLeft;
        if (turningPageNumber < 1 | turningPageNumber > book.getTotalNumberOfPages()) {
            return;
        }
        IBookPage bookPage = book.getPageNumber(turningPageNumber);
        if (bookPage != null) {
            //ModRenderHelper.disableAlphaBlend();
            ModRenderHelper.disableAlphaBlend();
            enablePageFramebuffer();
            ModRenderHelper.enableAlphaBlend(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //ModRenderHelper.enableAlphaBlend(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //ModRenderHelper.enableAlphaBlend();
            //GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            //GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            bookPage.renderPage(fontRenderer, 0, 0, true, turningPageNumber);
            //ModRenderHelper.disableAlphaBlend();
            disablePageFramebuffer();
            
            bindFramebufferTexture();
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glColorMask(true, true, true, false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_LIGHTING);
            //GL11.glDisable(GL11.GL_ALPHA_TEST);
            //GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            ModRenderHelper.enableAlphaBlend();
            //ModRenderHelper.enableAlphaBlend();
            //GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            int xOffset = guiLeft + BookPageBase.PAGE_MARGIN_LEFT;
            int yOffset = guiTop + BookPageBase.PAGE_MARGIN_TOP;
            //drawFboRec(xOffset, yOffset, 256, 256);
            ModRenderHelper.disableAlphaBlend();
            unbindFramebufferTexture();
        }
    }
    
    private static Framebuffer fbo;
    
    protected void enablePageFramebuffer() {
        mc.getFramebuffer().unbindFramebuffer();
        
        ScaledResolution reso = new ScaledResolution(mc);
        
        double scaleWidth = mc.displayWidth / reso.getScaledWidth_double();
        double scaleHeight = mc.displayHeight / reso.getScaledHeight_double();
        
        int fboScaledWidth = MathHelper.ceil(256 * scaleWidth);
        int fboScaledHeight = MathHelper.ceil(256 * scaleHeight);
        
        if (fbo == null) {
            fbo = new Framebuffer(fboScaledWidth, fboScaledHeight, true);
            fbo.setFramebufferColor(0, 0, 0, 0);
        }
        
        if (fbo.framebufferWidth != fboScaledWidth | fbo.framebufferHeight != fboScaledHeight) {
            fbo.createFramebuffer(fboScaledWidth, fboScaledHeight);
            ModLogger.log("resizing fbo to scale: " + scaleHeight);
        }
        
        //OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, fbo.framebufferObject);
        
        GL11.glClearColor(0F, 0F, 0F, 0F);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearDepth(1.0D);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0D, 256D, 256D, 0D, 1000D, 3000.0D);
        GL11.glViewport(0, 0, fboScaledWidth, fboScaledHeight);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glClearColor(0, 0, 0, 0);
        ModRenderHelper.enableAlphaBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    protected void disablePageFramebuffer() {
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        ModRenderHelper.disableAlphaBlend();
        mc.getFramebuffer().bindFramebuffer(true);
    }
    
    protected void bindFramebufferTexture() {
        fbo.bindFramebufferTexture();
    }
    
    protected void unbindFramebufferTexture() {
        fbo.unbindFramebufferTexture();
    }
    /*
    private void drawFboRec(int x, int y, int width, int height) {
        double zLevel = 1D;
        Tessellator tess = new Tessellator().instance;
        //ModRenderHelper.enableAlphaBlend();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //GL11.glColorMask(true, true, true, false);
        //OpenGlHelper.glBlendFunc(0, 0, 0, 0);
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1F, 1F, 1F, 1F);
        tess.setColorOpaque_I(-1);
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
    */
}
