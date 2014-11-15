package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.inventory.ContainerEquipmentWardrobe;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiUpdateNakedInfo;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEquipmentWardrobe extends GuiContainer{
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/customArmourInventory.png");
    
    Color skinColour;
    Color pantsColour;
    BitSet armourOverride;
    boolean headOverlay;
    
    ExtendedPropsPlayerEquipmentData customEquipmentData;
    PlayerSkinInfo skinInfo;
    EntityPlayer player;
    
    private GuiCheckBox nakedCheck;
    private GuiButtonExt autoButton;
    
    private GuiCheckBox[] armourOverrideCheck;
    private GuiCheckBox[] overlayOverrideCheck;
    
    private float mouseX;
    private float mouseY;
    
    public GuiEquipmentWardrobe(InventoryPlayer inventory, ExtendedPropsPlayerEquipmentData customEquipmentData) {
        super(new ContainerEquipmentWardrobe(inventory, customEquipmentData));
        this.customEquipmentData = customEquipmentData;
        this.player = inventory.player;
        skinInfo = ArmourersWorkshop.proxy.getPlayersNakedData(this.player.getUniqueID());
        
        if (skinInfo == null) {
            skinInfo = new PlayerSkinInfo(false, UtilColour.getMinecraftColor(0), UtilColour.getMinecraftColor(0), new BitSet(4), true);
            ModLogger.log(Level.ERROR,"Unable to get skin info for player: " + this.player.getDisplayName());
        }
        
        if (skinInfo != null) {
            this.skinColour = new Color(skinInfo.getSkinColour());
            this.pantsColour = new Color(skinInfo.getPantsColour());
            this.armourOverride = skinInfo.getArmourOverride();
            this.headOverlay = skinInfo.getHeadOverlay();
        }
        
        this.xSize = 176;
        this.ySize = 248;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        String guiName = "equipmentWardrobe";
        
        autoButton = new GuiButtonExt(0, this.guiLeft + 80, this.guiTop + 128, 80, 20, GuiHelper.getLocalizedControlName(guiName, "autoColour"));
        nakedCheck = new GuiCheckBox(1, this.guiLeft + 8, this.guiTop + 94, 14, 14, GuiHelper.getLocalizedControlName(guiName, "nakedSkin"), skinInfo.isNaked(), false);
        
        armourOverrideCheck = new GuiCheckBox[4];
        armourOverrideCheck[0] = new GuiCheckBox(2, this.guiLeft + 29, this.guiTop + 17, 7, 7, "Disable head armour render", armourOverride.get(0), true);
        armourOverrideCheck[1] = new GuiCheckBox(3, this.guiLeft + 29, this.guiTop + 44, 7, 7, "Disable chest armour render", armourOverride.get(1), true);
        armourOverrideCheck[2] = new GuiCheckBox(4, this.guiLeft + 140, this.guiTop + 17, 7, 7, "Disable leg armour render", armourOverride.get(2), true);
        armourOverrideCheck[3] = new GuiCheckBox(5, this.guiLeft + 140, this.guiTop + 71, 7, 7, "Disable foot armour render", armourOverride.get(3), true);
        
        overlayOverrideCheck = new GuiCheckBox[1];
        overlayOverrideCheck[0] = new GuiCheckBox(6, this.guiLeft + 29, this.guiTop + 28, 7, 7, "Disable head overlay render", headOverlay, true);
        
        buttonList.add(autoButton);
        buttonList.add(nakedCheck);
        buttonList.add(overlayOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[1]);
        buttonList.add(armourOverrideCheck[2]);
        buttonList.add(armourOverrideCheck[3]);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
    	if (button instanceof GuiCheckBox) {
    		((GuiCheckBox)button).setChecked(!((GuiCheckBox)button).isChecked());
    		headOverlay = overlayOverrideCheck[0].isChecked();
    		for (int i = 0; i < 4; i++) {
    			armourOverride.set(i, armourOverrideCheck[i].isChecked());
    		}
    	}
    	
    	
    	
        if (button.id >= 1) {
            //((GuiCheckBox)button).setChecked(!((GuiCheckBox)button).isChecked());
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(
            		nakedCheck.isChecked(),
                            skinColour.getRGB(), pantsColour.getRGB(), armourOverride, headOverlay)
            );
        }
        if (button.id == 0) {
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, "equipmentWardrobe");
        String labelSkinColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinColour");
        this.fontRendererObj.drawString(labelSkinColour + ":", 8, 114, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        
        for (int i = 0; i < this.buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiCheckBox) {
                if (((GuiCheckBox)button).isHovering(mouseX, mouseY) & ((GuiCheckBox)button).small) {
                    
                    ArrayList lines = new ArrayList();
                    lines.add(button.displayString);
                    drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
                }
            }
        }

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
