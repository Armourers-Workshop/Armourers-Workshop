package riskyken.armourersWorkshop.client.gui.controls;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

@SideOnly(Side.CLIENT)
public class GuiColourSelector extends GuiButtonExt {

    private Color selectedColour;
    private int colorWidth;
    private int colourHeight;
    private int rowLength;
    private ResourceLocation guiTexture;
    private ColourFamily colourFamily;
    
    public GuiColourSelector(int id, int xPos, int yPos, int width, int height, int colorWidth, int colourHeight, int rowLength, ResourceLocation guiTexture) {
        super(id, xPos, yPos, width, height, "");
        this.colorWidth = colorWidth;
        this.colourHeight = colourHeight;
        this.rowLength = rowLength;
        this.guiTexture = guiTexture;
        this.colourFamily = ColourFamily.MINECRAFT;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) { return; }
        
        this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int k = this.getHoverState(this.field_146123_n);
        GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        mc.renderEngine.bindTexture(guiTexture);
        for (int i = 0; i < 16; i++) {
            int curRow = i / rowLength;
            Color c = new Color(UtilColour.getMinecraftColor(i, this.colourFamily));
            float red = (float) c.getRed() / 255;
            float green = (float) c.getGreen() / 255;
            float blue = (float) c.getBlue() / 255;
            GL11.glColor4f(red, green, blue, 1.0F);
            int xPos = this.xPosition + 1 + this.colorWidth * i - curRow * colourHeight * rowLength;
            int yPos = this.yPosition + 1 + curRow * 10;
            drawTexturedModalRect(xPos, yPos, 146, 52, this.colorWidth, this.colourHeight);
            if (mouseX >= xPos & mouseY >= yPos & mouseX <= xPos + this.colorWidth & mouseY <= yPos + this.colourHeight) {
                this.selectedColour = new Color(UtilColour.getMinecraftColor(i, this.colourFamily));
            }
        }
        
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
