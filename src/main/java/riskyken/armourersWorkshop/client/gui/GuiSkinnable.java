package riskyken.armourersWorkshop.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.inventory.ContainerSkinnable;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;

public class GuiSkinnable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.SKINNABLE);
    
    private final TileEntitySkinnable tileEntity;
    private final int invWidth;
    private final int invHeight;
    
    public GuiSkinnable(InventoryPlayer invPlayer, TileEntitySkinnable tileEntity, Skin skin) {
        super(new ContainerSkinnable(invPlayer, tileEntity, skin));
        this.tileEntity = tileEntity;
        invWidth = skin.getProperties().getPropertyInt(Skin.KEY_BLOCK_INVENTORY_WIDTH, 9);
        invHeight = skin.getProperties().getPropertyInt(Skin.KEY_BLOCK_INVENTORY_HEIGHT, 4);
    }
    
    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = invHeight * 18 + 125;
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
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, 0, 0, xSize, ySize, 176, 74, 4, zLevel);
        drawTexturedModalRect(guiLeft + 7, guiTop + ySize - 85, 0, 180, 162, 76);
        for (int ix = 0; ix < invWidth; ix ++) {
            for (int iy = 0; iy < invHeight; iy ++) {
                drawTexturedModalRect(guiLeft + ix * 18 + (xSize / 2 - (invWidth * 18) / 2), guiTop + iy * 18 + 20, 238, 0, 18, 18);
            }
        }
    }
}
