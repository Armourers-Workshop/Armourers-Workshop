package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerOutfitMaker;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityOutfitMaker;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiOutfitMaker extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.OUTFIT_MAKER);
    
    private final TileEntityOutfitMaker tileEntity;
    
    private GuiIconButton iconButtonLoad;
    private GuiIconButton iconButtonSave;
    
    public GuiOutfitMaker(EntityPlayer entityPlayer, TileEntityOutfitMaker tileEntity) {
        super(new ContainerOutfitMaker(entityPlayer, tileEntity));
        this.tileEntity = tileEntity;
        xSize = 176;
        ySize = 214;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        iconButtonLoad = new GuiIconButton(this, 0, getGuiLeft() + 8, getGuiTop() + 96, 20, 20, GuiHelper.getLocalizedControlName(tileEntity.getName(), "load"), TEXTURE).setIconLocation(0, 240, 16, 16);
        iconButtonSave = new GuiIconButton(this, 1, getGuiLeft() + getXSize() - 20 - 8, getGuiTop() + 96, 20, 20, GuiHelper.getLocalizedControlName(tileEntity.getName(), "save"), TEXTURE).setIconLocation(0, 224, 16, 16);
        buttonList.add(iconButtonLoad);
        buttonList.add(iconButtonSave);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        MessageClientGuiButton message = new MessageClientGuiButton((byte) button.id);
        PacketHandler.networkWrapper.sendToServer(message);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, tileEntity.getName());
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        iconButtonLoad.drawRollover(mc, mouseX, mouseY);
        iconButtonSave.drawRollover(mc, mouseX, mouseY);
        GlStateManager.popMatrix();
    }
}
