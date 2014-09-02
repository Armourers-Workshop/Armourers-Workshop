package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.inventory.ContainerArmourCrafter;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourCrafter;
import cpw.mods.fml.client.config.GuiButtonExt;

public class GuiArmourCrafter extends GuiContainer {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourCrafter.png");
    
    private TileEntityArmourCrafter armourCrafter;
    
    public GuiArmourCrafter(InventoryPlayer invPlayer, TileEntityArmourCrafter armourCrafter) {
        super(new ContainerArmourCrafter(invPlayer, armourCrafter));
        this.armourCrafter = armourCrafter;
        this.xSize = 176;
        this.ySize = 197;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiButtonExt(0, guiLeft + 54, guiTop + 55, 60, 16,
                GuiHelper.getLocalizedControlName(armourCrafter.getInventoryName(), "create")));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourCrafter.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
