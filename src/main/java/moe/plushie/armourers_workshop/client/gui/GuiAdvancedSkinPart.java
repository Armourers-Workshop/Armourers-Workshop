package moe.plushie.armourers_workshop.client.gui;

import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinPart;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinPart;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedSkinPart extends GuiContainer {

    public GuiAdvancedSkinPart(EntityPlayer player, TileEntityAdvancedSkinPart tileEntity) {
        super(new ContainerAdvancedSkinPart(player.inventory, tileEntity));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
    }
}
