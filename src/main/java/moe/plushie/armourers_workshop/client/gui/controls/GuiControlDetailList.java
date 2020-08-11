package moe.plushie.armourers_workshop.client.gui.controls;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControlDetailList extends GuiButtonExt {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.CONTROL_SKIN_PANEL);
    
    protected int scrollAmount;
    protected int selectedIndex;

    public GuiControlDetailList(int xPos, int yPos, int width, int height) {
        super(-1, xPos, yPos, width, height, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int k = this.getHoverState(this.hovered);
        GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }

    public interface IGuiDetailListColumn {

        public String getName();
        
        public int getWidth(int listWidth);
    }

    public interface IGuiDetailListItem {

        public void draw(Minecraft mc, int mouseX, int mouseY, float partialTicks);
    }
    
    public class GuiDetailListColumn implements IGuiDetailListColumn {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int getWidth(int listWidth) {
            return 0;
        }
    }

    public class GuiDetailListItem implements IGuiDetailListItem {

        @Override
        public void draw(Minecraft mc, int mouseX, int mouseY, float partialTicks) {

        }
    }
}
