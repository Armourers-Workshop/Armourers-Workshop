package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPartNode;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.MathHelper;
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

    private GuiCustomSlider sliderPosX;
    private GuiCustomSlider sliderPosY;
    private GuiCustomSlider sliderPosZ;

    private GuiCustomSlider sliderRotX;
    private GuiCustomSlider sliderRotY;
    private GuiCustomSlider sliderRotZ;

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

        sliderPosX = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 25, 80, 10, "X:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderPosY = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 40, 80, 10, "Y:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderPosZ = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 55, 80, 10, "Z:", "pos", -64, 64, 0, false, true, this).setFineTuneButtons(true);

        sliderRotX = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 70, 80, 10, "X:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderRotY = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 85, 80, 10, "Y:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderRotZ = new GuiCustomSlider(0, guiLeft + buttonsPosX, guiTop + buttonsPosY + 100, 80, 10, "Z:", "rot", -64, 64, 0, false, true, this).setFineTuneButtons(true);

        buttonList.add(indexDecrease);
        buttonList.add(indexIncrease);

        buttonList.add(parentDecrease);
        buttonList.add(parentIncrease);

        buttonList.add(sliderPosX);
        buttonList.add(sliderPosY);
        buttonList.add(sliderPosZ);

        buttonList.add(sliderRotX);
        buttonList.add(sliderRotY);
        buttonList.add(sliderRotZ);

        int slotSize = 18;

        int neiBump = 18;
        if (ModAddonManager.addonNEI.isVisible()) {
            neiBump = 18;
        } else {
            neiBump = 0;
        }

        // Move player inventory slots.
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

        updatePropertiesForPart(null);
    }

    private void updatePropertiesForPart(AdvancedPartNode advancedPart) {
        if (advancedPart != null) {
            sliderPosX.setValue(advancedPart.pos.x);
            sliderPosY.setValue(advancedPart.pos.y);
            sliderPosZ.setValue(advancedPart.pos.z);

            sliderRotX.setValue(advancedPart.rotationAngle.x);
            sliderRotY.setValue(advancedPart.rotationAngle.y);
            sliderRotZ.setValue(advancedPart.rotationAngle.z);
        } else {
            sliderPosX.setValue(0D);
            sliderPosY.setValue(0D);
            sliderPosZ.setValue(0D);

            sliderRotX.setValue(0D);
            sliderRotY.setValue(0D);
            sliderRotZ.setValue(0D);
        }
        sliderPosX.updateSlider();
        sliderPosY.updateSlider();
        sliderPosZ.updateSlider();

        sliderRotX.updateSlider();
        sliderRotY.updateSlider();
        sliderRotZ.updateSlider();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // this.drawDefaultBackground();
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
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
    }

    @Override
    public String getName() {
        return LibBlockNames.ADVANCED_SKIN_BUILDER;
    }
}
