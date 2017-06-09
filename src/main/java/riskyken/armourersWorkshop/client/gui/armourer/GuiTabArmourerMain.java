package riskyken.armourersWorkshop.client.gui.armourer;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiCustomSlider;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
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

public class GuiTabArmourerMain extends GuiTabPanel implements IDropDownListCallback, ISlider {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourer.png");
    
    public TileEntityArmourer tileEntity;
    
    private GuiCheckBox checkShowGuides;
    private GuiCheckBox checkShowOverlay;
    private GuiCheckBox checkShowHelper;
    
    private GuiCheckBox checkBlockGlowing;
    private GuiCheckBox checkBlockLadder;
    private GuiCheckBox checkBlockNoCollision;
    private GuiCheckBox checkBlockSeat;
    private GuiCheckBox checkBlockMultiblock;
    private GuiCheckBox checkBlockBed;
    private GuiCheckBox checkBlockInventory;
    
    private GuiCustomSlider sliderWingIdleSpeed;
    private GuiCustomSlider sliderWingFlyingSpeed;
    private GuiCustomSlider sliderWingMinAngle;
    private GuiCustomSlider sliderWingMaxAngle;
    
    private GuiCheckBox checkArmourOverrideBodyPart;
    
    private GuiTextField textItemName;
    private GuiTextField textUserSkin;
    private boolean loadedArmourItem;
    private SkinProperties skinProps;
    private final String DEGREE  = "\u00b0";
    
