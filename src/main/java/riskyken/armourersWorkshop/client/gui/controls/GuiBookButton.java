package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;

public class GuiBookButton extends GuiButtonExt {

    private final ResourceLocation texture;
    private final int srcX;
    private final int srcY;
    
    public GuiBookButton(int id, int xPos, int yPos, int srcX, int srcY, ResourceLocation texture) {
        super(id, xPos, yPos, 18, 10, "awdaw");
        this.srcX = srcX;
        this.srcY = srcY;
        this.texture = texture;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) { return; }
        this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int hoverState = this.getHoverState(this.field_146123_n);
        int xOffset = 0;
        if (hoverState == 2) {
            xOffset = 23;
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(this.xPosition, this.yPosition, srcX + xOffset, srcY, this.width, this.height);
    }
    
    
}
