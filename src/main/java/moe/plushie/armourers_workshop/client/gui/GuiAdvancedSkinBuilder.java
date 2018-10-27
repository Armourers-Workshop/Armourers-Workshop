package moe.plushie.armourers_workshop.client.gui;

import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

public class GuiAdvancedSkinBuilder extends GuiContainer {

    public GuiAdvancedSkinBuilder(EntityPlayer player, TileEntityAdvancedSkinBuilder tileEntity) {
        super(new ContainerAdvancedSkinBuilder(player.inventory, tileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    }
}
