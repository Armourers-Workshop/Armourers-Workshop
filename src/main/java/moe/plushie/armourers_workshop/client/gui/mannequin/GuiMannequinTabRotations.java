package moe.plushie.armourers_workshop.client.gui.mannequin;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.common.data.BipedRotations;
import moe.plushie.armourers_workshop.common.data.BipedRotations.BipedPart;
import moe.plushie.armourers_workshop.common.data.Rectangle_I_2D;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiBipedRotations;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMannequinTabRotations extends GuiTabPanel implements ISlider {
    
    private static final int ROT_MAN_TEX_WIDTH = 206;
    private static final int ROT_MAN_TEX_HEIGHT = 62;
    private static final int ROT_MAN_TEX_U = 0;
    private static final int ROT_MAN_TEX_V = 138;
    
    private final String inventoryName;
    private boolean guiLoaded = false;
    
    private GuiButtonExt resetRotsButton;
    private GuiButtonExt randomRotsButton;
    
    private BipedRotations bipedRotations;
    private BipedRotations lastBipedRotations;
    
    private GuiCustomSlider bipedRotXslider;
    private GuiCustomSlider bipedRotYslider;
    private GuiCustomSlider bipedRotZslider;
    private int activeBipedPart = 0;
    private Rectangle_I_2D[] bipedParts = new Rectangle_I_2D[6];
    
    public GuiMannequinTabRotations(int tabId, GuiScreen parent, String inventoryName, BipedRotations rots) {
        super(tabId, parent, true);
        this.inventoryName = inventoryName;
        updateRotationData(rots);
    }
    
    public void updateRotationData(BipedRotations rots) {
        this.bipedRotations = new BipedRotations();
        this.lastBipedRotations = new BipedRotations();
        NBTTagCompound compound = new NBTTagCompound();
        rots.saveNBTData(compound);
        this.bipedRotations.loadNBTData(compound);
        this.lastBipedRotations.loadNBTData(compound);
    }
    
    public BipedRotations getBipedRotations() {
        return bipedRotations;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        guiLoaded = false;
        
        int recX = (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2));
        
        bipedParts[0] = new Rectangle_I_2D(recX + 183, 18, 8, 8);
        bipedParts[1] = new Rectangle_I_2D(recX + 183, 27, 8, 12);
        bipedParts[2] = new Rectangle_I_2D(recX + 178, 27, 4, 12);
        bipedParts[3] = new Rectangle_I_2D(recX + 192, 27, 4, 12);
        bipedParts[4] = new Rectangle_I_2D(recX + 182, 40, 4, 12);
        bipedParts[5] = new Rectangle_I_2D(recX + 188, 40, 4, 12);
        
        resetRotsButton = new GuiButtonExt(0, width / 2 + 15, 25, 50, 14, GuiHelper.getLocalizedControlName(inventoryName, "reset"));
        randomRotsButton = new GuiButtonExt(0, width / 2 + 15, 40, 50, 14, GuiHelper.getLocalizedControlName(inventoryName, "random"));
        
        bipedRotXslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25, 100, 10, "X: ", "", -180D, 180D, 0D, true, true, this);
        bipedRotYslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25 + 10, 100, 10, "Y: ", "", -180D, 180D, 0D, true, true, this);
        bipedRotZslider = new GuiCustomSlider(0, (int)((width / 2F) - (ROT_MAN_TEX_WIDTH / 2F)) + 10, 25 + 20, 100, 10, "Z: ", "", -180D, 180D, 0D, true, true, this);
        
        if (bipedRotations != null) {
            setSliderValue(bipedRotXslider, Math.toDegrees(-bipedRotations.head.rotationX));
            setSliderValue(bipedRotYslider, Math.toDegrees(-bipedRotations.head.rotationY));
            setSliderValue(bipedRotZslider, Math.toDegrees(-bipedRotations.head.rotationZ));
        }
        
        buttonList.add(resetRotsButton);
        buttonList.add(randomRotsButton);
        buttonList.add(bipedRotXslider);
        buttonList.add(bipedRotYslider);
        buttonList.add(bipedRotZslider);
        
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
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        for (int i = 0; i < bipedParts.length; i++) {
            if (bipedParts[i].isInside(mouseX, mouseY)) {
                bipedPartChange(i);
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == resetRotsButton) {
            guiLoaded = false;
            bipedRotations.resetRotations();
            bipedPartChange(activeBipedPart);
            
            guiLoaded = true;
            checkAndSendRotationValues();
        }
        if (button == randomRotsButton) {
            guiLoaded = false;
            
            Random rnd = new Random();
            for (int i = 0; i < bipedParts.length; i++) {
                BipedPart part = bipedRotations.getPartForIndex(i);
                if (part != bipedRotations.chest) {
                    part.rotationX = (float) Math.toRadians(rnd.nextFloat() * 180F - 90F);
                    part.rotationY = (float) Math.toRadians(rnd.nextFloat() * 180F - 90F);
                    part.rotationZ = (float) Math.toRadians(rnd.nextFloat() * 180F - 90F);
                    bipedPartChange(activeBipedPart);
                }
            }
            
            guiLoaded = true;
            checkAndSendRotationValues();
        }
    }
    
    public void checkAndSendRotationValues() {
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

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!guiLoaded) {
            return;
        }
        checkAndSendRotationValues();
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        int center = (int) ((float)this.width / 2F);
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
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        /*
        String headRotationLabel = GuiHelper.getLocalizedControlName(inventoryName, "label.headRotation");
        String leftArmRotationLabel = GuiHelper.getLocalizedControlName(inventoryName, "label.leftArmRotation");
        String rightArmRotationLabel = GuiHelper.getLocalizedControlName(inventoryName, "label.rightArmRotation");
        String leftLegRotationLabel = GuiHelper.getLocalizedControlName(inventoryName, "label.leftLegRotation");
        String rightLegRotationLabel = GuiHelper.getLocalizedControlName(inventoryName, "label.rightLegRotation");
        
        this.fontRendererObj.drawString(leftArmRotationLabel, 40, 20, 4210752);
        this.fontRendererObj.drawString(rightArmRotationLabel, 147, 20, 4210752);
        this.fontRendererObj.drawString(leftLegRotationLabel, 40, 65, 4210752);
        this.fontRendererObj.drawString(rightLegRotationLabel, 147, 65, 4210752);
        this.fontRendererObj.drawString(headRotationLabel, 40, 110, 4210752);
        */
    }
}
