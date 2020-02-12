package moe.plushie.armourers_workshop.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinnable;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiSkinnable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.COMMON);
    
    private final TileEntitySkinnable tileEntity;
    private final boolean ender;
    private int invWidth;
    private int invHeight;
    
    public GuiSkinnable(InventoryPlayer invPlayer, TileEntitySkinnable tileEntity, Skin skin) {
        super(new ContainerSkinnable(invPlayer, tileEntity, skin));
        this.tileEntity = tileEntity;
        ender = SkinProperties.PROP_BLOCK_ENDER_INVENTORY.getValue(skin.getProperties());
        invWidth = SkinProperties.PROP_BLOCK_INVENTORY_WIDTH.getValue(skin.getProperties());
        invHeight = SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT.getValue(skin.getProperties());
        if (ender) {
            invWidth = 9;
            invHeight = 3;
        }
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
            int width = fontRenderer.getStringWidth(name);
            fontRenderer.drawString(name, this.xSize / 2 - width / 2, 6, 4210752);
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, LibBlockNames.SKINNABLE);
        }
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(guiLeft, guiTop, 0, 0, xSize, ySize, 128, 128, 4, zLevel);
        drawTexturedModalRect(guiLeft + 7, guiTop + ySize - 85, 0, 180, 162, 76);
        for (int ix = 0; ix < invWidth; ix ++) {
            for (int iy = 0; iy < invHeight; iy ++) {
                drawTexturedModalRect(guiLeft + ix * 18 + (xSize / 2 - (invWidth * 18) / 2), guiTop + iy * 18 + 20, 238, 0, 18, 18);
            }
        }
    }
}
