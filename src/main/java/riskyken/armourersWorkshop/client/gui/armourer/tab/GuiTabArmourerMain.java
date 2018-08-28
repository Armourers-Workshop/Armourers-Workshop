package riskyken.armourersWorkshop.client.gui.armourer.tab;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.armourer.GuiArmourer;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerSkinProps;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientLoadArmour;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerMain extends GuiTabPanel implements IDropDownListCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.ARMOURER);
    
    private final TileEntityArmourer tileEntity;
    
    private GuiTextField textItemName;
    private boolean resetting;
    
    public GuiTabArmourerMain(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer)parent).tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getInventoryName();
        
        buttonList.clear();
        
        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        GuiDropDownList dropDownList = new GuiDropDownList(0, 10, 20, 50, "", this);
        ArrayList<ISkinType> skinList = str.getRegisteredSkinTypes();
        int skinCount = 0;
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (!skinType.isHidden()) {
                String skinLocalizedName = str.getLocalizedSkinTypeName(skinType);
                String skinRegistryName = skinType.getRegistryName();
                dropDownList.addListItem(skinLocalizedName, skinRegistryName, skinType.enabled());
                if (skinType == tileEntity.getSkinType()) {
                    dropDownList.setListSelectedIndex(skinCount);
                }
                skinCount++;
            }
        }
        buttonList.add(dropDownList);
        
        buttonList.add(new GuiButtonExt(13, 86, 16, 50, 12, GuiHelper.getLocalizedControlName(guiName, "save")));
        buttonList.add(new GuiButtonExt(14, 86, 16 + 13, 50, 12, GuiHelper.getLocalizedControlName(guiName, "load")));
        
        textItemName = new GuiTextField(-1, fontRenderer, x + 64, y + 58, 103, 16);
        textItemName.setMaxStringLength(40);
        textItemName.setText(tileEntity.getSkinProps().getPropertyString(Skin.KEY_CUSTOM_NAME, ""));
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textItemName.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            if (textItemName.isFocused()) {
                textItemName.setText("");
            }
        }
    }
    
    int fidgCount = 0;
    String[] fidgMessage = new String[] {"STOP!", "STOP ALREADY", "I SAID STOP!"};
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!textItemName.textboxKeyTyped(c, keycode)) {
            return super.keyTyped(c, keycode);
        } else {
            SkinProperties skinProps = tileEntity.getSkinProps();
            String sendText = textItemName.getText().trim();
            if (fidgCount < 3) {
                if (sendText.equalsIgnoreCase("fidget spinner")) {
                    sendText = fidgMessage[fidgCount];
                    fidgCount++;
                }
            }
            
            String oldText = skinProps.getPropertyString(Skin.KEY_CUSTOM_NAME, "");
            if (!sendText.equals(oldText)) {
                skinProps.setProperty(Skin.KEY_CUSTOM_NAME, sendText);
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        case 13:
            PacketHandler.networkWrapper.sendToServer(new MessageClientLoadArmour(textItemName.getText().trim(), ""));
            break;
        default:
            if (button.id == 14) {
                // Load
                // loadedArmourItem = true;
            }
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
            break;
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
        // input slot
        drawTexturedModalRect(this.x + 63, this.y + 20, 238, 0, 18, 18);
        // output slot
        drawTexturedModalRect(this.x + 142, this.y + 16, 230, 18, 26, 26);
        textItemName.drawTextBox();
    }
    
    public void resetValues(SkinProperties skinProperties) {
        resetting = true;
        String newText = skinProperties.getPropertyString(Skin.KEY_CUSTOM_NAME, "");
        if (!textItemName.getText().startsWith(newText)) {
            int cur = textItemName.getCursorPosition();
            textItemName.setText(newText);
            textItemName.setCursorPosition(cur);
        }
        resetting = false;
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.height - 96 + 2, 4210752);
    
        String itemNameLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.itemName");
        
        String cloneLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.clone");
        String versionLabel = "Beta: " + LibModInfo.VERSION;
        
        this.fontRenderer.drawString(itemNameLabel, 64, 48, 4210752);
        
        int versionWidth = fontRenderer.getStringWidth(versionLabel);
        this.fontRenderer.drawString(versionLabel, this.width - versionWidth - 7, this.height - 96 + 2, 4210752);
    }
    
    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        DropDownListItem listItem = dropDownList.getListSelectedItem();
        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(listItem.tag);
        ((GuiArmourer)parent).skinTypeUpdate(skinType);
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinType(skinType));
    }
}
