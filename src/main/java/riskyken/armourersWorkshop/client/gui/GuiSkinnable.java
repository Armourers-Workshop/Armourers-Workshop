package riskyken.armourersWorkshop.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.inventory.ContainerSkinnable;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;

public class GuiSkinnable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    
    private final TileEntitySkinnable tileEntity;
    
    public GuiSkinnable(InventoryPlayer invPlayer, TileEntitySkinnable tileEntity) {
        super(new ContainerSkinnable(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 184;
        super.initGui();
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        if (tileEntity.hasCustomName()) {
            String name = tileEntity.getCustomName();
            int width = fontRendererObj.getStringWidth(name);
            fontRendererObj.drawString(name, this.xSize / 2 - width / 2, 6, 4210752);
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, LibBlockNames.SKINNABLE);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 4 * 18 + 17);
        this.drawTexturedModalRect(k, l + 4 * 18 + 17, 0, 126, 176, 96);
    }
}
