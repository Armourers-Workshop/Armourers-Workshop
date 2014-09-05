package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.common.customarmor.ArmourType;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
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
    
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = armourerBrain.getInventoryName();
        
        buttonList.clear();
        
        for (int i = 0; i < ArmourType.values().length - 1; i++) {
            buttonList.add(new GuiButtonExt(i, guiLeft + 5, guiTop + 16 + (i * 20), 50, 16, ArmourType.getOrdinal(i + 1).getLocalizedName()));
        }
        
        buttonList.add(new GuiButtonExt(5, guiLeft + 86, guiTop + 16, 50, 12, "Save"));
        buttonList.add(new GuiButtonExt(6, guiLeft + 86, guiTop + 16 + 13, 50, 12, "Load"));
        
        checkShowGuides = new GuiCheckBox(7, guiLeft + 5, guiTop + 118, GuiHelper.getLocalizedControlName(guiName, "showGuide"), armourerBrain.isShowGuides());
        checkShowOverlay = new GuiCheckBox(9, guiLeft + 5, guiTop + 134, GuiHelper.getLocalizedControlName(guiName, "showOverlay"), armourerBrain.isShowOverlay());
        
        textItemName = new GuiTextField(fontRendererObj, guiLeft + 64, guiTop + 58, 103, 16);
        textItemName.setMaxStringLength(20);
        
        textUserSkin = new GuiTextField(fontRendererObj, guiLeft + 64, guiTop + 88, 70, 16);
        textUserSkin.setMaxStringLength(20);
        buttonList.add(new GuiButtonExt(8, guiLeft + 138, guiTop + 88, 30, 16, "Set"));
        
        if (armourerBrain.getGameProfile() != null) {
            textUserSkin.setText(armourerBrain.getGameProfile().getName());
        }
        
        buttonList.add(checkShowGuides);
        buttonList.add(checkShowOverlay);
        //buttonList.add(new GuiButtonExt(9, guiLeft + 5, guiTop + 118, 115, 16, "Mirror Left To Right"));
        //buttonList.add(new GuiButtonExt(10, guiLeft + 5, guiTop + 138, 115, 16, "Mirror Right To Left"));
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
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        case 5:
            PacketHandler.networkWrapper.sendToServer(new MessageClientLoadArmour(textItemName.getText().trim()));
            break;
        case 8:
            String username = textUserSkin.getText().trim();
            if (!username.equals("")) {
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetSkin(username));
            }
            break;
        default:
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
            break;
        }
    }
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourerBrain armourerBrain) {
        super(new ContainerArmourer(invPlayer, armourerBrain));
        this.armourerBrain = armourerBrain;
        this.xSize = 176;
        this.ySize = 256;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourerBrain.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    
        String itemNameLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.itemName");
        String usernameLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.username");
        
        this.fontRendererObj.drawString(itemNameLabel, 64, 48, 4210752);
        this.fontRendererObj.drawString(usernameLabel, 64, 78, 4210752);
        
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        checkShowGuides.setChecked(armourerBrain.isShowGuides());
        checkShowOverlay.setChecked(armourerBrain.isShowOverlay());
        checkShowOverlay.visible = armourerBrain.getType() == ArmourType.HEAD;
        
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        textItemName.drawTextBox();
        textUserSkin.drawTextBox();
    }
}
