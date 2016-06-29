package riskyken.armourersWorkshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class GuiBookButton extends GuiButtonExt {

    private final static ResourceLocation buttonSound = new ResourceLocation(LibModInfo.ID.toLowerCase() + ":pageTurn");
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
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int hoverState = this.getHoverState(this.hovered);
        int xOffset = 0;
        if (hoverState == 2) {
            xOffset = 23;
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(this.xPosition, this.yPosition, srcX + xOffset, srcY, this.width, this.height);
    }
    
    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        //soundHandlerIn.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(LibSounds.PAGE_TURN), 1.0F));
    }
}
