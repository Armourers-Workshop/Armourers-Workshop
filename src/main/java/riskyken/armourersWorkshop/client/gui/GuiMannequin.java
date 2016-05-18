package riskyken.armourersWorkshop.client.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
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
import riskyken.armourersWorkshop.common.data.BipedRotations.BipedPart;
import riskyken.armourersWorkshop.common.data.Rectangle_I_2D;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiBipedRotations;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiMannequinData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

@SideOnly(Side.CLIENT)
public class GuiMannequin extends GuiContainer implements ISlider  {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/mannequinNew.png");
    private static ModelMannequin model = new ModelMannequin();
    
    private static final int INV_SLOT_SIZE = 18;
    
    private static final int INV_PLAYER_TEX_WIDTH = 176;
    private static final int INV_PLAYER_TEX_HEIGHT = 98;
    private static final int INV_PLAYER_TEX_U = 0;
    private static final int INV_PLAYER_TEX_V = 0;
    private static final int INV_PLAYER_TOP_PAD = 15;
    private static final int INV_PLAYER_LEFT_PAD = 8;
    
    private static final int INV_MAN_TEX_WIDTH = 176;
    private static final int INV_MAN_TEX_HEIGHT = 40;
    private static final int INV_MAN_TEX_U = 0;
    private static final int INV_MAN_TEX_V = 98;
    private static final int INV_MAN_TOP_PAD = 15;
    private static final int INV_MAN_LEFT_PAD = 26;
    
    private static final int ROT_MAN_TEX_WIDTH = 176;
    private static final int ROT_MAN_TEX_HEIGHT = 62;
    private static final int ROT_MAN_TEX_U = 0;
    private static final int ROT_MAN_TEX_V = 138;
    
    
    private final TileEntityMannequin tileEntity;
    private final EntityPlayer player;
    private final String inventoryName;
    
    private boolean guiLoaded = false;
    private GuiTabController tabController;
    private int activeTab = -1;
    private int activeBipedPart = 0;
    private Rectangle_I_2D[] bipedParts = new Rectangle_I_2D[6];
    
    //Rotations
    private GuiButtonExt resetRotsButton;
    private GuiButtonExt randomRotsButton;
    private BipedRotations bipedRotations;
    private BipedRotations lastBipedRotations;
    private GuiCustomSlider bipedRotXslider;
    private GuiCustomSlider bipedRotYslider;
    private GuiCustomSlider bipedRotZslider;
    
    //Offset
    private GuiButtonExt resetOffsetButton;
    private GuiCustomSlider bipedOffsetXslider;
    private GuiCustomSlider bipedOffsetYslider;
    private GuiCustomSlider bipedOffsetZslider;
    
    //Name
    private GuiTextField nameTextbox;
    
    //Skin & Hair Colour
    private GuiButtonExt selectSkinButton;
    private GuiButtonExt autoSkinButton;
    private GuiButtonExt selectHairButton;
    private GuiButtonExt autoHairButton;
    private int skinColour;
    private int hairColour;
    
    //Extra Renders 
    private GuiCheckBox isChildCheck;
    private GuiCheckBox isExtraRenders;
    
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
        
