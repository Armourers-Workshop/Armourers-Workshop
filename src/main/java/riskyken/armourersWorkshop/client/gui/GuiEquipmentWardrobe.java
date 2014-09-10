package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.custom.equipment.PlayerCustomEquipmentData;
import riskyken.armourersWorkshop.common.inventory.ContainerEquipmentWardrobe;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEquipmentWardrobe extends GuiContainer{
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/customArmourInventory.png");
    
    private float mouseX;
    private float mouseY;
    
    public GuiEquipmentWardrobe(InventoryPlayer inventory, PlayerCustomEquipmentData customEquipmentData) {
        super(new ContainerEquipmentWardrobe(inventory, customEquipmentData));
        this.xSize = 176;
        this.ySize = 176;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        super.drawScreen(mouseX, mouseY, p_73863_3_);
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, "equipmentWardrobe");
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        int boxX = this.guiLeft + 88;
        int boxY = this.guiTop + 82;
        float lookX = boxX - this.mouseX;
        float lookY = boxY - 50 - this.mouseY;
        GuiInventory.func_147046_a(boxX, boxY, 29, lookX, lookY, this.mc.thePlayer);
    }
}
