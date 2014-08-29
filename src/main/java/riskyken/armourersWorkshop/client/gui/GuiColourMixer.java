package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiColourUpdate;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiColourMixer extends GuiContainer {

    private TileEntityColourMixer tileEntityColourMixer;
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/colour-mixer.png");
    
    private GuiSlider redSlider;
    private GuiSlider greenSlider;
    private GuiSlider blueSlider;
    
    @Override
    public void initGui() {
        super.initGui();
        
        Color c = new Color(tileEntityColourMixer.getColour());
        redSlider = new GuiSlider(0, this.guiLeft + 5, this.guiTop + 35, 128 , 20, "R ", "", 0, 255, c.getRed(), false, true);
        greenSlider = new GuiSlider(1, this.guiLeft + 5, this.guiTop + 60, 128 , 20, "G ", "", 0, 255, c.getGreen(), false, true);
        blueSlider = new GuiSlider(2, this.guiLeft + 5, this.guiTop + 85, 128 , 20, "B ", "", 0, 255, c.getBlue(), false, true);
        
        buttonList.add(redSlider);
        buttonList.add(greenSlider);
        buttonList.add(blueSlider);
    }
    
    private void checkForColourUpdates() {
        if (tileEntityColourMixer.getHasItemUpdateAndReset()) {
            Color c = new Color(tileEntityColourMixer.getColour());
            redSlider.setValue(c.getRed());
            greenSlider.setValue(c.getGreen());
            blueSlider.setValue(c.getBlue());
            ModLogger.log("Item update");
        }
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        
        if (which != 0) { return; }
        
        Color colourNew = new Color(redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt());
        Color colourOld = new Color(tileEntityColourMixer.getColour());
        
        if (colourNew.equals(colourOld)) { return; }
        
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiColourUpdate(colourNew.getRGB(), false));
    }
    
    
    
    public GuiColourMixer(InventoryPlayer invPlayer, TileEntityColourMixer tileEntityColourMixer) {
        super(new ContainerColourMixer(invPlayer, tileEntityColourMixer));
        this.tileEntityColourMixer = tileEntityColourMixer;
        this.xSize = 176;
        this.ySize = 213;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntityColourMixer.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        checkForColourUpdates();
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        float red = (float) redSlider.getValueInt() / 255;
        float green = (float) greenSlider.getValueInt() / 255;
        float blue = (float) blueSlider.getValueInt() / 255;
        GL11.glColor4f(red, green, blue, 1F);
        
        drawTexturedModalRect(this.guiLeft + 146, this.guiTop + 59, 146, 59, 12, 13);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }
}
