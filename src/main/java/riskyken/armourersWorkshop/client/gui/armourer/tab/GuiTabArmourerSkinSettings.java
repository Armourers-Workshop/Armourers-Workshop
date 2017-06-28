package riskyken.armourersWorkshop.client.gui.armourer.tab;

import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.armourer.GuiArmourer;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiCustomSlider;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerSkinProps;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerSkinSettings extends GuiTabPanel implements ISlider {
    
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
    
    private GuiCustomSlider sliderWingIdleSpeed;
    private GuiCustomSlider sliderWingFlyingSpeed;
    private GuiCustomSlider sliderWingMinAngle;
    private GuiCustomSlider sliderWingMaxAngle;
    
    private GuiCheckBox checkArmourOverrideBodyPart;
    
    private boolean resetting;
    
    public GuiTabArmourerSkinSettings(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer)parent).tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getInventoryName();
        
        buttonList.clear();
        
        SkinProperties skinProps = tileEntity.getSkinProps();
        checkBlockGlowing = new GuiCheckBox(15, 10, 20, GuiHelper.getLocalizedControlName(guiName, "glowing"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_GLOWING, false));
        checkBlockLadder = new GuiCheckBox(15, 10, 35, GuiHelper.getLocalizedControlName(guiName, "ladder"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_LADDER, false));
        checkBlockNoCollision = new GuiCheckBox(15, 10, 50, GuiHelper.getLocalizedControlName(guiName, "noCollision"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_NO_COLLISION, false));
        checkBlockSeat = new GuiCheckBox(15, 10, 65, GuiHelper.getLocalizedControlName(guiName, "seat"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_SEAT, false));
        checkBlockMultiblock = new GuiCheckBox(15, 10, 80, GuiHelper.getLocalizedControlName(guiName, "multiblock"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false));
        checkBlockBed = new GuiCheckBox(15, 10, 95, GuiHelper.getLocalizedControlName(guiName, "bed"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_BED, false));
        checkBlockInventory = new GuiCheckBox(15, 10, 110, GuiHelper.getLocalizedControlName(guiName, "inventory"), skinProps.getPropertyBoolean(Skin.KEY_BLOCK_INVENTORY, false));
        if (!checkBlockMultiblock.isChecked()) {
            checkBlockBed.enabled = false;
            checkBlockBed.setIsChecked(false);
        } else {
            checkBlockBed.enabled = true;
        }
        //TODO remove to re-enable beds
        checkBlockBed.enabled = false;
        
        sliderWingIdleSpeed = new GuiCustomSlider(15, 10, 45, 154, 10, "", "ms", 200D, 10000D, skinProps.getPropertyDouble(Skin.KEY_WINGS_IDLE_SPEED, 6000D), false, true, this);
        sliderWingFlyingSpeed = new GuiCustomSlider(15, 10, 65, 154, 10, "", "ms", 200D, 10000D, skinProps.getPropertyDouble(Skin.KEY_WINGS_FLYING_SPEED, 350D), false, true, this);
        sliderWingMinAngle = new GuiCustomSlider(15, 10, 85, 154, 10, "", DEGREE, -90D, 90D, skinProps.getPropertyDouble(Skin.KEY_WINGS_MIN_ANGLE, 0D), false, true, this);
        sliderWingMaxAngle = new GuiCustomSlider(15, 10, 105, 154, 10, "", DEGREE, -90D, 90D, skinProps.getPropertyDouble(Skin.KEY_WINGS_MAX_ANGLE, 75D), false, true, this);
        
        checkArmourOverrideBodyPart = new GuiCheckBox(15, 10, 20, GuiHelper.getLocalizedControlName(guiName, "overrideBodyPart"), skinProps.getPropertyBoolean(Skin.KEY_ARMOUR_OVERRIDE, false));
        
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
    }
    
    public void resetValues(SkinProperties skinProperties) {
        resetting = true;
        checkBlockGlowing.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_GLOWING, false));
        checkBlockLadder.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_LADDER, false));
        checkBlockNoCollision.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_NO_COLLISION, false));
        checkBlockSeat.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_SEAT, false));
        checkBlockMultiblock.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false));
        checkBlockBed.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_BED, false));
        checkBlockInventory.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_BLOCK_INVENTORY, false));
        
        sliderWingMinAngle.setValue(skinProperties.getPropertyDouble(Skin.KEY_WINGS_MIN_ANGLE, 0D));
        sliderWingMinAngle.updateSlider();
        sliderWingMaxAngle.setValue(skinProperties.getPropertyDouble(Skin.KEY_WINGS_MAX_ANGLE, 75D));
        sliderWingMaxAngle.updateSlider();
        sliderWingIdleSpeed.setValue(skinProperties.getPropertyDouble(Skin.KEY_WINGS_IDLE_SPEED, 6000D));
        sliderWingIdleSpeed.updateSlider();
        sliderWingFlyingSpeed.setValue(skinProperties.getPropertyDouble(Skin.KEY_WINGS_FLYING_SPEED, 350D));
        sliderWingFlyingSpeed.updateSlider();
        
        checkArmourOverrideBodyPart.setIsChecked(skinProperties.getPropertyBoolean(Skin.KEY_ARMOUR_OVERRIDE, false));
        resetting = false;
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
        
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
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        
        if (tileEntity.getSkinType() == SkinTypeRegistry.skinWings) {
            String idleSpeedLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.idleSpeed");
            String flyingSpeedLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.flyingSpeed");
            String minAngleLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.minAngle");
            String maxAngleLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.maxAngle");
            
            this.fontRenderer.drawString(idleSpeedLabel, 10, 36, 4210752);
            this.fontRenderer.drawString(flyingSpeedLabel, 10, 56, 4210752);
            this.fontRenderer.drawString(minAngleLabel, 10, 76, 4210752);
            this.fontRenderer.drawString(maxAngleLabel, 10, 96, 4210752);
        }
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!resetting) {
            SkinProperties skinProps = tileEntity.getSkinProps();
            skinProps.setProperty(Skin.KEY_WINGS_IDLE_SPEED, (double)Math.round(sliderWingIdleSpeed.getValue()));
            skinProps.setProperty(Skin.KEY_WINGS_FLYING_SPEED, (double)Math.round(sliderWingFlyingSpeed.getValue()));
            skinProps.setProperty(Skin.KEY_WINGS_MIN_ANGLE, (double)Math.round(sliderWingMinAngle.getValue()));
            skinProps.setProperty(Skin.KEY_WINGS_MAX_ANGLE, (double)Math.round(sliderWingMaxAngle.getValue()));
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
        }
    }
}
