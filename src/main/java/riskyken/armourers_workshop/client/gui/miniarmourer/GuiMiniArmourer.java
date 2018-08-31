package riskyken.armourers_workshop.client.gui.miniarmourer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.client.gui.GuiHelper;
import riskyken.armourers_workshop.common.inventory.ContainerMiniArmourer;
import riskyken.armourers_workshop.common.lib.LibModInfo;
import riskyken.armourers_workshop.common.tileentities.TileEntityMiniArmourer;

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
        String guiName = tileEntity.getName();
        buttonList.clear();
        
        buttonList.add(new GuiButtonExt(0, guiLeft + 58, guiTop + 53, 50, 12, GuiHelper.getLocalizedControlName(guiName, "save")));
        buttonList.add(new GuiButtonExt(1, guiLeft + 58, guiTop + 53 + 13, 50, 12, GuiHelper.getLocalizedControlName(guiName, "load")));
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, this.tileEntity.getName());
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        String labelBuildingAccess = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.buildingAccess");
        this.fontRenderer.drawSplitString(labelBuildingAccess, 5, 21, 170, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
