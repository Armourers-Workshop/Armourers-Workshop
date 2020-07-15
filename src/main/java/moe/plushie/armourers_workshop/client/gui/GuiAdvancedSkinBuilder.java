package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPartNode;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.Vec3d;
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

    private GuiCustomSlider sliderX;
    private GuiCustomSlider sliderY;
    private GuiCustomSlider sliderZ;

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

        sliderX = new GuiCustomSlider(0, guiLeft + width - 85, guiTop + 25 + 25, 80, 10, "X:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderY = new GuiCustomSlider(0, guiLeft + width - 85, guiTop + 25 + 40, 80, 10, "Y:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderZ = new GuiCustomSlider(0, guiLeft + width - 85, guiTop + 25 + 55, 80, 10, "Z:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);

        buttonList.add(sliderX);
        buttonList.add(sliderY);
        buttonList.add(sliderZ);

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

    private void updatePropertiesForPart(AdvancedPartNode advancedPartNode) {
        if (advancedPartNode != null) {
            updateSliders(advancedPartNode.pos);
        } else {
            updateSliders(0D, 0D, 0D);
        }
    }

    private void updateSliders(Vec3d vec3d) {
        updateSliders(vec3d.x, vec3d.y, vec3d.z);
    }

    private void updateSliders(double x, double y, double z) {
        sliderX.setValue(x);
        sliderY.setValue(y);
        sliderZ.setValue(z);

        sliderX.updateSlider();
        sliderY.updateSlider();
        sliderZ.updateSlider();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // fontRenderer.drawString("Index: " + indexActive, guiLeft + 55, guiTop + 127,
        // 0xCCCCCC, true);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
    }

    @Override
    public String getName() {
        return LibBlockNames.ADVANCED_SKIN_BUILDER;
    }
}
