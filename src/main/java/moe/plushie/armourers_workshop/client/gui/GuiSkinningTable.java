package moe.plushie.armourers_workshop.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinningTable;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinningTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSkinningTable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.GUI_SKINNING_TABLE);

    private final TileEntitySkinningTable tileEntity;

    public GuiSkinningTable(InventoryPlayer invPlayer, TileEntitySkinningTable tileEntity) {
        super(new ContainerSkinningTable(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = 176;
        this.ySize = 176;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, LibBlockNames.SKINNING_TABLE, 0x282216);
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 0x282216);
    }
}