    public GuiTabArmourerMain(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        loadedArmourItem = false;
        tileEntity = ((GuiArmourer)parent).tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getInventoryName();
        
        buttonList.clear();
        
        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        GuiDropDownList dropDownList = new GuiDropDownList(0, 5, 16, 50, "", this);
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
        
        skinProps = tileEntity.getSkinProps();
        
        buttonList.add(new GuiButtonExt(13, 86, 16, 50, 12, GuiHelper.getLocalizedControlName(guiName, "save")));
        buttonList.add(new GuiButtonExt(14, 86, 16 + 13, 50, 12, GuiHelper.getLocalizedControlName(guiName, "load")));
        
        checkShowGuides = new GuiCheckBox(7, 64, 118, GuiHelper.getLocalizedControlName(guiName, "showGuide"), tileEntity.isShowGuides());
        checkShowOverlay = new GuiCheckBox(9, 64, 134, GuiHelper.getLocalizedControlName(guiName, "showOverlay"), tileEntity.isShowOverlay());
        checkShowHelper = new GuiCheckBox(6, 64, 134, GuiHelper.getLocalizedControlName(guiName, "showHelper"), tileEntity.isShowHelper());
        
        checkBlockGlowing = new GuiCheckBox(15, 177, 45, GuiHelper.getLocalizedControlName(guiName, "glowing"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_GLOWING, false));
        checkBlockLadder = new GuiCheckBox(15, 177, 60, GuiHelper.getLocalizedControlName(guiName, "ladder"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_LADDER, false));
        checkBlockNoCollision = new GuiCheckBox(15, 177, 75, GuiHelper.getLocalizedControlName(guiName, "noCollision"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_NO_COLLISION, false));
        checkBlockSeat = new GuiCheckBox(15, 177, 90, GuiHelper.getLocalizedControlName(guiName, "seat"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_SEAT, false));
        checkBlockMultiblock = new GuiCheckBox(15, 177, 105, GuiHelper.getLocalizedControlName(guiName, "multiblock"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false));
        checkBlockBed = new GuiCheckBox(15, 177, 120, GuiHelper.getLocalizedControlName(guiName, "bed"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_BED, false));
        checkBlockInventory = new GuiCheckBox(15, 177, 135, GuiHelper.getLocalizedControlName(guiName, "inventory"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_INVENTORY, false));
        if (!checkBlockMultiblock.isChecked()) {
            checkBlockBed.enabled = false;
            checkBlockBed.setIsChecked(false);
        } else {
            checkBlockBed.enabled = true;
        }
        //TODO remove to re-enable beds
        checkBlockBed.enabled = false;
        
        sliderWingIdleSpeed = new GuiCustomSlider(15, 177, 45, 70, 10, "", "ms", 200D, 10000D, skinProps.getPropertyDouble(Skin.KEY_WINGS_IDLE_SPEED, 6000D), false, true, this);
        sliderWingFlyingSpeed = new GuiCustomSlider(15, 177, 65, 70, 10, "", "ms", 200D, 10000D, skinProps.getPropertyDouble(Skin.KEY_WINGS_FLYING_SPEED, 350D), false, true, this);
        sliderWingMinAngle = new GuiCustomSlider(15, 177, 85, 70, 10, "", DEGREE, -90D, 90D, skinProps.getPropertyDouble(Skin.KEY_WINGS_MIN_ANGLE, 0D), false, true, this);
        sliderWingMaxAngle = new GuiCustomSlider(15, 177, 105, 70, 10, "", DEGREE, -90D, 90D, skinProps.getPropertyDouble(Skin.KEY_WINGS_MAX_ANGLE, 75D), false, true, this);
        
        checkArmourOverrideBodyPart = new GuiCheckBox(15, 177, 45, GuiHelper.getLocalizedControlName(guiName, "overrideBodyPart"), skinProps.getPropertyBoolean(Skin.KEY_ARMOUR_OVERRIDE, false));
        
        textItemName = new GuiTextField(fontRenderer, x + 64, y + 58, 103, 16);
        textItemName.setMaxStringLength(40);
        textItemName.setText(tileEntity.getSkinProps().getPropertyString(Skin.KEY_CUSTOM_NAME, ""));
        
        textUserSkin = new GuiTextField(fontRenderer, x + 64, y + 88, 70, 16);
        textUserSkin.setMaxStringLength(30);
        buttonList.add(new GuiButtonExt(8, 138, 88, 30, 16, GuiHelper.getLocalizedControlName(guiName, "set")));
        
        buttonList.add(new GuiButtonExt(10, 177, 16, 70, 16, GuiHelper.getLocalizedControlName(guiName, "clear")));
        
        if (tileEntity.getGameProfile() != null) {
            textUserSkin.setText(tileEntity.getGameProfile().getName());
        }
        
        buttonList.add(checkShowGuides);
        buttonList.add(checkShowOverlay);
        buttonList.add(checkShowHelper);
        
        buttonList.add(checkBlockGlowing);
        buttonList.add(checkBlockLadder);
        buttonList.add(checkBlockNoCollision);
        buttonList.add(checkBlockSeat);
        buttonList.add(checkBlockMultiblock);
        buttonList.add(checkBlockBed);
        buttonList.add(checkBlockInventory);
        
        buttonList.add(sliderWingIdleSpeed);
        buttonList.add(sliderWingFlyingSpeed);
        buttonList.add(sliderWingMinAngle);
        buttonList.add(sliderWingMaxAngle);
        
        buttonList.add(checkArmourOverrideBodyPart);
        
        //buttonList.add(new GuiButtonExt(11, guiLeft + 177, guiTop + 46, 70, 16, GuiHelper.getLocalizedControlName(guiName, "westToEast")));
        //buttonList.add(new GuiButtonExt(12, guiLeft + 177, guiTop + 66, 70, 16, GuiHelper.getLocalizedControlName(guiName, "eastToWest")));
        //buttonList.add(new GuiButtonExt(13, guiLeft + 177, guiTop + 76, 70, 16, "Add Noise"));
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textItemName.mouseClicked(mouseX, mouseY, button);
        textUserSkin.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!textItemName.textboxKeyTyped(c, keycode)) {
            if (!textUserSkin.textboxKeyTyped(c, keycode)) {
                return super.keyTyped(c, keycode);
            }
        } else {
            SkinProperties skinProps = tileEntity.getSkinProps();
            String sendText = textItemName.getText().trim();
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
        skinProps = tileEntity.getSkinProps();
        
        if (!checkBlockMultiblock.isChecked()) {
            checkBlockBed.enabled = false;
            checkBlockBed.setIsChecked(false);
        } else {
            checkBlockBed.enabled = true;
        }
        //TODO remove to re-enable beds
        checkBlockBed.enabled = false;
        
        if (
                button == checkBlockGlowing | button == checkBlockLadder | button == checkBlockNoCollision |
                button == checkBlockSeat | button == checkBlockMultiblock | button == checkBlockBed | button == checkBlockInventory) {
            skinProps.setProperty(Skin.KEY_BLOCK_GLOWING, checkBlockGlowing.isChecked());
            skinProps.setProperty(Skin.KEY_BLOCK_LADDER, checkBlockLadder.isChecked());
            skinProps.setProperty(Skin.KEY_BLOCK_NO_COLLISION, checkBlockNoCollision.isChecked());
            skinProps.setProperty(Skin.KEY_BLOCK_SEAT, checkBlockSeat.isChecked());
            skinProps.setProperty(Skin.KEY_BLOCK_MULTIBLOCK, checkBlockMultiblock.isChecked());
            skinProps.setProperty(Skin.KEY_BLOCK_BED, checkBlockBed.isChecked());
            skinProps.setProperty(Skin.KEY_BLOCK_INVENTORY, checkBlockInventory.isChecked());
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
        
        if (button == checkArmourOverrideBodyPart) {
            skinProps.setProperty(Skin.KEY_ARMOUR_OVERRIDE, checkArmourOverrideBodyPart.isChecked());
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
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        if (loadedArmourItem & tileEntity.loadedArmourItem) {
            skinProps = tileEntity.getSkinProps();
            
            textItemName.setText(skinProps.getPropertyString(Skin.KEY_CUSTOM_NAME, ""));
            checkBlockGlowing.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_GLOWING, false));
            checkBlockLadder.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_LADDER, false));
            checkBlockNoCollision.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_NO_COLLISION, false));
            checkBlockSeat.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_SEAT, false));
            checkBlockMultiblock.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false));
            checkBlockBed.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_BED, false));
            checkBlockInventory.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_BLOCK_INVENTORY, false));
            
            sliderWingMinAngle.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_MIN_ANGLE, 0D));
            sliderWingMinAngle.updateSlider();
            sliderWingMaxAngle.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_MAX_ANGLE, 75D));
            sliderWingMaxAngle.updateSlider();
            sliderWingIdleSpeed.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_IDLE_SPEED, 6000D));
            sliderWingIdleSpeed.updateSlider();
            sliderWingFlyingSpeed.setValue(skinProps.getPropertyDouble(Skin.KEY_WINGS_FLYING_SPEED, 350D));
            sliderWingFlyingSpeed.updateSlider();
            
            checkArmourOverrideBodyPart.setIsChecked(skinProps.getPropertyBoolean(Skin.KEY_ARMOUR_OVERRIDE, false));
            
            tileEntity.loadedArmourItem = false;
            loadedArmourItem = false;
        }
        tileEntity.loadedArmourItem = false;
        
        checkShowGuides.setIsChecked(tileEntity.isShowGuides());
        checkShowOverlay.setIsChecked(tileEntity.isShowOverlay());
        
        int checkY = 134;
        if (tileEntity.getSkinType() != null) {
            checkShowOverlay.visible = tileEntity.getSkinType().showSkinOverlayCheckbox();
            checkShowOverlay.yPosition = checkY;
            if (checkShowOverlay.visible) {
                checkY += 16;
            }
        } else {
            checkShowOverlay.visible = false;
        }
        
        if (tileEntity.getSkinType() != null) {
            checkShowHelper.visible = tileEntity.getSkinType().showHelperCheckbox();
            checkShowHelper.yPosition = checkY;
            //checkY += 16;
        } else {
            checkShowHelper.visible = false;
        }
        
        checkBlockGlowing.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockLadder.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockNoCollision.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockSeat.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockMultiblock.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockBed.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockInventory.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        
        sliderWingIdleSpeed.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingFlyingSpeed.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingMinAngle.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingMaxAngle.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        
        checkArmourOverrideBodyPart.visible = tileEntity.getSkinType().getVanillaArmourSlotId() != -1;
        
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
        textItemName.drawTextBox();
        textUserSkin.drawTextBox();
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        GuiHelper.renderLocalizedGuiName(fontRenderer, this.width, tileEntity.getInventoryName());
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.height - 96 + 2, 4210752);
    
        String itemNameLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.itemName");
        String usernameLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.username");
        String cloneLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.clone");
        String versionLabel = "Alpha: " + LibModInfo.VERSION;
        
        this.fontRenderer.drawString(itemNameLabel, 64, 48, 4210752);
        this.fontRenderer.drawString(usernameLabel, 64, 78, 4210752);
        
        if (tileEntity.getSkinType() == SkinTypeRegistry.skinWings) {
            String idleSpeedLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.idleSpeed");
            String flyingSpeedLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.flyingSpeed");
            String minAngleLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.minAngle");
            String maxAngleLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.maxAngle");
            
            this.fontRenderer.drawString(idleSpeedLabel, 177, 36, 4210752);
            this.fontRenderer.drawString(flyingSpeedLabel, 177, 56, 4210752);
            this.fontRenderer.drawString(minAngleLabel, 177, 76, 4210752);
            this.fontRenderer.drawString(maxAngleLabel, 177, 96, 4210752);
        }
        
        int versionWidth = fontRenderer.getStringWidth(versionLabel);
        this.fontRenderer.drawString(versionLabel, this.width - versionWidth - 4, this.height - 96, 4210752);
        //this.fontRendererObj.drawString(cloneLabel, 177, 36, 4210752);
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
