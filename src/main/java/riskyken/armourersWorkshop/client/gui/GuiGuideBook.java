package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGuideBook extends GuiScreen {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/guideBook.png");
    
    private final int guiWidth;
    private final int guiHeight;
    private int guiLeft;
    private int guiTop;
    private ItemStack stack;
    
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        buttonList.clear();
    }
    
    public GuiGuideBook(ItemStack stack) {
        this.stack = stack;
        guiWidth = 256;
        guiHeight = 180;
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.guiWidth, this.guiHeight);
        
        drawTexturedModalRect(this.guiLeft + 212, this.guiTop + 156, 3, 194, 18, 10);
        drawTexturedModalRect(this.guiLeft + 26, this.guiTop + 156, 3, 207, 18, 10);
        
        
        String unlocalized =  "book.armourersworkshop:guideBook.page1" + ".text";
        String localized = StatCollector.translateToLocal(unlocalized);
        if (!unlocalized.equals(localized)) {
            fontRendererObj.drawSplitString(localized, this.guiLeft + 16, this.guiTop + 12, 106, UtilColour.getMinecraftColor(7));
        }
        
        
    }
    
    
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
