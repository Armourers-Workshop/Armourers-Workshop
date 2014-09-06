package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmourLibrary extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourLibrary.png");
    
    private TileEntityArmourLibrary armourLibrary;
    
    public GuiArmourLibrary(InventoryPlayer invPlayer, TileEntityArmourLibrary armourLibrary) {
        super(new ContainerArmourLibrary(invPlayer, armourLibrary));
        this.armourLibrary = armourLibrary;
        this.xSize = 176;
        this.ySize = 197;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiButtonExt(0, guiLeft + 50, guiTop + 20, 100, 20, "Save Test"));
        buttonList.add(new GuiButtonExt(1, guiLeft + 50, guiTop + 80, 100, 20, "Load Test"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_,int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        this.fontRendererObj.drawString("WIP Do not use!!!", 46, 60, UtilColour.getMinecraftColor(14));
    }
}
