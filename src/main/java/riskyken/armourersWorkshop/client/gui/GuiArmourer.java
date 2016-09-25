package riskyken.armourersWorkshop.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiCustomSlider;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerSkinProps;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetSkin;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientLoadArmour;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiArmourer extends GuiContainer implements IDropDownListCallback, ISlider {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourer.png");
    
    private TileEntityArmourer armourerBrain;
    private GuiCheckBox checkShowGuides;
    private GuiCheckBox checkShowOverlay;
    private GuiCheckBox checkShowHelper;
    private GuiCheckBox checkBlockGlowing;
    private GuiCustomSlider sliderWingIdleSpeed;
    private GuiCustomSlider sliderWingFlyingSpeed;
    private GuiCustomSlider sliderWingMinAngle;
    private GuiCustomSlider sliderWingMaxAngle;
    private GuiTextField textItemName;
    private GuiTextField textUserSkin;
    private boolean loadedArmourItem;
    private SkinProperties skinProps;
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourer armourerBrain) {
        super(new ContainerArmourer(invPlayer, armourerBrain));
        this.armourerBrain = armourerBrain;
        this.xSize = 256;
        this.ySize = 256;
        loadedArmourItem = false;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = armourerBrain.getInventoryName();
        
        buttonList.clear();
        
        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        GuiDropDownList dropDownList = new GuiDropDownList(0, guiLeft + 5, guiTop + 16, 50, "", this);
        ArrayList<ISkinType> skinList = str.getRegisteredSkinTypes();
        int skinCount = 0;
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (!skinType.isHidden()) {
                String skinLocalizedName = str.getLocalizedSkinTypeName(skinType);
                String skinRegistryName = skinType.getRegistryName();
                dropDownList.addListItem(skinLocalizedName, skinRegistryName, skinType.enabled());
                if (skinType == armourerBrain.getSkinType()) {
                    dropDownList.setListSelectedIndex(skinCount);
                }
                skinCount++;
            }
        }
        buttonList.add(dropDownList);
        
        skinProps = armourerBrain.getSkinProps();
        
        buttonList.add(new GuiButtonExt(13, guiLeft + 86, guiTop + 16, 50, 12, GuiHelper.getLocalizedControlName(guiName, "save")));
        buttonList.add(new GuiButtonExt(14, guiLeft + 86, guiTop + 16 + 13, 50, 12, GuiHelper.getLocalizedControlName(guiName, "load")));
        
        checkShowGuides = new GuiCheckBox(7, guiLeft + 64, guiTop + 118, GuiHelper.getLocalizedControlName(guiName, "showGuide"), armourerBrain.isShowGuides());
        checkShowOverlay = new GuiCheckBox(9, guiLeft + 64, guiTop + 134, GuiHelper.getLocalizedControlName(guiName, "showOverlay"), armourerBrain.isShowOverlay());
        checkShowHelper = new GuiCheckBox(6, guiLeft + 64, guiTop + 134, GuiHelper.getLocalizedControlName(guiName, "showHelper"), armourerBrain.isShowHelper());
        checkBlockGlowing = new GuiCheckBox(15, guiLeft + 64, guiTop + 134, GuiHelper.getLocalizedControlName(guiName, "glowing"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_GLOWING, false));
        
        sliderWingIdleSpeed = new GuiCustomSlider(15, guiLeft + 177, guiTop + 45, 70, 10, "", "ms", 200D, 10000D, skinProps.getPropertyDouble(Skin.KEY_WINGS_IDLE_SPEED, 6000D), false, true, this);
        sliderWingFlyingSpeed = new GuiCustomSlider(15, guiLeft + 177, guiTop + 65, 70, 10, "", "ms", 200D, 10000D, skinProps.getPropertyDouble(Skin.KEY_WINGS_FLYING_SPEED, 350D), false, true, this);
        sliderWingMinAngle = new GuiCustomSlider(15, guiLeft + 177, guiTop + 85, 70, 10, "", "°", -90D, 90D, skinProps.getPropertyDouble(Skin.KEY_WINGS_MIN_ANGLE, 0D), false, true, this);
        sliderWingMaxAngle = new GuiCustomSlider(15, guiLeft + 177, guiTop + 105, 70, 10, "", "°", -90D, 90D, skinProps.getPropertyDouble(Skin.KEY_WINGS_MAX_ANGLE, 75D), false, true, this);
        
        textItemName = new GuiTextField(fontRendererObj, guiLeft + 64, guiTop + 58, 103, 16);
        textItemName.setMaxStringLength(40);
        textItemName.setText(armourerBrain.getSkinProps().getPropertyString(Skin.KEY_CUSTOM_NAME, ""));
        
        textUserSkin = new GuiTextField(fontRendererObj, guiLeft + 64, guiTop + 88, 70, 16);
        textUserSkin.setMaxStringLength(30);
        buttonList.add(new GuiButtonExt(8, guiLeft + 138, guiTop + 88, 30, 16, GuiHelper.getLocalizedControlName(guiName, "set")));
        
        buttonList.add(new GuiButtonExt(10, guiLeft + 177, guiTop + 16, 70, 16, GuiHelper.getLocalizedControlName(guiName, "clear")));
        
        if (armourerBrain.getGameProfile() != null) {
            textUserSkin.setText(armourerBrain.getGameProfile().getName());
        }
        
        buttonList.add(checkShowGuides);
        buttonList.add(checkShowOverlay);
        buttonList.add(checkShowHelper);
        buttonList.add(checkBlockGlowing);
        buttonList.add(sliderWingIdleSpeed);
        buttonList.add(sliderWingFlyingSpeed);
        buttonList.add(sliderWingMinAngle);
        buttonList.add(sliderWingMaxAngle);
        //buttonList.add(new GuiButtonExt(11, guiLeft + 177, guiTop + 46, 70, 16, GuiHelper.getLocalizedControlName(guiName, "westToEast")));
        //buttonList.add(new GuiButtonExt(12, guiLeft + 177, guiTop + 66, 70, 16, GuiHelper.getLocalizedControlName(guiName, "eastToWest")));
        //buttonList.add(new GuiButtonExt(13, guiLeft + 177, guiTop + 76, 70, 16, "Add Noise"));
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
            SkinProperties skinProps = armourerBrain.getSkinProps();
            String sendText = textItemName.getText().trim();
            String oldText = skinProps.getPropertyString(Skin.KEY_CUSTOM_NAME, "");
            if (!sendText.equals(oldText)) {
                skinProps.setProperty(Skin.KEY_CUSTOM_NAME, sendText);
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
            }
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        skinProps = armourerBrain.getSkinProps();
        if (button == checkBlockGlowing) {
            skinProps.setProperty(Skin.KEY_BLOCK_GLOWING, checkBlockGlowing.isChecked());
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
        switch (button.id) {
        case 13:
            PacketHandler.networkWrapper.sendToServer(new MessageClientLoadArmour(textItemName.getText().trim(), ""));
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
            }
            if (button.id == 10) {
                loadedArmourItem = true;
                skinProps = new SkinProperties();
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
        String versionLabel = "Alpha: " + LibModInfo.VERSION;
        
        this.fontRendererObj.drawString(itemNameLabel, 64, 48, 4210752);
        this.fontRendererObj.drawString(usernameLabel, 64, 78, 4210752);
        
        if (armourerBrain.getSkinType() == SkinTypeRegistry.skinWings) {
            String idleSpeedLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.idleSpeed");
            String flyingSpeedLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.flyingSpeed");
            String minAngleLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.minAngle");
            String maxAngleLabel = GuiHelper.getLocalizedControlName(armourerBrain.getInventoryName(), "label.maxAngle");
            
            this.fontRendererObj.drawString(idleSpeedLabel, 177, 36, 4210752);
            this.fontRendererObj.drawString(flyingSpeedLabel, 177, 56, 4210752);
            this.fontRendererObj.drawString(minAngleLabel, 177, 76, 4210752);
            this.fontRendererObj.drawString(maxAngleLabel, 177, 96, 4210752);
        }
        
        int versionWidth = fontRendererObj.getStringWidth(versionLabel);
        this.fontRendererObj.drawString(versionLabel, this.xSize - versionWidth - 4, this.ySize - 96, 4210752);
        //this.fontRendererObj.drawString(cloneLabel, 177, 36, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        if (loadedArmourItem & armourerBrain.loadedArmourItem) {
            skinProps = armourerBrain.getSkinProps();
            
            textItemName.setText(skinProps.getPropertyString(Skin.KEY_CUSTOM_NAME, ""));
            checkBlockGlowing.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_GLOWING, false));
            
            sliderWingMinAngle.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_MIN_ANGLE, 0D));
            sliderWingMinAngle.updateSlider();
            sliderWingMaxAngle.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_MAX_ANGLE, 75D));
            sliderWingMaxAngle.updateSlider();
            sliderWingIdleSpeed.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_IDLE_SPEED, 6000D));
            sliderWingIdleSpeed.updateSlider();
            sliderWingFlyingSpeed.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_FLYING_SPEED, 350D));
            sliderWingFlyingSpeed.updateSlider();
            
            armourerBrain.loadedArmourItem = false;
            loadedArmourItem = false;
        }
        armourerBrain.loadedArmourItem = false;
        
        checkShowGuides.setIsChecked(armourerBrain.isShowGuides());
        checkShowOverlay.setIsChecked(armourerBrain.isShowOverlay());
        
        int checkY = 134;
        if (armourerBrain.getSkinType() != null) {
            checkShowOverlay.visible = armourerBrain.getSkinType().showSkinOverlayCheckbox();
            checkShowOverlay.yPosition = guiTop + checkY;
            if (checkShowOverlay.visible) {
                checkY += 16;
            }
        } else {
            checkShowOverlay.visible = false;
        }
        
        if (armourerBrain.getSkinType() != null) {
            checkShowHelper.visible = armourerBrain.getSkinType().showHelperCheckbox();
            checkShowHelper.yPosition = guiTop + checkY;
            //checkY += 16;
        } else {
            checkShowHelper.visible = false;
        }
        
        checkBlockGlowing.visible = armourerBrain.getSkinType() == SkinTypeRegistry.skinBlock;
        
        sliderWingIdleSpeed.visible = armourerBrain.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingFlyingSpeed.visible = armourerBrain.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingMinAngle.visible = armourerBrain.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingMaxAngle.visible = armourerBrain.getSkinType() == SkinTypeRegistry.skinWings;
        
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        textItemName.drawTextBox();
        textUserSkin.drawTextBox();
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        DropDownListItem listItem = dropDownList.getListSelectedItem();
        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(listItem.tag);
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinType(skinType));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!loadedArmourItem) {
            skinProps.setProperty(Skin.KEY_WINGS_IDLE_SPEED, (double)Math.round(sliderWingIdleSpeed.getValue()));
            skinProps.setProperty(Skin.KEY_WINGS_FLYING_SPEED, (double)Math.round(sliderWingFlyingSpeed.getValue()));
            skinProps.setProperty(Skin.KEY_WINGS_MIN_ANGLE, (double)Math.round(sliderWingMinAngle.getValue()));
            skinProps.setProperty(Skin.KEY_WINGS_MAX_ANGLE, (double)Math.round(sliderWingMaxAngle.getValue()));
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
    }
}
