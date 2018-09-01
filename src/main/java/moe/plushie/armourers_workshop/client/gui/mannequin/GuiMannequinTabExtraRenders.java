package moe.plushie.armourers_workshop.client.gui.mannequin;

import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.common.data.Rectangle_I_2D;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class GuiMannequinTabExtraRenders extends GuiTabPanel {
    
    private final String inventoryName;
    private final TileEntityMannequin tileEntity;
    private GuiCheckBox isChildCheck;
    public GuiCheckBox isExtraRenders;
    public GuiCheckBox isFlying;
    public GuiCheckBox isVisible;
    
    public GuiMannequinTabExtraRenders(int tabId, GuiScreen parent, String inventoryName, TileEntityMannequin tileEntity) {
        super(tabId, parent, true);
        this.inventoryName = inventoryName;
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        isChildCheck = new GuiCheckBox(3, this.width / 2 - 78, 25, GuiHelper.getLocalizedControlName(inventoryName, "label.isChild"), false);
        isExtraRenders = new GuiCheckBox(0, this.width / 2 - 78, 40, GuiHelper.getLocalizedControlName(inventoryName, "label.isExtraRenders"), tileEntity.isRenderExtras());
        isFlying = new GuiCheckBox(0, this.width / 2 - 78, 55, GuiHelper.getLocalizedControlName(inventoryName, "label.isFlying"), tileEntity.isFlying());
        isVisible = new GuiCheckBox(0, this.width / 2 - 78, 70, GuiHelper.getLocalizedControlName(inventoryName, "label.isVisible"), tileEntity.isVisible());
        if (((GuiMannequin)parent).tabRotations.getBipedRotations() != null) {
            isChildCheck.setIsChecked(((GuiMannequin)parent).tabRotations.getBipedRotations().isChild);
        }
        buttonList.add(isChildCheck);
        buttonList.add(isExtraRenders);
        buttonList.add(isFlying);
        buttonList.add(isVisible);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == isExtraRenders) {
            ((GuiMannequin)parent).tabOffset.sendData();
        }
        if (button == isFlying) {
            ((GuiMannequin)parent).tabOffset.sendData();
        }
        if (button == isChildCheck) {
            ((GuiMannequin)parent).tabRotations.getBipedRotations().isChild = isChildCheck.isChecked();
            ((GuiMannequin)parent).tabRotations.checkAndSendRotationValues();
        }
        if (button == isVisible) {
            ((GuiMannequin)parent).tabOffset.sendData();
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 88);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
    }
}
