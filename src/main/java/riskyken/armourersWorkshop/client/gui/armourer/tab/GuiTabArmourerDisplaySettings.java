package riskyken.armourersWorkshop.client.gui.armourer.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.armourer.GuiArmourer;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.client.texture.PlayerTexture;
import riskyken.armourersWorkshop.common.data.TextureType;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetSkin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerDisplaySettings extends GuiTabPanel implements IDropDownListCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.ARMOURER);
    
    private final TileEntityArmourer tileEntity;
    
    private GuiDropDownList textureTypeList;
    private GuiTextField textUserSkin;
    private GuiCheckBox checkShowGuides;
    private GuiCheckBox checkShowOverlay;
    private GuiCheckBox checkShowHelper;
    
    public GuiTabArmourerDisplaySettings(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer)parent).tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getInventoryName();
        
        buttonList.clear();
        
        checkShowGuides = new GuiCheckBox(7, 10, 95, GuiHelper.getLocalizedControlName(guiName, "showGuide"), tileEntity.isShowGuides());
        checkShowOverlay = new GuiCheckBox(9, 10, 110, GuiHelper.getLocalizedControlName(guiName, "showOverlay"), tileEntity.isShowOverlay());
        checkShowHelper = new GuiCheckBox(6, 10, 110, GuiHelper.getLocalizedControlName(guiName, "showHelper"), tileEntity.isShowHelper());
        
        buttonList.add(new GuiButtonExt(8, width - 36 - 5, 70, 30, 16, GuiHelper.getLocalizedControlName(guiName, "set")));
        textUserSkin = new GuiTextField(-1, fontRenderer, x + 10, y + 70, 120, 16);
        textUserSkin.setMaxStringLength(300);
        textUserSkin.setText(tileEntity.getTexture().getTextureString());
        
        textureTypeList = new GuiDropDownList(0, 10, 30, 50, "", this);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "dropdown.user"), TextureType.USER.toString(), true);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "dropdown.url"), TextureType.URL.toString(), true);
        textureTypeList.setListSelectedIndex(tileEntity.getTexture().getTextureType().ordinal());
        
        buttonList.add(checkShowGuides);
        buttonList.add(checkShowOverlay);
        buttonList.add(checkShowHelper);
        buttonList.add(textureTypeList);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textUserSkin.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textUserSkin.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return false;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 8) {
            String username = textUserSkin.getText().trim();
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetSkin(new PlayerTexture(username, TextureType.values()[textureTypeList.getListSelectedIndex()])));
        } else {
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
        
        drawTexturedModalRect(this.x + 7, this.y + 141, 7, 3, 162, 76);
        
        textUserSkin.drawTextBox();
        
        checkShowGuides.setIsChecked(tileEntity.isShowGuides());
        checkShowOverlay.setIsChecked(tileEntity.isShowOverlay());
        
        int checkY = 110;
        if (tileEntity.getSkinType() != null) {
            checkShowOverlay.visible = tileEntity.getSkinType().showSkinOverlayCheckbox();
            checkShowOverlay.y = checkY;
            if (checkShowOverlay.visible) {
                checkY += 16;
            }
        } else {
            checkShowOverlay.visible = false;
        }
        
        if (tileEntity.getSkinType() != null) {
            checkShowHelper.visible = tileEntity.getSkinType().showHelperCheckbox();
            checkShowHelper.y = checkY;
        } else {
            checkShowHelper.visible = false;
        }
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        String labelSkinType = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.skinType");
        String usernameLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.username");
        String urlLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.url");
        this.fontRenderer.drawString(labelSkinType, 10, 20, 4210752);
        if (textureTypeList.getListSelectedIndex() == 0) {
            this.fontRenderer.drawString(usernameLabel, 10, 60, 4210752);
        } else {
            this.fontRenderer.drawString(urlLabel, 10, 60, 4210752);
        }
        super.drawForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        textUserSkin.setText("");
    }
}
