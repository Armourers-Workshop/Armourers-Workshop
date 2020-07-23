package moe.plushie.armourers_workshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHelp extends ModGuiControl {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.CONTROL_HELP);
    
    public GuiHelp(IScreenSize parent, int id, int xPos, int yPos, String hoverText) {
        super(parent, id, xPos, yPos, 7, 8);
        setHoverText(hoverText);
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        //super.drawButton(mc, mouseX, mouseY, partial);
        updateHoverState(mouseX, mouseY);
        if (!this.visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int hoverState = this.getHoverState(this.hovered);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        int offsetPos = 0;
        //hovering
        if (hoverState == 2) {
            offsetPos = width;
        }
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(x, y , offsetPos, 0, width, height);
    }
    
    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }
    
    @Override
    public void drawRollover(Minecraft mc, int mouseX, int mouseY) {
        if (getHoverTime() > 200) {
            super.drawRollover(mc, mouseX, mouseY);
        }
    }
}
