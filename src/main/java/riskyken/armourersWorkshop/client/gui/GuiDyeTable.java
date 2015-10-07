package riskyken.armourersWorkshop.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.inventory.ContainerDyeTable;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;

public class GuiDyeTable extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/dyeTable.png");
    
    private final TileEntityDyeTable tileEntity;
    
    public GuiDyeTable(InventoryPlayer invPlayer, TileEntityDyeTable tileEntity) {
        super(new ContainerDyeTable(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = 176;
        this.ySize = 232;
    }
    
    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 213;
        super.initGui();
        
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f1, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
}
