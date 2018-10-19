package moe.plushie.armourers_workshop.client.gui;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerOutfit;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiOutfit extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.OUTFIT);
    
    private final ItemStack stackOutfit;
    
    public GuiOutfit(EntityPlayer player, ItemStack stackOutfit) {
        super(new ContainerOutfit(player.inventory, stackOutfit));
        this.stackOutfit = stackOutfit;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = stackOutfit.getDisplayName();
        int xPos = xSize / 2 - fontRenderer.getStringWidth(title) / 2;
        this.fontRenderer.drawString(title, xPos, 6, 4210752);
        
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
}
