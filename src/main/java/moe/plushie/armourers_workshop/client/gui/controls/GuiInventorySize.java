package moe.plushie.armourers_workshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiInventorySize extends GuiButtonExt {

    private final static int BUTTON_WIDTH = 10;
    private final static int BUTTON_HEIGHT = 10;
    
    public final int maxWidth;
    public final int maxHeight;
    
    private int selectionWidth = 1;
    private int selectionHeight = 1;
    
    private ResourceLocation texture;
    private int srcX = 0;
    private int srcY = 0;
    
    public GuiInventorySize(int id, int xPos, int yPos, int maxWidth, int maxHeight) {
        super(id, xPos, yPos, maxWidth * BUTTON_WIDTH, maxHeight * BUTTON_HEIGHT, "");
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }
    
    public void setSrc(ResourceLocation texture, int srcX, int srcY) {
        this.texture = texture;
        this.srcX = srcX;
        this.srcY = srcY;
    }
    
    public void setSelection(int selectionWidth, int selectionHeight) {
        this.selectionWidth = MathHelper.clamp(selectionWidth, 1, maxWidth);
        this.selectionHeight = MathHelper.clamp(selectionHeight, 1, maxHeight);
    }
    
    public int getSelectionWidth() {
        return selectionWidth;
    }
    
    public int getSelectionHeight() {
        return selectionHeight;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        int hoverWidth = MathHelper.ceil((float)(mouseX - x) / (float)BUTTON_WIDTH);
        int hoverHeight = MathHelper.ceil((float)(mouseY - y) / (float)BUTTON_HEIGHT);
        if (visible) {
            mc.renderEngine.bindTexture(texture);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            for (int ix = 0; ix < maxWidth; ix++) {
                for (int iy = 0; iy < maxHeight; iy++) {
                    int srcX = this.srcX;
                    int srcY = this.srcY;
                    if (ix < this.selectionWidth & iy < this.selectionHeight) {
                        srcX += BUTTON_WIDTH;
                    }
                    if ((ix < hoverWidth & iy < hoverHeight) & k == 2) {
                        srcY += BUTTON_HEIGHT;
                    }

                    drawTexturedModalRect(x + BUTTON_WIDTH * ix, y + BUTTON_HEIGHT * iy, srcX, srcY, BUTTON_WIDTH, BUTTON_HEIGHT);
                }
            }
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            int selectionWidth = MathHelper.ceil((float)(mouseX - x) / (float)BUTTON_WIDTH);
            int selectionHeight = MathHelper.ceil((float)(mouseY - y) / (float)BUTTON_HEIGHT);
            setSelection(selectionWidth, selectionHeight);
            return true;
        }
        return false;
    }
}
