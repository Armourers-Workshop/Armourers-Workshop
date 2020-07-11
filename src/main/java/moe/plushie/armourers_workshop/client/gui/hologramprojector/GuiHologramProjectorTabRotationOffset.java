package moe.plushie.armourers_workshop.client.gui.hologramprojector;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.common.data.type.Rectangle_I_2D;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiHologramProjectorTabRotationOffset extends GuiTabPanel implements ISlider {
    
    private final String inventoryName;
    private final TileEntityHologramProjector tileEntity;
    
    private boolean guiLoaded = false;
    private GuiCustomSlider sliderOffsetX;
    private GuiCustomSlider sliderOffsetY;
    private GuiCustomSlider sliderOffsetZ;
    private GuiCheckBox checkShowRotationPoint;
    
    public GuiHologramProjectorTabRotationOffset(int tabId, GuiScreen parent, String inventoryName, TileEntityHologramProjector tileEntity) {
        super(tabId, parent, true);
        this.inventoryName = inventoryName;
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        guiLoaded = false;
        
        sliderOffsetX = new GuiCustomSlider(-1, (int)((width / 2F) - (200 / 2F)) + 10, 30, 178, 10, "X: ", "", -64D, 64D, tileEntity.getRotationOffsetX().get(), false, true, this);
        sliderOffsetY = new GuiCustomSlider(-1, (int)((width / 2F) - (200 / 2F)) + 10, 45, 178, 10, "Y: ", "", -64D, 64D, tileEntity.getRotationOffsetY().get(), false, true, this);
        sliderOffsetZ = new GuiCustomSlider(-1, (int)((width / 2F) - (200 / 2F)) + 10, 60, 178, 10, "Z: ", "", -64D, 64D, tileEntity.getRotationOffsetZ().get(), false, true, this);
        checkShowRotationPoint = new GuiCheckBox(-1, (int)((width / 2F) - (200 / 2F)) + 10, 75, GuiHelper.getLocalizedControlName(inventoryName, "showRotationPoint"), tileEntity.isShowRotationPoint());
        
        sliderOffsetX.setFineTuneButtons(true);
        sliderOffsetY.setFineTuneButtons(true);
        sliderOffsetZ.setFineTuneButtons(true);
        
        buttonList.add(sliderOffsetX);
        buttonList.add(sliderOffsetY);
        buttonList.add(sliderOffsetZ);
        buttonList.add(checkShowRotationPoint);
        
        guiLoaded = true;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == checkShowRotationPoint) {
            tileEntity.setShowRotationPoint(checkShowRotationPoint.isChecked());
        }
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 200, 92);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 138, rec.width, rec.height, 38, 38, 4, zLevel);
    }
    
    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (!guiLoaded) {
            return;
        }
        int xOffset = sliderOffsetX.getValueInt();
        int yOffset = sliderOffsetY.getValueInt();
        int zOffset = sliderOffsetZ.getValueInt();
        tileEntity.getRotationOffsetX().set(xOffset);
        tileEntity.getRotationOffsetY().set(yOffset);
        tileEntity.getRotationOffsetZ().set(zOffset);
        tileEntity.updateProperty(tileEntity.getRotationOffsetX(), tileEntity.getRotationOffsetY(), tileEntity.getRotationOffsetZ());
    }
}