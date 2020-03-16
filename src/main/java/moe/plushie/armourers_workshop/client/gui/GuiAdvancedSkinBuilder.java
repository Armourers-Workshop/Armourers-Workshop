package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder.SkinPartSettings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedSkinBuilder extends ModGuiContainer<ContainerAdvancedSkinBuilder> implements ISlider {
    
    private static final int PADDING = 5;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INVENTORY_WIDTH = 162;
    
    private final TileEntityAdvancedSkinBuilder tileEntity;
    
    private GuiButtonExt indexDecrease;
    private GuiButtonExt indexIncrease;
    private static int indexActive;
    
    private GuiButtonExt parentDecrease;
    private GuiButtonExt parentIncrease;
    private int parentIndex;
    
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
        
        parentDecrease = new GuiButtonExt(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 120, 20, 20, "-");
        parentIncrease = new GuiButtonExt(0, guiLeft + buttonsPosX + 25, guiTop + buttonsPosY + 120, 20, 20, "+");
        
        posOffsetXSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 25, 80, 10, "X:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        posOffsetYSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 40, 80, 10, "Y:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        posOffsetZSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 55, 80, 10, "Z:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        
        rotOffsetXSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 70, 80, 10, "X:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        rotOffsetYSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 85, 80, 10, "Y:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        rotOffsetZSlider = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 100, 80, 10, "Z:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        
        buttonList.add(indexDecrease);
        buttonList.add(indexIncrease);
        
        buttonList.add(parentDecrease);
        buttonList.add(parentIncrease);
        
        buttonList.add(posOffsetXSlider);
        buttonList.add(posOffsetYSlider);
        buttonList.add(posOffsetZSlider);
        
        buttonList.add(rotOffsetXSlider);
        buttonList.add(rotOffsetYSlider);
        buttonList.add(rotOffsetZSlider);
        
        int slotSize = 18;
        
        int neiBump = 18;
        if (ModAddonManager.addonNEI.isVisible()) {
            neiBump = 18;
        } else {
            neiBump = 0;
        }
        
        //Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = inventorySlots.inventorySlots.get(x);
            slot.xPos = width - INVENTORY_WIDTH + x * 18;
            slot.yPos = this.height + 1 - PADDING - slotSize - neiBump;
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = inventorySlots.inventorySlots.get(x + y * 9 + 9);
                slot.xPos = width - INVENTORY_WIDTH + x * 18;
                slot.yPos = this.height + 1 - INVENTORY_HEIGHT - PADDING + y * slotSize - neiBump;
            }
        }
        
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
        //this.drawDefaultBackground();
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
        if (button == parentDecrease) {
            MessageClientGuiButton message = new MessageClientGuiButton((byte) 0);
            PacketHandler.networkWrapper.sendToServer(message);
        }
        indexActive = MathHelper.clamp(indexActive, 0, 9);
        setSlidersForIndex(indexActive);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        setValuesForIndex(indexActive);
    }

    @Override
    public String getName() {
        return LibBlockNames.ADVANCED_SKIN_BUILDER;
    }
}
