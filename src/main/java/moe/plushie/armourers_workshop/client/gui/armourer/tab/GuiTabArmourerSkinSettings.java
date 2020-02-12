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

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.GUI_ARMOURER);
    private static final String DEGREE = "\u00b0";
    private static final int SYNC_ID_BLOCK = 15;
    private static final int SYNC_ID_MODEL = 16;
    private static final int SYNC_ID_WINGS = 17;

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

    private GuiCheckBox checkHideOverlayHead;
    private GuiCheckBox checkHideOverlayChest;
    private GuiCheckBox checkHideOverlayArmLeft;
    private GuiCheckBox checkHideOverlayArmRight;
    private GuiCheckBox checkHideOverlayLegLeft;
    private GuiCheckBox checkHideOverlayLegRight;

    private GuiCheckBox checkLimitLimbMovement;
    private GuiDropDownList dropDownMovementType;

    private boolean resetting;

    public GuiTabArmourerSkinSettings(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer) parent).tileEntity;
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getName();

        buttonList.clear();

        SkinProperties skinProps = tileEntity.getSkinProps();
        checkBlockGlowing = new GuiCheckBox(SYNC_ID_BLOCK, 10, 20, GuiHelper.getLocalizedControlName(guiName, "glowing"), SkinProperties.PROP_BLOCK_GLOWING.getValue(skinProps));
        checkBlockLadder = new GuiCheckBox(SYNC_ID_BLOCK, 10, 35, GuiHelper.getLocalizedControlName(guiName, "ladder"), SkinProperties.PROP_BLOCK_LADDER.getValue(skinProps));
        checkBlockNoCollision = new GuiCheckBox(SYNC_ID_BLOCK, 10, 50, GuiHelper.getLocalizedControlName(guiName, "noCollision"), SkinProperties.PROP_BLOCK_NO_COLLISION.getValue(skinProps));
        checkBlockSeat = new GuiCheckBox(SYNC_ID_BLOCK, 10, 65, GuiHelper.getLocalizedControlName(guiName, "seat"), SkinProperties.PROP_BLOCK_SEAT.getValue(skinProps));
        checkBlockMultiblock = new GuiCheckBox(SYNC_ID_BLOCK, 10, 80, GuiHelper.getLocalizedControlName(guiName, "multiblock"), SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps));
        checkBlockBed = new GuiCheckBox(SYNC_ID_BLOCK, 22, 95, GuiHelper.getLocalizedControlName(guiName, "bed"), SkinProperties.PROP_BLOCK_BED.getValue(skinProps));
        checkBlockEnderInventory = new GuiCheckBox(SYNC_ID_BLOCK, 10, 110, GuiHelper.getLocalizedControlName(guiName, "enderInventory"), SkinProperties.PROP_BLOCK_ENDER_INVENTORY.getValue(skinProps));
        checkBlockInventory = new GuiCheckBox(SYNC_ID_BLOCK, 10, 125, GuiHelper.getLocalizedControlName(guiName, "inventory"), SkinProperties.PROP_BLOCK_INVENTORY.getValue(skinProps));
        if (!checkBlockMultiblock.isChecked()) {
            checkBlockBed.enabled = false;
            checkBlockBed.setIsChecked(false);
        } else {
            checkBlockBed.enabled = true;
        }
        // TODO remove to re-enable beds
        checkBlockBed.enabled = false;

        checkBlockEnderInventory.enabled = !checkBlockInventory.isChecked();
        checkBlockInventory.enabled = !checkBlockEnderInventory.isChecked();
        if (checkBlockInventory.isChecked()) {
            checkBlockEnderInventory.setIsChecked(false);
        }
        if (checkBlockEnderInventory.isChecked()) {
            checkBlockInventory.setIsChecked(false);
        }

        inventorySize = new GuiInventorySize(SYNC_ID_BLOCK, 10, 158, 9, 6);
        inventorySize.setSrc(TEXTURE, 176, 0);
        inventorySize.setSelection(SkinProperties.PROP_BLOCK_INVENTORY_WIDTH.getValue(skinProps), SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT.getValue(skinProps));

        sliderWingIdleSpeed = new GuiCustomSlider(SYNC_ID_WINGS, 10, 45, 154, 10, "", "ms", 200D, 10000D, SkinProperties.PROP_WINGS_IDLE_SPEED.getValue(skinProps), false, true, this);
        sliderWingFlyingSpeed = new GuiCustomSlider(SYNC_ID_WINGS, 10, 65, 154, 10, "", "ms", 200D, 10000D, SkinProperties.PROP_WINGS_FLYING_SPEED.getValue(skinProps), false, true, this);
        sliderWingMinAngle = new GuiCustomSlider(SYNC_ID_WINGS, 10, 85, 154, 10, "", DEGREE, -180D, 180D, SkinProperties.PROP_WINGS_MIN_ANGLE.getValue(skinProps), false, true, this);
        sliderWingMaxAngle = new GuiCustomSlider(SYNC_ID_WINGS, 10, 105, 154, 10, "", DEGREE, -180D, 180D, SkinProperties.PROP_WINGS_MAX_ANGLE.getValue(skinProps), false, true, this);

        sliderWingIdleSpeed.setFineTuneButtons(true);
        sliderWingFlyingSpeed.setFineTuneButtons(true);
        sliderWingMinAngle.setFineTuneButtons(true);
        sliderWingMaxAngle.setFineTuneButtons(true);

        checkModelOverrideHead = new GuiCheckBox(SYNC_ID_MODEL, 10, 20, GuiHelper.getLocalizedControlName(guiName, "modelOverrideHead"), SkinProperties.PROP_MODEL_OVERRIDE_HEAD.getValue(skinProps));
        checkModelOverrideChest = new GuiCheckBox(SYNC_ID_MODEL, 10, 20, GuiHelper.getLocalizedControlName(guiName, "modelOverrideChest"), SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skinProps));
        checkModelOverrideArmLeft = new GuiCheckBox(SYNC_ID_MODEL, 10, 35, GuiHelper.getLocalizedControlName(guiName, "modelOverrideArmLeft"), SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.getValue(skinProps));
        checkModelOverrideArmRight = new GuiCheckBox(SYNC_ID_MODEL, 10, 50, GuiHelper.getLocalizedControlName(guiName, "modelOverrideArmRight"), SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.getValue(skinProps));
        checkModelOverrideLegLeft = new GuiCheckBox(SYNC_ID_MODEL, 10, 20, GuiHelper.getLocalizedControlName(guiName, "modelOverrideLegLeft"), SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.getValue(skinProps));
        checkModelOverrideLegRight = new GuiCheckBox(SYNC_ID_MODEL, 10, 35, GuiHelper.getLocalizedControlName(guiName, "modelOverrideLegRight"), SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.getValue(skinProps));

        checkHideOverlayHead = new GuiCheckBox(SYNC_ID_MODEL, 10, 35, GuiHelper.getLocalizedControlName(guiName, "hideOverlayHead"), SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD.getValue(skinProps));
        checkHideOverlayChest = new GuiCheckBox(SYNC_ID_MODEL, 10, 65, GuiHelper.getLocalizedControlName(guiName, "hideOverlayChest"), SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST.getValue(skinProps));
        checkHideOverlayArmLeft = new GuiCheckBox(SYNC_ID_MODEL, 10, 80, GuiHelper.getLocalizedControlName(guiName, "hideOverlayArmLeft"), SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT.getValue(skinProps));
        checkHideOverlayArmRight = new GuiCheckBox(SYNC_ID_MODEL, 10, 95, GuiHelper.getLocalizedControlName(guiName, "hideOverlayArmRight"), SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT.getValue(skinProps));
        checkHideOverlayLegLeft = new GuiCheckBox(SYNC_ID_MODEL, 10, 50, GuiHelper.getLocalizedControlName(guiName, "hideOverlayLegLeft"), SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.getValue(skinProps));
        checkHideOverlayLegRight = new GuiCheckBox(SYNC_ID_MODEL, 10, 65, GuiHelper.getLocalizedControlName(guiName, "hideOverlayLegRight"), SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT.getValue(skinProps));

        checkLimitLimbMovement = new GuiCheckBox(SYNC_ID_MODEL, 10, 80, GuiHelper.getLocalizedControlName(guiName, "limitLimbs"), SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.getValue(skinProps));

        MovementType skinMovmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skinProps));
        dropDownMovementType = new GuiDropDownList(SYNC_ID_WINGS, 10, 125, 50, "", this);
        for (int i = 0; i < MovementType.values().length; i++) {
            MovementType movementType = MovementType.values()[i];
            String unlocalizedName = "movmentType." + LibModInfo.ID.toLowerCase() + ":" + movementType.name().toLowerCase();
            String localizedName = I18n.format(unlocalizedName);
            dropDownMovementType.addListItem(localizedName, movementType.name(), true);
            if (movementType == skinMovmentType) {
                dropDownMovementType.setListSelectedIndex(i);
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

        buttonList.add(checkHideOverlayHead);
        buttonList.add(checkHideOverlayChest);
        buttonList.add(checkHideOverlayArmLeft);
        buttonList.add(checkHideOverlayArmRight);
        buttonList.add(checkHideOverlayLegLeft);
        buttonList.add(checkHideOverlayLegRight);

        buttonList.add(checkLimitLimbMovement);
        buttonList.add(dropDownMovementType);
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

        // TODO remove to re-enable beds
        checkBlockBed.enabled = false;

        if (button.id == SYNC_ID_BLOCK) {
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

        if (button.id == SYNC_ID_MODEL) {
            SkinProperties.PROP_MODEL_OVERRIDE_HEAD.setValue(skinProps, checkModelOverrideHead.isChecked());
            SkinProperties.PROP_MODEL_OVERRIDE_CHEST.setValue(skinProps, checkModelOverrideChest.isChecked());
            SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.setValue(skinProps, checkModelOverrideArmLeft.isChecked());
            SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.setValue(skinProps, checkModelOverrideArmRight.isChecked());
            SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.setValue(skinProps, checkModelOverrideLegLeft.isChecked());
            SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.setValue(skinProps, checkModelOverrideLegRight.isChecked());

            SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD.setValue(skinProps, checkHideOverlayHead.isChecked());
            SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST.setValue(skinProps, checkHideOverlayChest.isChecked());
            SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT.setValue(skinProps, checkHideOverlayArmLeft.isChecked());
            SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT.setValue(skinProps, checkHideOverlayArmRight.isChecked());
            SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.setValue(skinProps, checkHideOverlayLegLeft.isChecked());
            SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT.setValue(skinProps, checkHideOverlayLegRight.isChecked());

            SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.setValue(skinProps, checkLimitLimbMovement.isChecked());
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }

        inventorySize.visible = checkBlockInventory.isChecked();
    }

    public void resetValues(SkinProperties skinProperties) {
        resetting = true;
        //ModLogger.log("Reset: " + skinProperties);
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

        checkModelOverrideHead.setIsChecked(SkinProperties.PROP_MODEL_OVERRIDE_HEAD.getValue(skinProperties));
        checkModelOverrideChest.setIsChecked(SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skinProperties));
        checkModelOverrideArmLeft.setIsChecked(SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT.getValue(skinProperties));
        checkModelOverrideArmRight.setIsChecked(SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT.getValue(skinProperties));
        checkModelOverrideLegLeft.setIsChecked(SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT.getValue(skinProperties));
        checkModelOverrideLegRight.setIsChecked(SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT.getValue(skinProperties));

        checkHideOverlayHead.setIsChecked(SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD.getValue(skinProperties));
        checkHideOverlayChest.setIsChecked(SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST.getValue(skinProperties));
        checkHideOverlayArmLeft.setIsChecked(SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT.getValue(skinProperties));
        checkHideOverlayArmRight.setIsChecked(SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT.getValue(skinProperties));
        checkHideOverlayLegLeft.setIsChecked(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT.getValue(skinProperties));
        checkHideOverlayLegRight.setIsChecked(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT.getValue(skinProperties));

        checkLimitLimbMovement.setIsChecked(SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS.getValue(skinProperties));

        MovementType skinMovmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skinProperties));
        for (int i = 0; i < MovementType.values().length; i++) {
            MovementType movementType = MovementType.values()[i];
            if (movementType == skinMovmentType) {
                dropDownMovementType.setListSelectedIndex(i);
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
        dropDownMovementType.visible = tileEntity.getSkinType() == SkinTypeRegistry.skinWings;

        checkModelOverrideHead.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_OVERRIDE_HEAD);
        checkModelOverrideChest.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_OVERRIDE_CHEST);
        checkModelOverrideArmLeft.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_OVERRIDE_ARM_LEFT);
        checkModelOverrideArmRight.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_OVERRIDE_ARM_RIGHT);
        checkModelOverrideLegLeft.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_OVERRIDE_LEG_LEFT);
        checkModelOverrideLegRight.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_OVERRIDE_LEG_RIGHT);

        checkHideOverlayHead.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD);
        checkHideOverlayChest.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_HIDE_OVERLAY_CHEST);
        checkHideOverlayArmLeft.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_LEFT);
        checkHideOverlayArmRight.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_HIDE_OVERLAY_ARM_RIGHT);
        checkHideOverlayLegLeft.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_LEFT);
        checkHideOverlayLegRight.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_HIDE_OVERLAY_LEG_RIGHT);

        checkLimitLimbMovement.visible = tileEntity.getSkinType().getProperties().contains(SkinProperties.PROP_MODEL_LEGS_LIMIT_LIMBS);
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

        dropDownMovementType.drawForeground(mc, mouseX - x, mouseY - y, partialTickTime);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!resetting) {
            SkinProperties skinProps = tileEntity.getSkinProps();

            if (!sliderWingIdleSpeed.dragging & !sliderWingFlyingSpeed.dragging & !sliderWingMinAngle.dragging & !sliderWingMaxAngle.dragging) {
                //ModLogger.log("Sending slider update packet. " + skinProps);
                //ModLogger.log("Old: " + skinProps);
                SkinProperties.PROP_WINGS_IDLE_SPEED.setValue(skinProps, (double) Math.round(sliderWingIdleSpeed.getValue()));
                SkinProperties.PROP_WINGS_FLYING_SPEED.setValue(skinProps, (double) Math.round(sliderWingFlyingSpeed.getValue()));
                SkinProperties.PROP_WINGS_MIN_ANGLE.setValue(skinProps, (double) Math.round(sliderWingMinAngle.getValue()));
                SkinProperties.PROP_WINGS_MAX_ANGLE.setValue(skinProps, (double) Math.round(sliderWingMaxAngle.getValue()));
                //ModLogger.log("New: " + skinProps);
            }


            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        SkinProperties skinProps = tileEntity.getSkinProps();
        SkinProperties.PROP_WINGS_MOVMENT_TYPE.setValue(skinProps, dropDownList.getListSelectedItem().tag);
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
    }
}
