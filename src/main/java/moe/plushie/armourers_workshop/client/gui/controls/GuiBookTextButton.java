package moe.plushie.armourers_workshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBookTextButton extends GuiButtonExt {

    public GuiBookTextButton(int id, int xPos, int yPos, int width, String displayString) {
        super(id, xPos, yPos, width, 8, displayString);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int hoverState = this.getHoverState(this.hovered);
            int color = 0xFF151515;
            
            if (hoverState == 2) {
                color = 0xFF2A2A2A;
            }
            
            String buttonText = this.displayString;
            int strWidth = mc.fontRenderer.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
            
            mc.fontRenderer.drawString(buttonText, this.x, this.y, color);
        }
    }
    /*
    @Override
    public void func_146113_a(SoundHandler soundHandler) {
        soundHandler.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(LibSounds.PAGE_TURN), 1.0F));
    }
    */
}
