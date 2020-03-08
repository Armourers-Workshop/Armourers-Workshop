package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateMannequin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeManExtras extends GuiTabPanel {

    private final EntityMannequin entityMannequin;
    
    private GuiCheckBox isChildCheck;
    private GuiCheckBox isExtraRenders;
    private GuiCheckBox isFlying;
    private GuiCheckBox isVisible;
    private GuiCheckBox noclip;
    
    private final String guiName = "wardrobe.tab.man_extras";
    
    public GuiTabWardrobeManExtras(int tabId, GuiScreen parent, EntityMannequin entityMannequin) {
        super(tabId, parent);
        this.entityMannequin = entityMannequin;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        isChildCheck = new GuiCheckBox(3, 81, 25, GuiHelper.getLocalizedControlName(guiName, "label.isChild"), entityMannequin.getBipedRotations().isChild());
        isExtraRenders = new GuiCheckBox(0, 81, 40, GuiHelper.getLocalizedControlName(guiName, "label.isExtraRenders"), entityMannequin.isRenderExtras());
        isFlying = new GuiCheckBox(0, 81, 55, GuiHelper.getLocalizedControlName(guiName, "label.isFlying"), entityMannequin.isFlying());
        isVisible = new GuiCheckBox(0, 81, 70, GuiHelper.getLocalizedControlName(guiName, "label.isVisible"), entityMannequin.isVisible());
        noclip = new GuiCheckBox(0, 81, 85, GuiHelper.getLocalizedControlName(guiName, "label.noclip"), entityMannequin.isNoClip());
        
        buttonList.add(isChildCheck);
        buttonList.add(isExtraRenders);
        buttonList.add(isFlying);
        buttonList.add(isVisible);
        buttonList.add(noclip);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        MessageClientGuiUpdateMannequin message = new MessageClientGuiUpdateMannequin(entityMannequin);
        if (button == isExtraRenders) {
            message.setExtraRenders(isExtraRenders.isChecked());
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button == isFlying) {
            message.setFlying(isFlying.isChecked());
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button == isChildCheck) {
            BipedRotations bipedRotations = entityMannequin.getBipedRotations();
            bipedRotations.setChild(isChildCheck.isChecked());
            message.setBipedRotations(bipedRotations);
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button == isVisible) {
            message.setVisible(isVisible.isChecked());
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button == noclip) {
            message.setNoClip(noclip.isChecked());
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
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
}
