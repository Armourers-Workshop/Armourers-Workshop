package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiBookButton;
import riskyken.armourersWorkshop.client.guidebook.BookPage;
import riskyken.armourersWorkshop.client.guidebook.IBook;
import riskyken.armourersWorkshop.client.guidebook.IBookPage;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiBookBase extends GuiScreen {
    
    protected static final ResourceLocation bookTexture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/guideBook.png");
    
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
    protected void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
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
        buttonForward.visible = pagePanelRight <= book.getTotalNumberOfPages();
        renderPageText(pagePanelLeft, 0, mouseX, mouseY, false);
        renderPageText(pagePanelRight, BookPage.PAGE_TEXTURE_WIDTH, mouseX, mouseY, false);
    }
    
    private void renderPageText(int page, int left, int mouseX, int mouseY, boolean turning) {
        if (page < 1 | page > book.getTotalNumberOfPages()) {
            return;
        }
        
        IBookPage bookPage = book.getPageNumber(page);
        if (bookPage != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(guiLeft + left, guiTop, 0);
            bookPage.renderPage(fontRendererObj, mouseX, mouseY, turning, page);
            GL11.glPopMatrix();
        } else {
            ModLogger.log("page was null");
        }
    }
    
    private void renderStringCenter(String text, int x, int y) {
        int xCenter = 104 / 2 - fontRendererObj.getStringWidth(text) / 2;
        fontRendererObj.drawString(text, x + xCenter, y, UtilColour.getMinecraftColor(7));
    }
    
    private static Framebuffer fbo;
    
    protected void enablePageFramebuffer() {
        ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        
        double scaleWidth = (double)mc.displayWidth / reso.getScaledWidth_double();
        double scaleHeight = (double)mc.displayHeight / reso.getScaledHeight_double();
        
        int fboScaledWidth = MathHelper.ceiling_double_int(256 * scaleWidth);
        int fboScaledHeight = MathHelper.ceiling_double_int(256 * scaleHeight);
        
        if (fbo == null) {
            fbo = new Framebuffer(fboScaledWidth, fboScaledHeight, false);
        }
        
        if (fbo.framebufferWidth != fboScaledWidth | fbo.framebufferHeight != fboScaledHeight) {
            fbo.createFramebuffer(fboScaledWidth, fboScaledHeight);
            ModLogger.log("resizing fbo to scale: " + scaleHeight);
        }
        
        OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, fbo.framebufferObject);
        GL11.glClear(16384);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0D, 256D, 256D, 0D, 1000D, 3000.0D);
        GL11.glViewport(0, 0, fboScaledWidth, fboScaledHeight);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }
    
    protected void disablePageFramebuffer() {
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        mc.getFramebuffer().bindFramebuffer(true);
    }
    
    protected void bindFramebufferTexture() {
        fbo.bindFramebufferTexture();
    }
    
    protected void unbindFramebufferTexture() {
        fbo.unbindFramebufferTexture();
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
