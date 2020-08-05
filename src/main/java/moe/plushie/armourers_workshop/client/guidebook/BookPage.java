package moe.plushie.armourers_workshop.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookPage extends BookPageBase {
    
    private final ArrayList<String> lines;
    
    public BookPage(IBook parentBook, ArrayList<String> lines) {
        super(parentBook);
        this.lines = lines;
    }
    
    public List<String> getLines() {
        return lines;
    }
    
    @Override
    public void renderPage(FontRenderer fontRenderer, int mouseX, int mouseY, boolean turning, int pageNumber) {
        Minecraft mc = Minecraft.getMinecraft();
        /*
        RenderItem itemRender = new RenderItem();
        ItemStack stack = new ItemStack(Blocks.stone);
        */
        mc.renderEngine.bindTexture(bookPageTexture);
        GlStateManager.color(1F, 1F, 1F, 1F);
        
        //drawTexturedModalRect(0, 0, 0, 0, PAGE_TEXTURE_WIDTH, PAGE_TEXTURE_HEIGHT);
        //drawTexturedModalRect(0, 0, 0, 0, PAGE_TEXTURE_WIDTH, PAGE_TEXTURE_HEIGHT);
        //drawTexturedRec(0, 0, 0, 0, PAGE_TEXTURE_WIDTH, PAGE_TEXTURE_HEIGHT);
        
        
        
/*
        
        renderTestRec(0, 0, PAGE_TEXTURE_WIDTH, PAGE_TEXTURE_HEIGHT, 1, 0, 1);
        
        renderTestRec(PAGE_PADDING_LEFT, PAGE_PADDING_TOP,
                PAGE_TEXTURE_WIDTH - PAGE_PADDING_LEFT * 2, PAGE_TEXTURE_HEIGHT - PAGE_PADDING_TOP * 2, 0, 1, 1);
        */
        drawPageTitleAndNumber(fontRenderer, pageNumber);
        
        
        
        //drawTestRec(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
        
        /*
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(1, 1, 1);
        itemRender.renderItemIntoGUI(fontRenderer, mc.getTextureManager(), stack, 64, 128);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        */
        //GL11.glEnable(GL11.GL_DEPTH_TEST);
        //RenderHelper.enableGUIStandardItemLighting();
        //GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        //GL11.glScalef(1, 1, 1);
        //GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        //RenderHelper.disableStandardItemLighting();
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        for (int i = 0; i < lines.size(); i++) {
            fontRenderer.drawString(lines.get(i),
                    PAGE_PADDING_LEFT,
                    PAGE_PADDING_TOP + fontRenderer.FONT_HEIGHT * 2 + i * 9,
                    0xFF2A2A2A);
        }
    }
    
    @Override
    public void renderRollover(FontRenderer fontRenderer, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
        
    }
}
