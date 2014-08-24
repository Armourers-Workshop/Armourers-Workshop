package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmourer extends GuiContainer {

    private TileEntityArmourerBrain armourerBrain;
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourer.png");
    
    @Override
    public void initGui() {
        super.initGui();
    }
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourerBrain armourerBrain) {
        super(new ContainerArmourer(invPlayer, armourerBrain));
        this.armourerBrain = armourerBrain;
        this.xSize = 176;
        this.ySize = 170;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourerBrain.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
