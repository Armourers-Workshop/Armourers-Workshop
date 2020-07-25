package moe.plushie.armourers_workshop.client.guidebook;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BookPageBase implements IBookPage {
    
    protected static final ResourceLocation bookPageTexture = new ResourceLocation(LibGuiResources.GUI_GUIDE_BOOK_PAGES);
    
    protected static final int TEXT_COLOUR = 0xFF000000;
    
    public static final int PAGE_TEXTURE_WIDTH = 118;
    public static final int PAGE_TEXTURE_HEIGHT = 165;
    
    public static final int PAGE_MARGIN_LEFT = 10;
    public static final int PAGE_MARGIN_TOP = 7;
    
    protected static final int PAGE_PADDING_LEFT = 5;
    protected static final int PAGE_PADDING_TOP = 5;
    
    protected final IBook parentBook;
    
    public BookPageBase(IBook parentBook) {
        this.parentBook = parentBook;
    }
    
    protected void renderStringCenter(FontRenderer fontRenderer, String text, int y) {
        int contentWidth = PAGE_TEXTURE_WIDTH / 2;
        int stringWidth = fontRenderer.getStringWidth(text) / 2;
        
        int xCenter = 104 / 2 - fontRenderer.getStringWidth(text) / 2;
        fontRenderer.drawSplitString(text, contentWidth - stringWidth,
                y, PAGE_TEXTURE_WIDTH, 0xFF2A2A2A);
    }
    
    protected void drawPageTitleAndNumber(FontRenderer fontRenderer, int pageNumber) {
        String chapterTitle = parentBook.getChapterFromPageNumber(pageNumber).getUnlocalizedName();
        chapterTitle = I18n.format(chapterTitle + ".name");
        
        //Title
        renderStringCenter(fontRenderer, chapterTitle, PAGE_PADDING_TOP);
        
        //Page number
        renderStringCenter(fontRenderer, pageNumber + " - " + parentBook.getTotalNumberOfPages(), PAGE_TEXTURE_HEIGHT - PAGE_PADDING_TOP - fontRenderer.FONT_HEIGHT);
    }
    
    protected void drawTestRec(int x, int y, int width, int height) {
        double zLevel = 0D;
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        /*
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
        */
    }
    
    protected void renderTestRec(int x, int y, int width, int height, float r, float g, float b) {
        double zLevel = 0D;
        GL11.glEnable(GL11.GL_BLEND);
        //GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        /*
        Tessellator tess = new Tessellator().instance;
        tess.startDrawingQuads();
        tess.setColorRGBA_F(r, g, b, 1F);
        //Bottom Left
        tess.addVertex(x, y + height, zLevel);
        //Bottom Right
        tess.addVertex(x + width, y + height, zLevel);
        //Top Right
        tess.addVertex(x + width, y, zLevel);
        //Top Left
        tess.addVertex(x, y, zLevel);
        tess.draw();
        */
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
    /*
    protected void drawTexturedRec(int x, int y, int u, int v, int width, int height) {
        double zLevel = 0D;
        float textureFraction = 0.00390625F;
        Tessellator tess = new Tessellator().instance;
        tess.startDrawingQuads();
        tess.setColorRGBA_F(1F, 1F, 1F, 1F);
        tess.addVertexWithUV(x, y + height, zLevel, 0, 1);
        tess.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tess.addVertexWithUV(x + width, y, zLevel, 1, 0);
        tess.addVertexWithUV(x, y, zLevel, 0, 0);
        tess.draw();
    }
    
    protected void drawTexturedRec(int x, int y, int width, int height) {
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
    }*/
}
