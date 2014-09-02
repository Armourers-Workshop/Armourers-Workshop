package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmourer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourer.png");
    
    private TileEntityArmourerBrain armourerBrain;
    private GuiCheckBox checkShowGuides;
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButtonExt(0, guiLeft + 5, guiTop + 16, 66, 16, "Save"));
        buttonList.add(new GuiButtonExt(1, guiLeft + 5, guiTop + 76, 66, 16, "Load"));
        checkShowGuides = new GuiCheckBox(2, guiLeft + 85, guiTop + 26, "Show Guide", armourerBrain.isShowGuides());
        buttonList.add(checkShowGuides);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < 3) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
        }
    }
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourerBrain armourerBrain) {
        super(new ContainerArmourer(invPlayer, armourerBrain));
        this.armourerBrain = armourerBrain;
        this.xSize = 176;
        this.ySize = 197;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourerBrain.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        checkShowGuides.setChecked(armourerBrain.isShowGuides());
        
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
