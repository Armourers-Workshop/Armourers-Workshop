package riskyken.armourersWorkshop.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
    
    @Override
    public void func_146113_a(SoundHandler soundHandler) {
        soundHandler.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation(LibSounds.PAGE_TURN), 1.0F));
    }
}
