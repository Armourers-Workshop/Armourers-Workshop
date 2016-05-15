package riskyken.armourersWorkshop.client.gui;

import java.util.Random;

import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiCustomSlider;
import riskyken.armourersWorkshop.client.gui.controls.GuiTab;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabController;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiBipedRotations;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

@SideOnly(Side.CLIENT)
public class GuiMannequin extends GuiContainer implements ISlider  {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/mannequinNew.png");
    private static ModelMannequin model = new ModelMannequin();
    
    private final TileEntityMannequin tileEntity;
    private final EntityPlayer player;
    private final String inventoryName;
    
    private boolean guiLoaded = false;
    
    private GuiTabController tabController;
    private int activeTab = -1;
    
    //Rotations
    private BipedRotations bipedRotations;
    private BipedRotations lastBipedRotations;
    private GuiCustomSlider bipedXslider;
    private GuiCustomSlider bipedYslider;
    private GuiCustomSlider bipedZslider;
    
    private GuiCheckBox isChildCheck;
    
    public GuiMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        super(new ContainerMannequin(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.player = invPlayer.player;
        
        this.bipedRotations = new BipedRotations();
        this.lastBipedRotations = new BipedRotations();
        NBTTagCompound compound = new NBTTagCompound();
        tileEntity.getBipedRotations().saveNBTData(compound);
        this.bipedRotations.loadNBTData(compound);
        this.lastBipedRotations.loadNBTData(compound);
        this.inventoryName = tileEntity.getInventoryName();
    }
    
    @Override
    public void initGui() {
        this.xSize = this.width;
        this.ySize = this.height;
        super.initGui();
        buttonList.clear();
        guiLoaded = false;
        
        tabController = new GuiTabController(this);
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.inventory")).setIconLocation(0, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.rotations")).setIconLocation(16, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.offset")).setIconLocation(32, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.skinAndHair")).setIconLocation(48, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.name")).setIconLocation(64, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.extraRenders")).setIconLocation(80, 52));
        tabController.setActiveTabIndex(0);
        
        bipedXslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 120, 100, 10, "X: ", "", -90D, 90D, 0D, true, true, this);
        bipedYslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 130, 100, 10, "Y: ", "", -90D, 90D, 0D, true, true, this);
        bipedZslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 140, 100, 10, "Z: ", "", -20D, 20D, 0D, true, true, this);
        
        isChildCheck = new GuiCheckBox(3, this.guiLeft + 149, this.guiTop + 110, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.isChild"), false);
        
        int slotSize = 18;
        //Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(7 + x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(
                        (int) ((this.width / 2F) - (176F / 2F) + x * slotSize + 8),
                        this.height + 1 - 1 - slotSize);
            } else {
                slot.yDisplayPosition = this.height + 1 - 1 - slotSize;
                slot.xDisplayPosition = (int) ((this.width / 2F) - (176F / 2F) + x * slotSize + 8);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(7 + x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setDisplayPosition(
                            (int) ((this.width / 2F) - (176F / 2F) + x * slotSize + 8),
                            this.height + 1 - 72 - 5 + y * slotSize);
                } else {
                    slot.yDisplayPosition = this.height + 1 - 72 - 5 + y * slotSize;
                    slot.xDisplayPosition = (int) ((this.width / 2F) - (176F / 2F) + x * slotSize + 8);
                }
            }
        }
        
        //Move mannequin inventory slots.
        for (int i = 0; i < 7; i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(50 + i * 18, 10);
            } else {
                slot.xDisplayPosition = 50 + i * 18;
                slot.yDisplayPosition = 10;
            }
        }
        
        if (bipedRotations != null) {
            isChildCheck.setIsChecked(bipedRotations.isChild);
            setSliderValue(bipedXslider, Math.toDegrees(-bipedRotations.head.rotationX));
            setSliderValue(bipedYslider, Math.toDegrees(-bipedRotations.head.rotationY));
            setSliderValue(bipedZslider, Math.toDegrees(-bipedRotations.head.rotationZ));
        }
        
        buttonList.add(tabController);
        
        buttonList.add(bipedXslider);
        buttonList.add(bipedYslider);
        buttonList.add(bipedZslider);
        
        //buttonList.add(isChildCheck);
        
        //buttonList.add(new GuiButtonExt(1, this.guiLeft + 67, this.guiTop + 152, 50, 18, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "reset")));
        //buttonList.add(new GuiButtonExt(2, this.guiLeft + 119, this.guiTop + 152, 50, 18, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "random")));
        
        tabChanged();
        guiLoaded = true;
    }
    
    private void setSliderValue(GuiCustomSlider slider, double value) {
        slider.setValue(value);
        slider.precision = 2;
        slider.updateSlider();
    }
    
    private void tabChanged() {
        this.activeTab = tabController.getActiveTabIndex();
        
        for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(activeTab == 0);
            }
        }
        
        bipedXslider.visible = activeTab == 1;
        bipedYslider.visible = activeTab == 1;
        bipedZslider.visible = activeTab == 1;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == tabController) {
            tabChanged();
        }
        if (button.id == 1) {
            guiLoaded = false;
            bipedRotations.resetRotations();
            bipedXslider.setValue(0D);
            bipedYslider.setValue(0D);
            bipedZslider.setValue(0D);
            bipedXslider.updateSlider();
            bipedYslider.updateSlider();
            bipedZslider.updateSlider();
            
            guiLoaded = true;
            checkAndSendRotationValues();
        }
        if (button.id == 2) {
            guiLoaded = false;
            //TODO Fixed random
            Random rnd = new Random();
            /*
            setSliderValue(leftArmXslider, rnd.nextFloat() * 270 - 90);
            setSliderValue(leftArmYslider, rnd.nextFloat() * 135 - 45);
            setSliderValue(leftArmZslider, rnd.nextFloat() * 90 - 45);
            
            setSliderValue(rightArmXslider, rnd.nextFloat() * 270 - 90);
            setSliderValue(rightArmYslider, rnd.nextFloat() * 135 - 45);
            setSliderValue(rightArmZslider, rnd.nextFloat() * 90 - 45);
            
            setSliderValue(leftLegXslider, rnd.nextFloat() * 180 - 90);
            setSliderValue(leftLegYslider, rnd.nextFloat() * 90 - 45);
            setSliderValue(leftLegZslider, rnd.nextFloat() * 90 - 45);
            
            setSliderValue(rightLegXslider, rnd.nextFloat() * 180 - 90);
            setSliderValue(rightLegYslider, rnd.nextFloat() * 90 - 45);
            setSliderValue(rightLegZslider, rnd.nextFloat() * 90 - 45);
            
            setSliderValue(headXslider, rnd.nextFloat() * 180 - 90);
            setSliderValue(headYslider, rnd.nextFloat() * 180 - 90);
            setSliderValue(headZslider, rnd.nextFloat() * 40 - 20);
            */
            guiLoaded = true;
            checkAndSendRotationValues();
        }
        if (button.id == 3) {
            bipedRotations.isChild = isChildCheck.isChecked();
            checkAndSendRotationValues();
        }
    }
    
    @Override
    public void drawDefaultBackground() {
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        String append = null;
        if (tileEntity.getGameProfile() != null) {
            append = tileEntity.getGameProfile().getName();
        } 
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName(), append, 0xFFFFFF);
        //this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        /*
        String headRotationLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.headRotation");
        String leftArmRotationLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.leftArmRotation");
        String rightArmRotationLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.rightArmRotation");
        String leftLegRotationLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.leftLegRotation");
        String rightLegRotationLabel = GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.rightLegRotation");
        
        this.fontRendererObj.drawString(leftArmRotationLabel, 40, 20, 4210752);
        this.fontRendererObj.drawString(rightArmRotationLabel, 147, 20, 4210752);
        this.fontRendererObj.drawString(leftLegRotationLabel, 40, 65, 4210752);
        this.fontRendererObj.drawString(rightLegRotationLabel, 147, 65, 4210752);
        this.fontRendererObj.drawString(headRotationLabel, 40, 110, 4210752);
        */
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        mc.renderEngine.bindTexture(texture);
        int invSizeX = 176;
        int invSizeY = 98;
        
        if (activeTab == 0) {
            drawTexturedModalRect(this.width / 2 - invSizeX / 2, height - invSizeY + 6, 0, 0, invSizeX, invSizeY);
        }
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!guiLoaded) {
            return;
        }
        checkAndSendRotationValues();
    }
    
    private void checkAndSendRotationValues() {
        
        bipedRotations.head.setRotationsDegrees(
                (float)-bipedXslider.getValue(),
                (float)-bipedYslider.getValue(),
                (float)-bipedZslider.getValue());
        //tileEntity.setBipedRotations(bipedRotations);
        if (!this.bipedRotations.equals(this.lastBipedRotations)) {
            NBTTagCompound compound = new NBTTagCompound();
            this.bipedRotations.saveNBTData(compound);
            this.lastBipedRotations.loadNBTData(compound);
            MessageClientGuiBipedRotations message = new MessageClientGuiBipedRotations(bipedRotations);
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }
}
