package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.custom.equipment.PlayerCustomEquipmentData;
import riskyken.armourersWorkshop.common.inventory.ContainerEquipmentWardrobe;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiUpdateNakedInfo;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEquipmentWardrobe extends GuiContainer{
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/customArmourInventory.png");
    
    Color skinColour;
    Color pantsColour;
    
    PlayerCustomEquipmentData customEquipmentData;
    PlayerSkinInfo skinInfo;
    EntityPlayer player;
    
    private GuiCheckBox nakedCheck;
    private GuiButtonExt autoButton;
    private float mouseX;
    private float mouseY;
    
    public GuiEquipmentWardrobe(InventoryPlayer inventory, PlayerCustomEquipmentData customEquipmentData) {
        super(new ContainerEquipmentWardrobe(inventory, customEquipmentData));
        this.customEquipmentData = customEquipmentData;
        this.player = inventory.player;
        skinInfo = ArmourersWorkshop.proxy.getPlayersNakedData(this.player.getUniqueID());
        this.skinColour = new Color(skinInfo.getSkinColour());
        this.pantsColour = new Color(skinInfo.getPantsColour());
        this.xSize = 176;
        this.ySize = 248;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        nakedCheck = new GuiCheckBox(0, this.guiLeft + 8, this.guiTop + 94, "Make players skin naked?", skinInfo.isNaked());
        autoButton = new GuiButtonExt(1, this.guiLeft + 80, this.guiTop + 128, 80, 20, "Auto Colour");
        buttonList.add(nakedCheck);
        buttonList.add(autoButton);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            ((GuiCheckBox)button).setChecked(!((GuiCheckBox)button).isChecked());
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(
                            ((GuiCheckBox)button).isChecked(),
                            skinColour.getRGB(), pantsColour.getRGB())
            );
        }
        if (button.id == 1) {
            skinInfo.autoColourSkin((AbstractClientPlayer) this.player);
        }
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
        
        String labelSkinColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinColour");
        this.fontRendererObj.drawString(labelSkinColour + ":", 8, 114, 4210752);
        
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.skinColour = new Color(skinInfo.getSkinColour());
        this.pantsColour = new Color(skinInfo.getPantsColour());
        
        float red = (float) skinColour.getRed() / 255;
        float green = (float) skinColour.getGreen() / 255;
        float blue = (float) skinColour.getBlue() / 255;
        GL11.glColor4f(red, green, blue, 1F);
        this.drawTexturedModalRect(this.guiLeft + 30, this.guiTop + 132, 30, 132, 12, 12);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int boxX = this.guiLeft + 88;
        int boxY = this.guiTop + 82;
        float lookX = boxX - this.mouseX;
        float lookY = boxY - 50 - this.mouseY;
        GuiInventory.func_147046_a(boxX, boxY, 29, lookX, lookY, this.mc.thePlayer);
    }
}
