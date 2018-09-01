package moe.plushie.armourers_workshop.client.gui.controls;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (!this.visible) { return; }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int hoverState = this.getHoverState(this.hovered);
        int xOffset = 0;
        if (hoverState == 2) {
            xOffset = 23;
        }
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(this.x, this.y, srcX + xOffset, srcY, this.width, this.height);
    }
    
    @Override
    public void playPressSound(SoundHandler soundHandler) {
        //soundHandler.playSound(PositionedSoundRecord.getMasterRecord(new ResourceLocation(LibSounds.PAGE_TURN), 1.0F));
    }
}
