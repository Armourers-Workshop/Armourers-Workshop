package moe.plushie.armourers_workshop.client.gui.controls;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.utils.UtilColour;
import moe.plushie.armourers_workshop.utils.UtilColour.ColourFamily;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiColourSelector extends GuiButtonExt {

    private Color selectedColour;
    private int colorWidth;
    private int colourHeight;
    private int rowSize;
    private int colSize;
    private ResourceLocation guiTexture;
    private ColourFamily colourFamily;
    
    public GuiColourSelector(int id, int xPos, int yPos, int width, int height, int colorWidth, int colourHeight, int rowSize, int colSize, ResourceLocation guiTexture) {
        super(id, xPos, yPos, width, height, "");
        this.colorWidth = colorWidth;
        this.colourHeight = colourHeight;
        this.rowSize = rowSize;
        this.colSize = colSize;
        this.guiTexture = guiTexture;
        this.colourFamily = ColourFamily.MINECRAFT;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (!this.visible) { return; }
        
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int k = this.getHoverState(this.hovered);
        GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        mc.renderEngine.bindTexture(guiTexture);
        
        int hoverX = -1;
        int hoverY = -1;
        for (int i = 0; i < (rowSize * colSize); i++) {
            GlStateManager.resetColor();
            GlStateManager.color(1, 1, 1, 1);
            
            int curRow = i / rowSize;
            Color c = new Color(UtilColour.getMinecraftColor(i, this.colourFamily));
            int xPos = this.x + 1 + this.colorWidth * i - curRow * colourHeight * rowSize;
            int yPos = this.y + 1 + curRow * 10;
            drawRect(xPos, yPos, xPos + this.colorWidth, yPos + this.colourHeight, c.getRGB());
            if (mouseX >= xPos & mouseY >= yPos & mouseX <= xPos + this.colorWidth & mouseY <= yPos + this.colourHeight) {
                hoverX = xPos;
                hoverY = yPos;
                this.selectedColour = new Color(UtilColour.getMinecraftColor(i, this.colourFamily));
            }
        }
        GlStateManager.resetColor();
        if (hoverX != -1) {
            //mc.renderEngine.bindTexture(new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/colour-mixer.png"));
            GuiUtils.drawContinuousTexturedBox(hoverX, hoverY, 0, 240, colorWidth, colourHeight, 16, 16, 2, zLevel);
        }
        //drawTexturedModalRect(mouseX, hoverY, 0, 240, hoverX, hoverY);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mouseDragged(mc, mouseX, mouseY);
    }
    
    public ColourFamily getColourFamily() {
        return this.colourFamily;
    }
    
    public void setColourFamily(ColourFamily colourFamily) {
        this.colourFamily = colourFamily;
    }
    
    public Color getSelectedColour() {
        return selectedColour;
    }
}
