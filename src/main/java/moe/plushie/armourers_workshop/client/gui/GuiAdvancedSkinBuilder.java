package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder.SkinPartSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedSkinBuilder extends GuiContainer implements ISlider {

    private final TileEntityAdvancedSkinBuilder tileEntity;
    
    private GuiButtonExt indexIncrease;
    private GuiButtonExt indexDecrease;
    private static int indexActive;
    
    private GuiCustomSlider posOffsetXSlider;
    private GuiCustomSlider posOffsetYSlider;
    private GuiCustomSlider posOffsetZSlider;
    
    private GuiCustomSlider rotOffsetXSlider;
    private GuiCustomSlider rotOffsetYSlider;
    private GuiCustomSlider rotOffsetZSlider;
    
    public GuiAdvancedSkinBuilder(EntityPlayer player, TileEntityAdvancedSkinBuilder tileEntity) {
        super(new ContainerAdvancedSkinBuilder(player.inventory, tileEntity));
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        ScaledResolution reso = new ScaledResolution(mc);
        this.xSize = reso.getScaledWidth();
        this.ySize = reso.getScaledHeight();
        super.initGui();
        
        buttonList.clear();
        
        int buttonsPosX = 5;
        int buttonsPosY = 120;
        
        indexDecrease = new GuiButtonExt(0, guiLeft + buttonsPosX, guiTop + buttonsPosY, 20, 20, "-");
        indexIncrease = new GuiButtonExt(0, guiLeft + buttonsPosX + 25, guiTop + buttonsPosY, 20, 20, "+");
        
        posOffsetXSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 25, 80, 10, "X:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        posOffsetYSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 40, 80, 10, "Y:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        posOffsetZSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 55, 80, 10, "Z:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        
        rotOffsetXSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 70, 80, 10, "X:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        rotOffsetYSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 85, 80, 10, "Y:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        rotOffsetZSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 100, 80, 10, "Z:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        
        buttonList.add(indexDecrease);
        buttonList.add(indexIncrease);
        
        buttonList.add(posOffsetXSlider);
        buttonList.add(posOffsetYSlider);
        buttonList.add(posOffsetZSlider);
        
        buttonList.add(rotOffsetXSlider);
        buttonList.add(rotOffsetYSlider);
        buttonList.add(rotOffsetZSlider);
        
        setSlidersForIndex(indexActive);
    }
    
    private void setSlidersForIndex(int index) {
        SkinPartSettings ps = tileEntity.getPartSettings(index);
        posOffsetXSlider.setValue(ps.posOffset.x);
        posOffsetYSlider.setValue(ps.posOffset.y);
        posOffsetZSlider.setValue(ps.posOffset.z);
        
        rotOffsetXSlider.setValue(ps.rotOffset.x);
        rotOffsetYSlider.setValue(ps.rotOffset.y);
        rotOffsetZSlider.setValue(ps.rotOffset.z);
        
        posOffsetXSlider.updateSlider();
        posOffsetYSlider.updateSlider();
        posOffsetZSlider.updateSlider();
        
        rotOffsetXSlider.updateSlider();
        rotOffsetYSlider.updateSlider();
        rotOffsetZSlider.updateSlider();
    }
    
    private void setValuesForIndex(int index) {
        SkinPartSettings ps = tileEntity.getPartSettings(index);
        ps.posOffset = new Vec3d(posOffsetXSlider.getValueInt(), posOffsetYSlider.getValueInt(), posOffsetZSlider.getValueInt());
        ps.rotOffset = new Vec3d(rotOffsetXSlider.getValueInt(), rotOffsetYSlider.getValueInt(), rotOffsetZSlider.getValueInt());
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        fontRenderer.drawString("Index: " + indexActive, guiLeft + 55, guiTop + 127, 0xCCCCCC, true);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == indexDecrease) {
            indexActive--;
        }
        if (button == indexIncrease) {
            indexActive++;
        }
        indexActive = MathHelper.clamp(indexActive, 0, 9);
        setSlidersForIndex(indexActive);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        setValuesForIndex(indexActive);
    }
}
