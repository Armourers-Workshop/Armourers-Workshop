package moe.plushie.armourers_workshop.client.gui.mannequin;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.common.data.type.Rectangle_I_2D;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateTileProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMannequinTabExtraRenders extends GuiTabPanel<GuiMannequin> {
    
    private final String inventoryName;
    private final TileEntityMannequin tileEntity;
    private GuiCheckBox isChildCheck;
    public GuiCheckBox isExtraRenders;
    public GuiCheckBox isFlying;
    public GuiCheckBox isVisible;
    public GuiCheckBox noclip;
    
    public GuiMannequinTabExtraRenders(int tabId, GuiMannequin parent, String inventoryName, TileEntityMannequin tileEntity) {
        super(tabId, parent, true);
        this.inventoryName = inventoryName;
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        isChildCheck = new GuiCheckBox(3, this.width / 2 - 78, 25, GuiHelper.getLocalizedControlName(inventoryName, "label.isChild"), false);
        isExtraRenders = new GuiCheckBox(0, this.width / 2 - 78, 40, GuiHelper.getLocalizedControlName(inventoryName, "label.isExtraRenders"), tileEntity.PROP_RENDER_EXTRAS.get());
        isFlying = new GuiCheckBox(0, this.width / 2 - 78, 55, GuiHelper.getLocalizedControlName(inventoryName, "label.isFlying"), tileEntity.PROP_FLYING.get());
        isVisible = new GuiCheckBox(0, this.width / 2 - 78, 70, GuiHelper.getLocalizedControlName(inventoryName, "label.isVisible"), tileEntity.PROP_VISIBLE.get());
        if (parent.tabRotations.getBipedRotations() != null) {
            isChildCheck.setIsChecked(parent.tabRotations.getBipedRotations().isChild());
        }
        noclip = new GuiCheckBox(0, this.width / 2 - 78, 85, GuiHelper.getLocalizedControlName(inventoryName, "label.noclip"), tileEntity.PROP_NOCLIP.get());
        buttonList.add(isChildCheck);
        buttonList.add(isExtraRenders);
        buttonList.add(isFlying);
        buttonList.add(isVisible);
        buttonList.add(noclip);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == isExtraRenders) {
            parent.tabOffset.sendData();
        }
        if (button == isFlying) {
            parent.tabOffset.sendData();
        }
        if (button == isChildCheck) {
            parent.tabRotations.getBipedRotations().setChild(isChildCheck.isChecked());
            parent.tabRotations.checkAndSendRotationValues();
        }
        if (button == isVisible) {
            tileEntity.PROP_VISIBLE.set(isVisible.isChecked());
            MessageClientGuiUpdateTileProperties message = new MessageClientGuiUpdateTileProperties(tileEntity.PROP_VISIBLE);
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (button == noclip) {
            tileEntity.PROP_NOCLIP.set(noclip.isChecked());
            MessageClientGuiUpdateTileProperties message = new MessageClientGuiUpdateTileProperties(tileEntity.PROP_NOCLIP);
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 104);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
    }
}
