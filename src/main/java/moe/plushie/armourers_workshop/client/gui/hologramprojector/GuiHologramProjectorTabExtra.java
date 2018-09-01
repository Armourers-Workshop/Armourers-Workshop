package moe.plushie.armourers_workshop.client.gui.hologramprojector;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.common.data.Rectangle_I_2D;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiHologramProjector;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector.PowerMode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiHologramProjectorTabExtra extends GuiTabPanel implements IDropDownListCallback {

    private final String inventoryName;
    private final TileEntityHologramProjector tileEntity;
    
    private GuiCheckBox checkGlowing;
    private GuiDropDownList dropDownPowerMode;
    
    public GuiHologramProjectorTabExtra(int tabId, GuiScreen parent, String inventoryName, TileEntityHologramProjector tileEntity) {
        super(tabId, parent, true);
        this.inventoryName = inventoryName;
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        checkGlowing = new GuiCheckBox(-1, (int)((width / 2F) - (200 / 2F)) + 10, 30, GuiHelper.getLocalizedControlName(inventoryName, "glowing"), tileEntity.isGlowing());
        dropDownPowerMode = new GuiDropDownList(0, (int)((width / 2F) - (200 / 2F)) + 10, 55, 80, "", this);
        for (int i = 0; i < PowerMode.values().length; i++) {
            PowerMode powerMode = PowerMode.values()[i];
            dropDownPowerMode.addListItem(GuiHelper.getLocalizedControlName(inventoryName, "powerMode." + powerMode.toString().toLowerCase()), powerMode.toString(), true);
            if (powerMode == tileEntity.getPowerMode()) {
                dropDownPowerMode.setListSelectedIndex(i);
            }
        }
        buttonList.add(checkGlowing);
        buttonList.add(dropDownPowerMode);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == checkGlowing) {
            MessageClientGuiHologramProjector message = new MessageClientGuiHologramProjector();
            message.setGlowing(checkGlowing.isChecked());
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 200, 78);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 138, rec.width, rec.height, 38, 38, 4, zLevel);
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        String labelPowerMode = GuiHelper.getLocalizedControlName(inventoryName, "label.powerMode");
        fontRenderer.drawString(labelPowerMode, (int)((width / 2F) - (200 / 2F)) + 10, 45, 4210752);
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        MessageClientGuiHologramProjector message = new MessageClientGuiHologramProjector();
        message.setPowerMode(PowerMode.valueOf(dropDownPowerMode.getListSelectedItem().tag));
        PacketHandler.networkWrapper.sendToServer(message);
    }
}
