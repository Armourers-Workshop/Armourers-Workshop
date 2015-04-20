package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.equipment.npc.InventoryEntitySkin;
import riskyken.armourersWorkshop.common.inventory.ContainerEntityEquipment;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class GuiEntityEquipment extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/entitySkinInventory.png");
    
    public GuiEntityEquipment(InventoryPlayer playerInventory, InventoryEntitySkin skinInventory) {
        super(new ContainerEntityEquipment(playerInventory, skinInventory));
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.xSize = 176;
        this.ySize = 148;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, "entityEquipment");
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
