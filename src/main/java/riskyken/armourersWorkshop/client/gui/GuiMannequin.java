package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiCustomSlider;
import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.client.render.EquipmentModelRender;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.BipedRotations;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiBipedRotations;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;

public class GuiMannequin extends GuiContainer implements ISlider  {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/mannequin.png");
    private static ModelMannequin model = new ModelMannequin();
    
    private TileEntityMannequin tileEntity;
    private EntityPlayer player;
    
    private BipedRotations bipedRotations;
    private BipedRotations lastBipedRotations;
    
    private boolean guiLoaded = false;
    
    private GuiCustomSlider headXslider;
    private GuiCustomSlider headYslider;
    private GuiCustomSlider headZslider;
    
    private GuiCustomSlider leftArmXslider;
    private GuiCustomSlider leftArmYslider;
    private GuiCustomSlider leftArmZslider;
    
    private GuiCustomSlider rightArmXslider;
    private GuiCustomSlider rightArmYslider;
    private GuiCustomSlider rightArmZslider;
    
    private GuiCustomSlider leftLegXslider;
    private GuiCustomSlider leftLegYslider;
    private GuiCustomSlider leftLegZslider;
    
    private GuiCustomSlider rightLegXslider;
    private GuiCustomSlider rightLegYslider;
    private GuiCustomSlider rightLegZslider;
    
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
        
        this.xSize = 256;
        this.ySize = 256;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        guiLoaded = false;
        
        leftArmXslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 30, 100, 10, "X: ", "", -90D, 180D, 0D, true, true, this);
        leftArmYslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 40, 100, 10, "Y: ", "", -45D, 90D, 0D, true, true, this);
        leftArmZslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 50, 100, 10, "Z: ", "", -45D, 45D, 0D, true, true, this);
        
        rightArmXslider = new GuiCustomSlider(0, this.guiLeft + 147, this.guiTop + 30, 100, 10, "X: ", "", -90D, 180D, 0D, true, true, this);
        rightArmYslider = new GuiCustomSlider(0, this.guiLeft + 147, this.guiTop + 40, 100, 10, "Y: ", "", -45D, 90D, 0D, true, true, this);
        rightArmZslider = new GuiCustomSlider(0, this.guiLeft + 147, this.guiTop + 50, 100, 10, "Z: ", "", -45D, 45D, 0D, true, true, this);
        
        leftLegXslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 75, 100, 10, "X: ", "", -90D, 90D, 0D, true, true, this);
        leftLegYslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 85, 100, 10, "Y: ", "", -45D, 45D, 0D, true, true, this);
        leftLegZslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 95, 100, 10, "Z: ", "", -45D, 45D, 0D, true, true, this);
        
        rightLegXslider = new GuiCustomSlider(0, this.guiLeft + 147, this.guiTop + 75, 100, 10, "X: ", "", -90D, 90D, 0D, true, true, this);
        rightLegYslider = new GuiCustomSlider(0, this.guiLeft + 147, this.guiTop + 85, 100, 10, "Y: ", "", -45D, 45D, 0D, true, true, this);
        rightLegZslider = new GuiCustomSlider(0, this.guiLeft + 147, this.guiTop + 95, 100, 10, "Z: ", "", -45D, 45D, 0D, true, true, this);
        
        headXslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 120, 100, 10, "X: ", "", -90D, 90D, 0D, true, true, this);
        headYslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 130, 100, 10, "Y: ", "", -90D, 90D, 0D, true, true, this);
        headZslider = new GuiCustomSlider(0, this.guiLeft + 40, this.guiTop + 140, 100, 10, "Z: ", "", -20D, 20D, 0D, true, true, this);
        
        isChildCheck = new GuiCheckBox(2, this.guiLeft + 149, this.guiTop + 110, 14, 14, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "label.isChild"), false, false);
        
        if (bipedRotations != null) {
            isChildCheck.setChecked(bipedRotations.isChild);
            
            setSliderValue(headXslider, Math.toDegrees(-bipedRotations.head.rotationX));
            setSliderValue(headYslider, Math.toDegrees(-bipedRotations.head.rotationY));
            setSliderValue(headZslider, Math.toDegrees(-bipedRotations.head.rotationZ));
            
            setSliderValue(leftArmXslider, Math.toDegrees(-bipedRotations.leftArm.rotationX));
            setSliderValue(leftArmYslider, Math.toDegrees(-bipedRotations.leftArm.rotationY));
            setSliderValue(leftArmZslider, Math.toDegrees(-bipedRotations.leftArm.rotationZ));
            
            setSliderValue(rightArmXslider, Math.toDegrees(-bipedRotations.rightArm.rotationX));
            setSliderValue(rightArmYslider, Math.toDegrees(bipedRotations.rightArm.rotationY));
            setSliderValue(rightArmZslider, Math.toDegrees(bipedRotations.rightArm.rotationZ));
            
            setSliderValue(leftLegXslider, Math.toDegrees(-bipedRotations.leftLeg.rotationX));
            setSliderValue(leftLegYslider, Math.toDegrees(-bipedRotations.leftLeg.rotationY));
            setSliderValue(leftLegZslider, Math.toDegrees(-bipedRotations.leftLeg.rotationZ));
            
            setSliderValue(rightLegXslider, Math.toDegrees(-bipedRotations.rightLeg.rotationX));
            setSliderValue(rightLegYslider, Math.toDegrees(bipedRotations.rightLeg.rotationY));
            setSliderValue(rightLegZslider, Math.toDegrees(bipedRotations.rightLeg.rotationZ));
        }
        
        buttonList.add(headXslider);
        buttonList.add(headYslider);
        buttonList.add(headZslider);
        
        buttonList.add(leftArmXslider);
        buttonList.add(leftArmYslider);
        buttonList.add(leftArmZslider);
        
        buttonList.add(rightArmXslider);
        buttonList.add(rightArmYslider);
        buttonList.add(rightArmZslider);
        
        buttonList.add(leftLegXslider);
        buttonList.add(leftLegYslider);
        buttonList.add(leftLegZslider);
        
        buttonList.add(rightLegXslider);
        buttonList.add(rightLegYslider);
        buttonList.add(rightLegZslider);
        
        buttonList.add(isChildCheck);
        buttonList.add(new GuiButtonExt(1, this.guiLeft + 147, this.guiTop + 130, 20, 20, "R"));
        
        guiLoaded = true;
    }
    
    private void setSliderValue(GuiCustomSlider slider, double value) {
        slider.setValue(value);
        slider.precision = 2;
        slider.updateSlider();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            guiLoaded = false;
            setSliderValue(headXslider, 0F);
            setSliderValue(headYslider, 0F);
            setSliderValue(headZslider, 0F);
            
            setSliderValue(leftArmXslider, 0F);
            setSliderValue(leftArmYslider, 0F);
            setSliderValue(leftArmZslider, 0F);
            
            setSliderValue(rightArmXslider, 0F);
            setSliderValue(rightArmYslider, 0F);
            setSliderValue(rightArmZslider, 0F);
            
            setSliderValue(leftLegXslider, 0F);
            setSliderValue(leftLegYslider, 0F);
            setSliderValue(leftLegZslider, 0F);
            
            setSliderValue(rightLegXslider, 0F);
            setSliderValue(rightLegYslider, 0F);
            setSliderValue(rightLegZslider, 0F);
            guiLoaded = true;

            checkAndSendRotationValues();
        }
        if (button.id == 2) {
            isChildCheck.setChecked(!isChildCheck.isChecked());
            bipedRotations.isChild = isChildCheck.isChecked();
            checkAndSendRotationValues();
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName());
        
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
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        float scale = 40F;
        GL11.glPushMatrix();
        RenderHelper.enableStandardItemLighting();
        
        ResourceLocation skin = AbstractClientPlayer.locationStevePng;
        PlayerSkinInfo skinInfo = null;
        
        if (tileEntity.getGameProfile() != null) {
            skinInfo = EquipmentModelRender.INSTANCE.getPlayersNakedData(tileEntity.getGameProfile().getId());
            skin = SkinHelper.getSkinResourceLocation(tileEntity.getGameProfile());
        }
        
        if (skinInfo != null && skinInfo.getNakedInfo().isNaked) {
            if (!skinInfo.bindNomalSkin()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
            }
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        }
        
        GL11.glTranslatef(this.guiLeft + 212, this.guiTop + 170, 100);
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glRotatef(10, 1, 0, 0);
        GL11.glRotatef(-20, 0, 1, 0);
        
        GL11.glScalef(-scale, scale, scale);
        model.render(bipedRotations, true, 0.0625F);
        GL11.glPopMatrix();
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!guiLoaded) {
            return;
        }
        checkAndSendRotationValues();
    }
    
    private void checkAndSendRotationValues() {
        bipedRotations.head.rotationX = (float) Math.toRadians(-headXslider.getValue());
        bipedRotations.head.rotationY = (float) Math.toRadians(-headYslider.getValue());
        bipedRotations.head.rotationZ = (float) Math.toRadians(-headZslider.getValue());
        
        bipedRotations.leftArm.rotationX = (float) Math.toRadians(-leftArmXslider.getValue());
        bipedRotations.leftArm.rotationY = (float) Math.toRadians(-leftArmYslider.getValue());
        bipedRotations.leftArm.rotationZ = (float) Math.toRadians(-leftArmZslider.getValue());
        
        bipedRotations.rightArm.rotationX = (float) Math.toRadians(-rightArmXslider.getValue());
        bipedRotations.rightArm.rotationY = (float) Math.toRadians(rightArmYslider.getValue());
        bipedRotations.rightArm.rotationZ = (float) Math.toRadians(rightArmZslider.getValue());
        
        bipedRotations.leftLeg.rotationX = (float) Math.toRadians(-leftLegXslider.getValue());
        bipedRotations.leftLeg.rotationY = (float) Math.toRadians(-leftLegYslider.getValue());
        bipedRotations.leftLeg.rotationZ = (float) Math.toRadians(-leftLegZslider.getValue());
        
        bipedRotations.rightLeg.rotationX = (float) Math.toRadians(-rightLegXslider.getValue());
        bipedRotations.rightLeg.rotationY = (float) Math.toRadians(rightLegYslider.getValue());
        bipedRotations.rightLeg.rotationZ = (float) Math.toRadians(rightLegZslider.getValue());
        
        if (!this.bipedRotations.equals(this.lastBipedRotations)) {
            NBTTagCompound compound = new NBTTagCompound();
            this.bipedRotations.saveNBTData(compound);
            this.lastBipedRotations.loadNBTData(compound);
            MessageClientGuiBipedRotations message = new MessageClientGuiBipedRotations(bipedRotations);
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }
}
