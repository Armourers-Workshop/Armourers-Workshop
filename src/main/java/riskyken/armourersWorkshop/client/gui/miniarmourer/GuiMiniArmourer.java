package riskyken.armourersWorkshop.client.gui.miniarmourer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMiniArmourer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/miniArmourer.png");
    
    private TileEntityMiniArmourer tileEntity;
    
    public GuiMiniArmourer(InventoryPlayer invPlayer, TileEntityMiniArmourer tileEntity) {
        super(new ContainerMiniArmourer(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = 176;
        this.ySize = 176;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = tileEntity.getInventoryName();
        buttonList.clear();
        
        buttonList.add(new GuiButtonExt(0, guiLeft + 58, guiTop + 53, 50, 12, GuiHelper.getLocalizedControlName(guiName, "save")));
        buttonList.add(new GuiButtonExt(1, guiLeft + 58, guiTop + 53 + 13, 50, 12, GuiHelper.getLocalizedControlName(guiName, "load")));
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, this.tileEntity.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        String labelBuildingAccess = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.buildingAccess");
        this.fontRendererObj.drawSplitString(labelBuildingAccess, 5, 21, 170, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
