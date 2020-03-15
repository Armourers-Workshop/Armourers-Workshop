package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations.BipedPart;
import moe.plushie.armourers_workshop.common.data.type.Rectangle_I_2D;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateMannequin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeManRotations extends GuiTabPanel implements ISlider {

    private static final ResourceLocation TEXTURE_2 = new ResourceLocation(LibGuiResources.GUI_WARDROBE_2);
    
    private final EntityMannequin entityMannequin;

    private GuiButtonExt resetRotsButton;
    private GuiButtonExt randomRotsButton;

    private BipedRotations bipedRotations;
    private BipedRotations lastBipedRotations;

    private GuiCustomSlider bipedRotXslider;
    private GuiCustomSlider bipedRotYslider;
    private GuiCustomSlider bipedRotZslider;

    private Rectangle_I_2D[] bipedParts = new Rectangle_I_2D[6];
    private BipedPart activeBipedPart = BipedPart.HEAD;

    private boolean guiLoaded = false;
    private final String guiName = "wardrobe.tab.man_rotations";

    int partsDisplayX = 80;
    int partsDisplayY = 50;
    
    public GuiTabWardrobeManRotations(int tabId, GuiScreen parent, EntityMannequin entityMannequin) {
        super(tabId, parent);
        this.entityMannequin = entityMannequin;
        bipedRotations = new BipedRotations();
        lastBipedRotations = new BipedRotations();
        updateRotationData(entityMannequin.getBipedRotations());
    }
    
    @Override
    public void tabChanged(int tabIndex) {
        if (tabIndex == getTabId()) {
            updateRotationData(entityMannequin.getBipedRotations());
        }
    }

    public void updateRotationData(BipedRotations rots) {
        NBTTagCompound compound = new NBTTagCompound();
        rots.saveNBTData(compound);
        this.bipedRotations.loadNBTData(compound);
        this.lastBipedRotations.loadNBTData(compound);
    }
    
    public void updateLastRotations() {
        NBTTagCompound compound = new NBTTagCompound();
        bipedRotations.saveNBTData(compound);
        lastBipedRotations.loadNBTData(compound);
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);

        partsDisplayX = 81;
        partsDisplayY = 26;

        bipedParts[0] = new Rectangle_I_2D(x + partsDisplayX + 8, y + partsDisplayY + 3, 8, 8);
        bipedParts[1] = new Rectangle_I_2D(x + partsDisplayX + 8, y + partsDisplayY + 12, 8, 12);
        bipedParts[2] = new Rectangle_I_2D(x + partsDisplayX + 3, y + partsDisplayY + 12, 4, 12);
        bipedParts[3] = new Rectangle_I_2D(x + partsDisplayX + 17, y + partsDisplayY + 12, 4, 12);
        bipedParts[4] = new Rectangle_I_2D(x + partsDisplayX + 7, y + partsDisplayY + 25, 4, 12);
        bipedParts[5] = new Rectangle_I_2D(x + partsDisplayX + 13, y + partsDisplayY + 25, 4, 12);

        resetRotsButton = new GuiButtonExt(0, 81, 70, 100, 16, GuiHelper.getLocalizedControlName(guiName, "reset"));
        randomRotsButton = new GuiButtonExt(0, 81, 90, 100, 16, GuiHelper.getLocalizedControlName(guiName, "random"));

        bipedRotXslider = new GuiCustomSlider(0, 110, 25, 160, 10, "X: ", "", -180D, 180D, 0D, true, true, this).setFineTuneButtons(true);
        bipedRotYslider = new GuiCustomSlider(0, 110, 25 + 11, 160, 10, "Y: ", "", -180D, 180D, 0D, true, true, this).setFineTuneButtons(true);
        bipedRotZslider = new GuiCustomSlider(0, 110, 25 + 22, 160, 10, "Z: ", "", -180D, 180D, 0D, true, true, this).setFineTuneButtons(true);

        if (bipedRotations != null) {
            float[] rots = bipedRotations.getPartRotations(BipedPart.HEAD);
            setSliderValue(bipedRotXslider, Math.toDegrees(-rots[0]));
            setSliderValue(bipedRotYslider, Math.toDegrees(-rots[1]));
            setSliderValue(bipedRotZslider, Math.toDegrees(-rots[2]));
        }

        buttonList.add(resetRotsButton);
        buttonList.add(randomRotsButton);
        buttonList.add(bipedRotXslider);
        buttonList.add(bipedRotYslider);
        buttonList.add(bipedRotZslider);

        bipedPartChange(BipedPart.HEAD);
        guiLoaded = true;
    }

    private void setSliderValue(GuiCustomSlider slider, double value) {
        slider.setValue(value);
        slider.precision = 2;
        slider.updateSlider();
    }

    private void bipedPartChange(BipedPart bipedPart) {
        activeBipedPart = bipedPart;
        float[] rots = bipedRotations.getPartRotations(activeBipedPart);
        guiLoaded = false;
        bipedRotXslider.setValue(Math.toDegrees(-rots[0]));
        bipedRotYslider.setValue(Math.toDegrees(-rots[1]));
        bipedRotZslider.setValue(Math.toDegrees(-rots[2]));
        bipedRotXslider.updateSlider();
        bipedRotYslider.updateSlider();
        bipedRotZslider.updateSlider();
        guiLoaded = true;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        for (int i = 0; i < bipedParts.length; i++) {
            if (bipedParts[i].isInside(mouseX, mouseY)) {
                bipedPartChange(BipedPart.values()[i]);
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
            for (BipedPart bipedPart : BipedPart.values()) {
                float[] rots = bipedRotations.getPartRotations(bipedPart);
                if (bipedPart != BipedPart.CHEST) {
                    float x = 0F;
                    float y = 0F;
                    float z = 0F;
                    for (int j = 0; j < 3; j++) {
                        x += (float) Math.toRadians(rnd.nextFloat() * 60F - 30F);
                        y += (float) Math.toRadians(rnd.nextFloat() * 60F - 30F);
                        z += (float) Math.toRadians(rnd.nextFloat() * 60F - 30F);
                    }
                    bipedRotations.setPartRotations(bipedPart, x, y, z);
                }
            }
            bipedPartChange(activeBipedPart);
            guiLoaded = true;
            checkAndSendRotationValues();
        }
    }

    public void checkAndSendRotationValues() {
        if (!this.bipedRotations.equals(this.lastBipedRotations)) {
            entityMannequin.setBipedRotations(bipedRotations);
            MessageClientGuiUpdateMannequin message = new MessageClientGuiUpdateMannequin(entityMannequin);
            message.setBipedRotations(bipedRotations);
            PacketHandler.networkWrapper.sendToServer(message);
            updateLastRotations();
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE_2);
        drawTexturedModalRect(x + partsDisplayX, y + partsDisplayY, 22, 0, 24, 40);
        for (int i = 0; i < bipedParts.length; i++) {
            int colour = 0xCCFFFF00;
            if (bipedParts[i].isInside(mouseX, mouseY)) {
                colour = 0xCCFFFFFF;
            }
            if (BipedPart.values()[i] == activeBipedPart) {
                colour = 0xCC00FF00;
            }
            drawRect(bipedParts[i].x, bipedParts[i].y, bipedParts[i].x + bipedParts[i].width, bipedParts[i].y + bipedParts[i].height, colour);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        // Draw entity preview.
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        ((GuiWardrobe) parent).drawPlayerPreview(x, y, mouseX, mouseY);
        GL11.glPopMatrix();
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!guiLoaded) {
            return;
        }
        float[] rots = bipedRotations.getPartRotations(activeBipedPart);
        rots[0] = (float) Math.toRadians(-bipedRotXslider.getValue());
        rots[1] = (float) Math.toRadians(-bipedRotYslider.getValue());
        rots[2] = (float) Math.toRadians(-bipedRotZslider.getValue());
        bipedRotations.setPartRotations(activeBipedPart, rots);
        checkAndSendRotationValues();
    }
}
