package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.utils.UtilItems;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiToolOptions extends GuiScreen implements ISlider {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/toolOptions.png");
    
    private final int guiWidth;
    private final int guiHeight;
    private int guiLeft;
    private int guiTop;
    private ItemStack stack;
    private String guiName;
    private GuiSlider slider;
    
    public GuiToolOptions(ItemStack stack) {
        this.stack = stack;
        guiWidth = 175;
        guiHeight = 61;
        this.guiName = stack.getDisplayName();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        buttonList.clear();
        slider = new GuiSlider(0, this.guiLeft + 12, this.guiTop + 25, "Intensity ", 1, 64, UtilItems.getIntensityFromStack(stack, 16), this);
        slider.showDecimal = false;
        buttonList.add(slider);
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.guiWidth, this.guiHeight);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        renderGuiTitle(fontRendererObj, guiName);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    private void renderGuiTitle(FontRenderer fontRenderer, String name) {
        int xPos = this.guiWidth / 2 - fontRenderer.getStringWidth(name) / 2;
        fontRenderer.drawString(name, this.guiLeft + xPos, this.guiTop + 6, 4210752);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate((byte)0, slider.getValueInt()));
    }
}
