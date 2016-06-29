package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

@SideOnly(Side.CLIENT)
public class GuiBookTextButton extends GuiButtonExt {

    public GuiBookTextButton(int id, int xPos, int yPos, int width, String displayString) {
        super(id, xPos, yPos, width, 8, displayString);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.hovered);
            int color = UtilColour.getMinecraftColor(8, ColourFamily.MINECRAFT);
            
            if (hoverState == 2) {
                color = UtilColour.getMinecraftColor(7, ColourFamily.MINECRAFT);
            }
            
            String buttonText = this.displayString;
            int strWidth = mc.fontRendererObj.getStringWidth(buttonText);
            int ellipsisWidth = mc.fontRendererObj.getStringWidth("...");
            
            mc.fontRendererObj.drawString(buttonText, this.xPosition, this.yPosition, color);
        }
    }
    
    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        //soundHandlerIn.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(LibSounds.PAGE_TURN), 1.0F));
    }
}
