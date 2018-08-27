package riskyken.armourersWorkshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

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
    
    public GuiInventorySize(int xPos, int yPos, int maxWidth, int maxHeight) {
        super(-1, xPos, yPos, maxWidth * BUTTON_WIDTH, maxHeight * BUTTON_HEIGHT, "");
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }
    
    public void setSrc(ResourceLocation texture, int srcX, int srcY) {
        this.texture = texture;
        this.srcX = srcX;
        this.srcY = srcY;
    }
    
    public void setSelection(int selectionWidth, int selectionHeight) {
        this.selectionWidth = MathHelper.clamp_int(selectionWidth, 1, maxWidth);
        this.selectionHeight = MathHelper.clamp_int(selectionHeight, 1, maxHeight);
    }
    
    public int getSelectionWidth() {
        return selectionWidth;
    }
    
    public int getSelectionHeight() {
        return selectionHeight;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        int hoverWidth = MathHelper.ceiling_float_int((float)(mouseX - xPosition) / (float)BUTTON_WIDTH);
        int hoverHeight = MathHelper.ceiling_float_int((float)(mouseY - yPosition) / (float)BUTTON_HEIGHT);
        if (visible) {
            mc.renderEngine.bindTexture(texture);
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
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

                    drawTexturedModalRect(xPosition + BUTTON_WIDTH * ix, yPosition + BUTTON_HEIGHT * iy, srcX, srcY, BUTTON_WIDTH, BUTTON_HEIGHT);
                }
            }
        }
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            int selectionWidth = MathHelper.ceiling_float_int((float)(mouseX - xPosition) / (float)BUTTON_WIDTH);
            int selectionHeight = MathHelper.ceiling_float_int((float)(mouseY - yPosition) / (float)BUTTON_HEIGHT);
            setSelection(selectionWidth, selectionHeight);
            return true;
        }
        return false;
    }
}