        resetRotsButton = new GuiButtonExt(1, width / 2 + 22, 25, 40, 14, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "reset"));
        randomRotsButton = new GuiButtonExt(2, width / 2 + 22, 40, 40, 14, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "random"));
        
        bipedRotXslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25, 100, 10, "X: ", "", -180D, 180D, 0D, true, true, this);
        bipedRotYslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25 + 10, 100, 10, "Y: ", "", -180D, 180D, 0D, true, true, this);
        bipedRotZslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25 + 20, 100, 10, "Z: ", "", -180D, 180D, 0D, true, true, this);
        
        resetOffsetButton = new GuiButtonExt(0, this.width / 2 + 27, 25, 50, 18, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "reset"));
        bipedOffsetXslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25, 100, 10, "X: ", "", -1D, 1D, 0D, true, true, this);
        bipedOffsetYslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25 + 10, 100, 10, "Y: ", "", -1D, 1D, 0D, true, true, this);
        bipedOffsetZslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25 + 20, 100, 10, "Z: ", "", -1D, 1D, 0D, true, true, this);
        bipedOffsetXslider.precision = 2;
        bipedOffsetYslider.precision = 2;
        bipedOffsetZslider.precision = 2;
        
        skinColour = tileEntity.getSkinColour();
        hairColour = tileEntity.getHairColour();
        nameTextbox = new GuiTextField(fontRendererObj, width / 2 - 78, 25, 100, 20);
        if (tileEntity.getGameProfile() != null) {
            nameTextbox.setText(tileEntity.getGameProfile().getName());
        }
        
        int recX = (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2));
        
        bipedParts[0] = new Rectangle_I_2D(recX + 153, 18, 8, 8);
        bipedParts[1] = new Rectangle_I_2D(recX + 153, 27, 8, 12);
        bipedParts[2] = new Rectangle_I_2D(recX + 148, 27, 4, 12);
        bipedParts[3] = new Rectangle_I_2D(recX + 162, 27, 4, 12);
        bipedParts[4] = new Rectangle_I_2D(recX + 152, 40, 4, 12);
        bipedParts[5] = new Rectangle_I_2D(recX + 158, 40, 4, 12);
        
        isChildCheck = new GuiCheckBox(3, this.width / 2 - 78, 25, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.isChild"), false);
        isExtraRenders = new GuiCheckBox(0, this.width / 2 - 78, 35, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.isExtraRenders"), tileEntity.isRenderExtras());
        
        int slotSize = 18;
        //Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(7 + x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(
                        (int) ((this.width / 2F) - (176F / 2F) + x * slotSize + INV_PLAYER_LEFT_PAD),
                        this.height + 1 - 1 - slotSize);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(7 + x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setDisplayPosition(
                            (int) ((this.width / 2F) - (176F / 2F) + x * slotSize + 8),
                            this.height + 1 - 72 - 5 + y * slotSize);
                }
            }
        }
        
        //Move mannequin inventory slots.
        for (int i = 0; i < 7; i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(this.width / 2 - INV_MAN_TEX_WIDTH / 2 + INV_MAN_LEFT_PAD + i * 18, 16);
            }
        }
        
        if (bipedRotations != null) {
            isChildCheck.setIsChecked(bipedRotations.isChild);
            setSliderValue(bipedRotXslider, Math.toDegrees(-bipedRotations.head.rotationX));
            setSliderValue(bipedRotYslider, Math.toDegrees(-bipedRotations.head.rotationY));
            setSliderValue(bipedRotZslider, Math.toDegrees(-bipedRotations.head.rotationZ));
        }
        
        buttonList.add(tabController);
        
        buttonList.add(resetRotsButton);
        buttonList.add(randomRotsButton);
        buttonList.add(bipedRotXslider);
        buttonList.add(bipedRotYslider);
        buttonList.add(bipedRotZslider);
        
        buttonList.add(resetOffsetButton);
        buttonList.add(bipedOffsetXslider);
        buttonList.add(bipedOffsetYslider);
        buttonList.add(bipedOffsetZslider);
        buttonList.add(isChildCheck);
        buttonList.add(isExtraRenders);
        
        tabChanged();
        bipedPartChange(0);
        guiLoaded = true;
    }
    
    private void setSliderValue(GuiCustomSlider slider, double value) {
        slider.setValue(value);
        slider.precision = 2;
        slider.updateSlider();
    }
    
    private void bipedPartChange(int partIndex) {
        activeBipedPart = partIndex;
        BipedPart part = bipedRotations.getPartForIndex(activeBipedPart);
        guiLoaded = false;
        bipedRotXslider.setValue(Math.toDegrees(-part.rotationX));
        bipedRotYslider.setValue(Math.toDegrees(-part.rotationY));
        bipedRotZslider.setValue(Math.toDegrees(-part.rotationZ));
        bipedRotXslider.updateSlider();
        bipedRotYslider.updateSlider();
        bipedRotZslider.updateSlider();
        guiLoaded = true;
    }
    
    private void tabChanged() {
        this.activeTab = tabController.getActiveTabIndex();
        
        for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(activeTab == 0);
            }
        }
        
        resetRotsButton.visible = activeTab == 1;
        randomRotsButton.visible = activeTab == 1;
        bipedRotXslider.visible = activeTab == 1;
        bipedRotYslider.visible = activeTab == 1;
        bipedRotZslider.visible = activeTab == 1;
        
        resetOffsetButton.visible = activeTab == 2;
        bipedOffsetXslider.visible = activeTab == 2;
        bipedOffsetYslider.visible = activeTab == 2;
        bipedOffsetZslider.visible = activeTab == 2;
        
        if (activeTab == 2) {
            bipedOffsetXslider.setValue(tileEntity.getOffsetX());
            bipedOffsetYslider.setValue(tileEntity.getOffsetY());
            bipedOffsetZslider.setValue(tileEntity.getOffsetZ());
            bipedOffsetXslider.updateSlider();
            bipedOffsetYslider.updateSlider();
            bipedOffsetZslider.updateSlider();
        }
        
        isChildCheck.visible = activeTab == 5;
        isExtraRenders.visible = activeTab == 5;
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        for (int i = 0; i < bipedParts.length; i++) {
            if (bipedParts[i].isInside(mouseX, mouseY)) {
                bipedPartChange(i);
                break;
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == tabController) {
            tabChanged();
        }
        if (button.id == 1) {
            guiLoaded = false;
            bipedRotations.resetRotations();
            bipedPartChange(activeBipedPart);
            
            guiLoaded = true;
            checkAndSendRotationValues();
        }
        if (button.id == 2) {
            guiLoaded = false;
            
            Random rnd = new Random();
            for (int i = 0; i < bipedParts.length; i++) {
                BipedPart part = bipedRotations.getPartForIndex(i);
                part.rotationX = (float) Math.toRadians(rnd.nextFloat() * 180F - 90F);
                part.rotationY = (float) Math.toRadians(rnd.nextFloat() * 180F - 90F);
                part.rotationZ = (float) Math.toRadians(rnd.nextFloat() * 180F - 90F);
                bipedPartChange(activeBipedPart);
            }
            
            guiLoaded = true;
            checkAndSendRotationValues();
        }
        if (button.id == 3) {
            bipedRotations.isChild = isChildCheck.isChecked();
            checkAndSendRotationValues();
        }
        
        if (button == resetOffsetButton) {
            bipedOffsetXslider.setValue(0D);
            bipedOffsetYslider.setValue(0D);
            bipedOffsetZslider.setValue(0D);
            bipedOffsetXslider.updateSlider();
            bipedOffsetYslider.updateSlider();
            bipedOffsetZslider.updateSlider();
            sendData();
        }
        
        if (button == isExtraRenders) {
            sendData();
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
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName(), append, 4210752);
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
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(texture);
        int center = (int) ((float)this.width / 2F);
        
        if (activeTab == 0) {
            drawTexturedModalRect(
                    center - INV_PLAYER_TEX_WIDTH / 2,
                    height - INV_PLAYER_TEX_HEIGHT + 6,
                    INV_PLAYER_TEX_U, INV_PLAYER_TEX_V,
                    INV_PLAYER_TEX_WIDTH, INV_PLAYER_TEX_HEIGHT);
            
            drawTexturedModalRect(
                    center - INV_MAN_TEX_WIDTH / 2, 0,
                    INV_MAN_TEX_U, INV_MAN_TEX_V,
                    INV_MAN_TEX_WIDTH, INV_MAN_TEX_HEIGHT);
        }
        if (activeTab == 1) {
            drawTexturedModalRect(
                    center - ROT_MAN_TEX_WIDTH / 2, 0,
                    ROT_MAN_TEX_U, ROT_MAN_TEX_V,
                    ROT_MAN_TEX_WIDTH, ROT_MAN_TEX_HEIGHT);
            
            for (int i = 0; i < bipedParts.length; i++) {
                int colour = 0xCCFFFF00;
                if (bipedParts[i].isInside(mouseX, mouseY)) {
                    colour = 0xCCFFFFFF;
                }
                if (i == activeBipedPart) {
                    colour = 0xCC00FF00;
                }
                drawRect(bipedParts[i].x, bipedParts[i].y, bipedParts[i].x + bipedParts[i].width, bipedParts[i].y + bipedParts[i].height, colour);
            }

            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
        if (activeTab == 2) {
            Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 62);
            rec.x = width / 2 - rec.width / 2;
            GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
        }
        if (activeTab == 3) {
            Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 62);
            rec.x = width / 2 - rec.width / 2;
            GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
        }
        if (activeTab == 4) {
            Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 62);
            rec.x = width / 2 - rec.width / 2;
            GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
        }
        if (activeTab == 5) {
            Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 62);
            rec.x = width / 2 - rec.width / 2;
            GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
        }
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!guiLoaded) {
            return;
        }
        if (slider == bipedRotXslider | slider == bipedRotYslider | slider == bipedRotZslider) {
            checkAndSendRotationValues();
        } else {
            sendData();
        }
    }
    
    private void checkAndSendRotationValues() {
        
        BipedPart activePart = bipedRotations.getPartForIndex(activeBipedPart);
        activePart.setRotationsDegrees(
                (float)-bipedRotXslider.getValue(),
                (float)-bipedRotYslider.getValue(),
                (float)-bipedRotZslider.getValue());
        
        if (!this.bipedRotations.equals(this.lastBipedRotations)) {
            NBTTagCompound compound = new NBTTagCompound();
            this.bipedRotations.saveNBTData(compound);
            this.lastBipedRotations.loadNBTData(compound);
            MessageClientGuiBipedRotations message = new MessageClientGuiBipedRotations(bipedRotations);
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }
    
    private void sendData() {
        float offsetX = (float) bipedOffsetXslider.getValue();
        float offsetY = (float) bipedOffsetYslider.getValue();
        float offsetZ = (float) bipedOffsetZslider.getValue();
        boolean renderExtras = isExtraRenders.isChecked();
        String name = nameTextbox.getText();
        MessageClientGuiMannequinData message = new MessageClientGuiMannequinData(offsetX, offsetY, offsetZ, skinColour, hairColour, name, renderExtras);
        PacketHandler.networkWrapper.sendToServer(message);
    }
}
