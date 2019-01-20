package moe.plushie.armourers_workshop.client.gui.armourer.tab;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.armourer.GuiArmourer;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiInventorySize;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinProps;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.type.wings.SkinWings.MovementType;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerSkinSettings extends GuiTabPanel implements ISlider, IDropDownListCallback {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.ARMOURER);
    private static final String DEGREE  = "\u00b0";
    
    private final TileEntityArmourer tileEntity;
    
    private GuiCheckBox checkBlockGlowing;
    private GuiCheckBox checkBlockLadder;
    private GuiCheckBox checkBlockNoCollision;
    private GuiCheckBox checkBlockSeat;
    private GuiCheckBox checkBlockMultiblock;
    private GuiCheckBox checkBlockBed;
    private GuiCheckBox checkBlockInventory;
    private GuiInventorySize inventorySize;
    private GuiCheckBox checkBlockEnderInventory;
    
    private GuiCustomSlider sliderWingIdleSpeed;
    private GuiCustomSlider sliderWingFlyingSpeed;
    private GuiCustomSlider sliderWingMinAngle;
    private GuiCustomSlider sliderWingMaxAngle;
    
    private GuiCheckBox checkModelOverrideHead;
    private GuiCheckBox checkModelOverrideChest;
    private GuiCheckBox checkModelOverrideArmLeft;
    private GuiCheckBox checkModelOverrideArmRight;
    private GuiCheckBox checkModelOverrideLegLeft;
    private GuiCheckBox checkModelOverrideLegRight;
    
    private GuiCheckBox checkArmourHideOverlay;
    private GuiCheckBox checkLimitLimbMovement;
    private GuiDropDownList dropDownList;
    
    private boolean resetting;
    
    public GuiTabArmourerSkinSettings(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer)parent).tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getName();
        
        buttonList.clear();
        
        SkinProperties skinProps = tileEntity.getSkinProps();
        checkBlockGlowing = new GuiCheckBox(15, 10, 20, GuiHelper.getLocalizedControlName(guiName, "glowing"), SkinProperties.PROP_BLOCK_GLOWING.getValue(skinProps));
        checkBlockLadder = new GuiCheckBox(15, 10, 35, GuiHelper.getLocalizedControlName(guiName, "ladder"), SkinProperties.PROP_BLOCK_LADDER.getValue(skinProps));
        checkBlockNoCollision = new GuiCheckBox(15, 10, 50, GuiHelper.getLocalizedControlName(guiName, "noCollision"), SkinProperties.PROP_BLOCK_NO_COLLISION.getValue(skinProps));
        checkBlockSeat = new GuiCheckBox(15, 10, 65, GuiHelper.getLocalizedControlName(guiName, "seat"), SkinProperties.PROP_BLOCK_SEAT.getValue(skinProps));
        checkBlockMultiblock = new GuiCheckBox(15, 10, 80, GuiHelper.getLocalizedControlName(guiName, "multiblock"), SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps));
        checkBlockBed = new GuiCheckBox(15, 22, 95, GuiHelper.getLocalizedControlName(guiName, "bed"), SkinProperties.PROP_BLOCK_BED.getValue(skinProps));
        checkBlockEnderInventory = new GuiCheckBox(15, 10, 110, GuiHelper.getLocalizedControlName(guiName, "enderInventory"), SkinProperties.PROP_BLOCK_ENDER_INVENTORY.getValue(skinProps));
        checkBlockInventory = new GuiCheckBox(15, 10, 125, GuiHelper.getLocalizedControlName(guiName, "inventory"), SkinProperties.PROP_BLOCK_INVENTORY.getValue(skinProps));
        if (!checkBlockMultiblock.isChecked()) {
            checkBlockBed.enabled = false;
            checkBlockBed.setIsChecked(false);
        } else {
            checkBlockBed.enabled = true;
        }
        //TODO remove to re-enable beds
        checkBlockBed.enabled = false;
        
        checkBlockEnderInventory.enabled = !checkBlockInventory.isChecked();
        checkBlockInventory.enabled = !checkBlockEnderInventory.isChecked();
        if (checkBlockInventory.isChecked()) {
            checkBlockEnderInventory.setIsChecked(false);
        }
        if (checkBlockEnderInventory.isChecked()) {
            checkBlockInventory.setIsChecked(false);
        }
        
        inventorySize = new GuiInventorySize(10, 158, 9, 6);
        inventorySize.setSrc(TEXTURE, 176, 0);
        inventorySize.setSelection(SkinProperties.PROP_BLOCK_INVENTORY_WIDTH.getValue(skinProps), SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT.getValue(skinProps));
        
        
        sliderWingIdleSpeed = new GuiCustomSlider(15, 10, 45, 154, 10, "", "ms", 200D, 10000D, SkinProperties.PROP_WINGS_IDLE_SPEED.getValue(skinProps), false, true, this);
        sliderWingFlyingSpeed = new GuiCustomSlider(15, 10, 65, 154, 10, "", "ms", 200D, 10000D, SkinProperties.PROP_WINGS_FLYING_SPEED.getValue(skinProps), false, true, this);
        sliderWingMinAngle = new GuiCustomSlider(15, 10, 85, 154, 10, "", DEGREE, -180D, 180D, SkinProperties.PROP_WINGS_MIN_ANGLE.getValue(skinProps), false, true, this);
        sliderWingMaxAngle = new GuiCustomSlider(15, 10, 105, 154, 10, "", DEGREE, -180D, 180D, SkinProperties.PROP_WINGS_MAX_ANGLE.getValue(skinProps), false, true, this);
        
        sliderWingIdleSpeed.setFineTuneButtons(true);
        sliderWingFlyingSpeed.setFineTuneButtons(true);
        sliderWingMinAngle.setFineTuneButtons(true);
        sliderWingMaxAngle.setFineTuneButtons(true);
        
        checkModelOverrideHead = new GuiCheckBox(15, 10, 35, GuiHelper.getLocalizedControlName(guiName, "modelOverrideHead"), SkinProperties.PROP_OVERRIDE_MODEL_HEAD.getValue(skinProps));
        checkModelOverrideChest = new GuiCheckBox(15, 10, 35, GuiHelper.getLocalizedControlName(guiName, "modelOverrideChest"), SkinProperties.PROP_OVERRIDE_MODEL_CHEST.getValue(skinProps));
        checkModelOverrideArmLeft = new GuiCheckBox(15, 10, 50, GuiHelper.getLocalizedControlName(guiName, "modelOverrideArmLeft"), SkinProperties.PROP_OVERRIDE_MODEL_ARM_LEFT.getValue(skinProps));
        checkModelOverrideArmRight = new GuiCheckBox(15, 10, 65, GuiHelper.getLocalizedControlName(guiName, "modelOverrideArmRight"), SkinProperties.PROP_OVERRIDE_MODEL_ARM_RIGHT.getValue(skinProps));
        checkModelOverrideLegLeft = new GuiCheckBox(15, 10, 35, GuiHelper.getLocalizedControlName(guiName, "modelOverrideLegLeft"), SkinProperties.PROP_OVERRIDE_MODEL_LEG_LEFT.getValue(skinProps));
        checkModelOverrideLegRight = new GuiCheckBox(15, 10, 50, GuiHelper.getLocalizedControlName(guiName, "modelOverrideLegRight"), SkinProperties.PROP_OVERRIDE_MODEL_LEG_RIGHT.getValue(skinProps));
        
        checkArmourHideOverlay = new GuiCheckBox(15, 10, 20, GuiHelper.getLocalizedControlName(guiName, "hideOverlay"), SkinProperties.PROP_ARMOUR_HIDE_OVERLAY.getValue(skinProps));
        checkLimitLimbMovement = new GuiCheckBox(15, 10, 65, GuiHelper.getLocalizedControlName(guiName, "limitLimbs"), SkinProperties.PROP_ARMOUR_LIMIT_LIMBS.getValue(skinProps));
        
        MovementType skinMovmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skinProps));
        dropDownList = new GuiDropDownList(0, 10, 125, 50, "", this);
        for (int i = 0; i < MovementType.values().length; i++) {
            MovementType movementType = MovementType.values()[i];
            String unlocalizedName = "movmentType." + LibModInfo.ID.toLowerCase() + ":" + movementType.name().toLowerCase();
            String localizedName = I18n.format(unlocalizedName);
            dropDownList.addListItem(localizedName, movementType.name(), true);
            if (movementType == skinMovmentType) {
                dropDownList.setListSelectedIndex(i);
            }
        }
        
        buttonList.add(checkBlockGlowing);
        buttonList.add(checkBlockLadder);
        buttonList.add(checkBlockNoCollision);
        buttonList.add(checkBlockSeat);
        buttonList.add(checkBlockMultiblock);
        buttonList.add(checkBlockBed);
        buttonList.add(checkBlockInventory);
        buttonList.add(checkBlockEnderInventory);
        buttonList.add(inventorySize);
        
        buttonList.add(sliderWingIdleSpeed);
        buttonList.add(sliderWingFlyingSpeed);
        buttonList.add(sliderWingMinAngle);
        buttonList.add(sliderWingMaxAngle);
        
        buttonList.add(checkModelOverrideHead);
        buttonList.add(checkModelOverrideChest);
        buttonList.add(checkModelOverrideArmLeft);
        buttonList.add(checkModelOverrideArmRight);
        buttonList.add(checkModelOverrideLegLeft);
        buttonList.add(checkModelOverrideLegRight);
        
        buttonList.add(checkArmourHideOverlay);
        buttonList.add(checkLimitLimbMovement);
        buttonList.add(dropDownList);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        SkinProperties skinProps = tileEntity.getSkinProps();
        
        if (!checkBlockMultiblock.isChecked()) {
            checkBlockBed.enabled = false;
            checkBlockBed.setIsChecked(false);
        } else {
            checkBlockBed.enabled = true;
        }
        
        
        checkBlockEnderInventory.enabled = !checkBlockInventory.isChecked();
        checkBlockInventory.enabled = !checkBlockEnderInventory.isChecked();
        if (checkBlockInventory.isChecked()) {
            checkBlockEnderInventory.setIsChecked(false);
        }
        if (checkBlockEnderInventory.isChecked()) {
            checkBlockInventory.setIsChecked(false);
        }
        
        //TODO remove to re-enable beds
        checkBlockBed.enabled = false;
        
        if (
                button == checkBlockGlowing | button == checkBlockLadder | button == checkBlockNoCollision |
                button == checkBlockSeat | button == checkBlockMultiblock | button == checkBlockBed | button == checkBlockInventory |
                button == inventorySize | button == checkBlockEnderInventory) {
            SkinProperties.PROP_BLOCK_GLOWING.setValue(skinProps, checkBlockGlowing.isChecked());
            SkinProperties.PROP_BLOCK_LADDER.setValue(skinProps, checkBlockLadder.isChecked());
            SkinProperties.PROP_BLOCK_NO_COLLISION.setValue(skinProps, checkBlockNoCollision.isChecked());
            SkinProperties.PROP_BLOCK_SEAT.setValue(skinProps, checkBlockSeat.isChecked());
            SkinProperties.PROP_BLOCK_MULTIBLOCK.setValue(skinProps, checkBlockMultiblock.isChecked());
            SkinProperties.PROP_BLOCK_BED.setValue(skinProps, checkBlockBed.isChecked());
            SkinProperties.PROP_BLOCK_INVENTORY.setValue(skinProps, checkBlockInventory.isChecked());
            SkinProperties.PROP_BLOCK_ENDER_INVENTORY.setValue(skinProps, checkBlockEnderInventory.isChecked());
            SkinProperties.PROP_BLOCK_INVENTORY_WIDTH.setValue(skinProps, inventorySize.getSelectionWidth());
            SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT.setValue(skinProps, inventorySize.getSelectionHeight());
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
        
        if (button == checkModelOverrideHead | button == checkModelOverrideChest | button == checkModelOverrideArmLeft | button == checkModelOverrideArmRight |
                button == checkModelOverrideLegLeft | button == checkModelOverrideLegRight | button == checkArmourHideOverlay | button == checkLimitLimbMovement) {
            SkinProperties.PROP_OVERRIDE_MODEL_HEAD.setValue(skinProps, checkModelOverrideHead.isChecked());
            SkinProperties.PROP_OVERRIDE_MODEL_CHEST.setValue(skinProps, checkModelOverrideChest.isChecked());
            SkinProperties.PROP_OVERRIDE_MODEL_ARM_LEFT.setValue(skinProps, checkModelOverrideArmLeft.isChecked());
            SkinProperties.PROP_OVERRIDE_MODEL_ARM_RIGHT.setValue(skinProps, checkModelOverrideArmRight.isChecked());
            SkinProperties.PROP_OVERRIDE_MODEL_LEG_LEFT.setValue(skinProps, checkModelOverrideLegLeft.isChecked());
            SkinProperties.PROP_OVERRIDE_MODEL_LEG_RIGHT.setValue(skinProps, checkModelOverrideLegRight.isChecked());
            
            SkinProperties.PROP_ARMOUR_HIDE_OVERLAY.setValue(skinProps, checkArmourHideOverlay.isChecked());
            SkinProperties.PROP_ARMOUR_LIMIT_LIMBS.setValue(skinProps, checkLimitLimbMovement.isChecked());
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
        
        inventorySize.visible = checkBlockInventory.isChecked();
    }
    
    public void resetValues(SkinProperties skinProperties) {
        resetting = true;
        checkBlockGlowing.setIsChecked(SkinProperties.PROP_BLOCK_GLOWING.getValue(skinProperties));
        checkBlockLadder.setIsChecked(SkinProperties.PROP_BLOCK_LADDER.getValue(skinProperties));
        checkBlockNoCollision.setIsChecked(SkinProperties.PROP_BLOCK_NO_COLLISION.getValue(skinProperties));
        checkBlockSeat.setIsChecked(SkinProperties.PROP_BLOCK_SEAT.getValue(skinProperties));
        checkBlockMultiblock.setIsChecked(SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProperties));
        checkBlockBed.setIsChecked(SkinProperties.PROP_BLOCK_BED.getValue(skinProperties));
        checkBlockInventory.setIsChecked(SkinProperties.PROP_BLOCK_INVENTORY.getValue(skinProperties));
        checkBlockEnderInventory.setIsChecked(SkinProperties.PROP_BLOCK_ENDER_INVENTORY.getValue(skinProperties));
        inventorySize.setSelection(SkinProperties.PROP_BLOCK_INVENTORY_WIDTH.getValue(skinProperties), SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT.getValue(skinProperties));
        inventorySize.visible = checkBlockInventory.isChecked();
        
        sliderWingMinAngle.setValue(SkinProperties.PROP_WINGS_MIN_ANGLE.getValue(skinProperties));
        sliderWingMinAngle.updateSlider();
        sliderWingMaxAngle.setValue(SkinProperties.PROP_WINGS_MAX_ANGLE.getValue(skinProperties));
        sliderWingMaxAngle.updateSlider();
        sliderWingIdleSpeed.setValue(SkinProperties.PROP_WINGS_IDLE_SPEED.getValue(skinProperties));
        sliderWingIdleSpeed.updateSlider();
        sliderWingFlyingSpeed.setValue(SkinProperties.PROP_WINGS_FLYING_SPEED.getValue(skinProperties));
        sliderWingFlyingSpeed.updateSlider();
        
        checkModelOverrideHead.setIsChecked(SkinProperties.PROP_OVERRIDE_MODEL_HEAD.getValue(skinProperties));
        checkModelOverrideChest.setIsChecked(SkinProperties.PROP_OVERRIDE_MODEL_CHEST.getValue(skinProperties));
        checkModelOverrideArmLeft.setIsChecked(SkinProperties.PROP_OVERRIDE_MODEL_ARM_LEFT.getValue(skinProperties));
        checkModelOverrideArmRight.setIsChecked(SkinProperties.PROP_OVERRIDE_MODEL_ARM_RIGHT.getValue(skinProperties));
        checkModelOverrideLegLeft.setIsChecked(SkinProperties.PROP_OVERRIDE_MODEL_LEG_LEFT.getValue(skinProperties));
        checkModelOverrideLegRight.setIsChecked(SkinProperties.PROP_OVERRIDE_MODEL_LEG_RIGHT.getValue(skinProperties));
        
        checkArmourHideOverlay.setIsChecked(SkinProperties.PROP_ARMOUR_HIDE_OVERLAY.getValue(skinProperties));
        checkLimitLimbMovement.setIsChecked(SkinProperties.PROP_ARMOUR_LIMIT_LIMBS.getValue(skinProperties));
        
        MovementType skinMovmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skinProperties));
        for (int i = 0; i < MovementType.values().length; i++) {
            MovementType movementType = MovementType.values()[i];
            if (movementType == skinMovmentType) {
                dropDownList.setListSelectedIndex(i);
            }
        }
        
        resetting = false;
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
        drawTexturedModalRect(this.x + 7, this.y + 141, 7, 3, 162, 76);
        checkBlockGlowing.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockLadder.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockNoCollision.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockSeat.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockMultiblock.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockBed.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockInventory.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        checkBlockEnderInventory.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock;
        inventorySize.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinBlock & checkBlockInventory.isChecked();
        
        sliderWingIdleSpeed.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingFlyingSpeed.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingMinAngle.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        sliderWingMaxAngle.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        dropDownList.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;
        
        checkModelOverrideHead.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_OVERRIDE_MODEL_HEAD);
        checkModelOverrideChest.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_OVERRIDE_MODEL_CHEST);
        checkModelOverrideArmLeft.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_OVERRIDE_MODEL_ARM_LEFT);
        checkModelOverrideArmRight.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_OVERRIDE_MODEL_ARM_RIGHT);
        checkModelOverrideLegLeft.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_OVERRIDE_MODEL_LEG_LEFT);
        checkModelOverrideLegRight.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_OVERRIDE_MODEL_LEG_RIGHT);
        
        checkArmourHideOverlay.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_ARMOUR_HIDE_OVERLAY);
        checkLimitLimbMovement.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_ARMOUR_LIMIT_LIMBS);
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        
        if (tileEntity.getSkinType() == SkinTypeRegistry.skinWings) {
            String idleSpeedLabel = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.idleSpeed");
            String flyingSpeedLabel = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.flyingSpeed");
            String minAngleLabel = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.minAngle");
            String maxAngleLabel = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.maxAngle");
            
            this.fontRenderer.drawString(idleSpeedLabel, 10, 36, 4210752);
            this.fontRenderer.drawString(flyingSpeedLabel, 10, 56, 4210752);
            this.fontRenderer.drawString(minAngleLabel, 10, 76, 4210752);
            this.fontRenderer.drawString(maxAngleLabel, 10, 96, 4210752);
        }
        
        if (tileEntity.getSkinType() == SkinTypeRegistry.skinBlock & checkBlockInventory.isChecked()) {
            String labelInventorySize = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.inventorySize");
            
            String labelInventorySlots = "inventory." + LibModInfo.ID.toLowerCase() + ":" + tileEntity.getName() + ".label.inventorySlots";
            labelInventorySlots = I18n.format(labelInventorySlots, inventorySize.getSelectionWidth() * inventorySize.getSelectionHeight(), inventorySize.getSelectionWidth(), inventorySize.getSelectionHeight());
            
            this.fontRenderer.drawString(labelInventorySize, 10, 140, 4210752);
            this.fontRenderer.drawString(labelInventorySlots, 10, 150, 4210752);
        }
        
        dropDownList.drawForeground(mc, mouseX - x, mouseY - y, partialTickTime);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!resetting) {
            SkinProperties skinProps = tileEntity.getSkinProps();
            
            SkinProperties.PROP_WINGS_IDLE_SPEED.setValue(skinProps, (double) Math.round(sliderWingIdleSpeed.getValue()));
            SkinProperties.PROP_WINGS_FLYING_SPEED.setValue(skinProps, (double) Math.round(sliderWingFlyingSpeed.getValue()));
            SkinProperties.PROP_WINGS_MIN_ANGLE.setValue(skinProps, (double) Math.round(sliderWingMinAngle.getValue()));
            SkinProperties.PROP_WINGS_MAX_ANGLE.setValue(skinProps, (double) Math.round(sliderWingMaxAngle.getValue()));
            
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        SkinProperties skinProps = tileEntity.getSkinProps();
        SkinProperties.PROP_WINGS_MOVMENT_TYPE.setValue(skinProps, dropDownList.getListSelectedItem().tag);
        ModLogger.log("Setting skin movment type to: " + dropDownList.getListSelectedItem().tag);
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
    }
}
