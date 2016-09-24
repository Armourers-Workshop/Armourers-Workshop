package riskyken.armourersWorkshop.client.gui;

import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.common.data.Rectangle_I_2D;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

public class GuiMannequinTabExtraRenders extends GuiTabPanel {
    
    private final String inventoryName;
    private final TileEntityMannequin tileEntity;
    private GuiCheckBox isChildCheck;
    public GuiCheckBox isExtraRenders;
    public GuiCheckBox isFlying;
    
    public GuiMannequinTabExtraRenders(int tabId, GuiScreen parent, String inventoryName, TileEntityMannequin tileEntity) {
        super(tabId, parent);
        this.inventoryName = inventoryName;
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        isChildCheck = new GuiCheckBox(3, this.width / 2 - 78, 25, GuiHelper.getLocalizedControlName(inventoryName, "label.isChild"), false);
        isExtraRenders = new GuiCheckBox(0, this.width / 2 - 78, 40, GuiHelper.getLocalizedControlName(inventoryName, "label.isExtraRenders"), tileEntity.isRenderExtras());
        isFlying = new GuiCheckBox(0, this.width / 2 - 78, 55, GuiHelper.getLocalizedControlName(inventoryName, "label.isFlying"), tileEntity.isFlying());
        if (((GuiMannequin)parent).tabRotations.getBipedRotations() != null) {
            isChildCheck.setIsChecked(((GuiMannequin)parent).tabRotations.getBipedRotations().isChild);
        }
        buttonList.add(isChildCheck);
        buttonList.add(isExtraRenders);
        buttonList.add(isFlying);
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
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 74);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
    }
}
