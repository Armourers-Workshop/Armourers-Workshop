package moe.plushie.armourers_workshop.client.gui.miniarmourer;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMiniArmourer extends GuiScreen {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/mini-armourer.png");
    
    private EntityPlayer player;
    private final int guiWidth;
    private final int guiHeight;
    protected int guiLeft;
    protected int guiTop;
    
    public GuiMiniArmourer(EntityPlayer player) {
        this.player = player;
        guiWidth = 176;
        guiHeight = 176;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        
        buttonList.clear();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.color(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.guiWidth, this.guiHeight);
    }
}
