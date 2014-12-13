package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiSetArmourerCustomName;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiSetSkin;
import riskyken.armourersWorkshop.common.network.messages.MessageClientLoadArmour;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmourer extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourer.png");
    
    private TileEntityArmourerBrain armourerBrain;
    private GuiCheckBox checkShowGuides;
    private GuiCheckBox checkShowOverlay;
    private GuiTextField textItemName;
    private GuiTextField textUserSkin;
    private boolean loadedArmourItem;
    private boolean canMakeBows = false;
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourerBrain armourerBrain) {
        super(new ContainerArmourer(invPlayer, armourerBrain));
        this.armourerBrain = armourerBrain;
        this.xSize = 256;
        this.ySize = 256;
        loadedArmourItem = false;
        if (invPlayer.player.getCommandSenderName().equalsIgnoreCase("riskyken")) {
            canMakeBows = true;
        } else {
            canMakeBows = false;
        }
    }
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = armourerBrain.getInventoryName();
        
        buttonList.clear();
        
        for (int i = 0; i < EnumEquipmentType.values().length - 1; i++) {
            GuiButtonExt equipmentButton = new GuiButtonExt(i, guiLeft + 5, guiTop + 16 + (i * 20), 50, 16, getLocalizedArmourName(EnumEquipmentType.getOrdinal(i + 1)));
            if (i == 6) {
                if (!canMakeBows) {
                    equipmentButton.enabled = false;
                }
            }
            buttonList.add(equipmentButton);
        }
        
        buttonList.add(new GuiButtonExt(13, guiLeft + 86, guiTop + 16, 50, 12, GuiHelper.getLocalizedControlName(guiName, "save")));
        buttonList.add(new GuiButtonExt(14, guiLeft + 86, guiTop + 16 + 13, 50, 12, GuiHelper.getLocalizedControlName(guiName, "load")));
        
        checkShowGuides = new GuiCheckBox(7, guiLeft + 64, guiTop + 118, 14, 14, GuiHelper.getLocalizedControlName(guiName, "showGuide"), armourerBrain.isShowGuides(), false);
        checkShowOverlay = new GuiCheckBox(9, guiLeft + 64, guiTop + 134, 14, 14, GuiHelper.getLocalizedControlName(guiName, "showOverlay"), armourerBrain.isShowOverlay(), false);
        
        textItemName = new GuiTextField(fontRendererObj, guiLeft + 64, guiTop + 58, 103, 16);
        textItemName.setMaxStringLength(40);
        textItemName.setText(armourerBrain.getCustomName());
        
        textUserSkin = new GuiTextField(fontRendererObj, guiLeft + 64, guiTop + 88, 70, 16);
        textUserSkin.setMaxStringLength(30);
        buttonList.add(new GuiButtonExt(8, guiLeft + 138, guiTop + 88, 30, 16, GuiHelper.getLocalizedControlName(guiName, "set")));
        
        buttonList.add(new GuiButtonExt(10, guiLeft + 177, guiTop + 16, 70, 16, GuiHelper.getLocalizedControlName(guiName, "clear")));
        
        if (armourerBrain.getGameProfile() != null) {
            textUserSkin.setText(armourerBrain.getGameProfile().getName());
        }
        
        buttonList.add(checkShowGuides);
        buttonList.add(checkShowOverlay);
        //buttonList.add(new GuiButtonExt(11, guiLeft + 177, guiTop + 46, 70, 16, GuiHelper.getLocalizedControlName(guiName, "westToEast")));
        //buttonList.add(new GuiButtonExt(12, guiLeft + 177, guiTop + 66, 70, 16, GuiHelper.getLocalizedControlName(guiName, "eastToWest")));
        //buttonList.add(new GuiButtonExt(13, guiLeft + 177, guiTop + 76, 70, 16, "Add Noise"));
    }
    
    private String getLocalizedArmourName(EnumEquipmentType armourType) {
        String unlocalizedName = "armourTypes." + LibModInfo.ID.toLowerCase() + ":" + armourType.name().toLowerCase() + ".name";
        String localizedName = StatCollector.translateToLocal(unlocalizedName);
        if (!unlocalizedName.equals(localizedName)){
            return localizedName;
        }
        return unlocalizedName;
    }
    
    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        textItemName.mouseClicked(x, y, button);
        textUserSkin.mouseClicked(x, y, button);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        if (!textItemName.textboxKeyTyped(key, keyCode)) {
            if (!textUserSkin.textboxKeyTyped(key, keyCode)) {
                super.keyTyped(key, keyCode);
            }
        } else {
            String sendText = textItemName.getText().trim();
            if (!sendText.equals(armourerBrain.getCustomName())) {
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerCustomName(sendText));
            }
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        case 13:
            PacketHandler.networkWrapper.sendToServer(new MessageClientLoadArmour(textItemName.getText().trim()));
            break;
        case 8:
            String username = textUserSkin.getText().trim();
            if (!username.equals("")) {
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetSkin(username));
            }
            break;
        default:
            if (button.id == 14) {
                loadedArmourItem = true;
                armourerBrain.setCustomName("");
            }
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
            break;
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourerBrain.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    
        String itemNameLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.itemName");
        String usernameLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.username");
        String cloneLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.clone");
        
        this.fontRendererObj.drawString(itemNameLabel, 64, 48, 4210752);
        this.fontRendererObj.drawString(usernameLabel, 64, 78, 4210752);
        //this.fontRendererObj.drawString(cloneLabel, 177, 36, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        if (loadedArmourItem) {
            if (!armourerBrain.getCustomName().equals("")) {
                textItemName.setText(armourerBrain.getCustomName());
                loadedArmourItem = false;
            }
        }
        checkShowGuides.setChecked(armourerBrain.isShowGuides());
        checkShowOverlay.setChecked(armourerBrain.isShowOverlay());
        checkShowOverlay.visible = armourerBrain.getType() == EnumEquipmentType.HEAD;
        
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        textItemName.drawTextBox();
        textUserSkin.drawTextBox();
    }
}
