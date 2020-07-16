package moe.plushie.armourers_workshop.client.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerMain.DropDownItemSkin;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView.GuiTreeViewItem;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPartNode;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedSkinBuilder extends ModGuiContainer<ContainerAdvancedSkinBuilder> implements ISlider, IDropDownListCallback {

    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);
    private static final int PADDING = 2;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INVENTORY_WIDTH = 162;

    private final TileEntityAdvancedSkinBuilder tileEntity;

    private ISkinType skinType = null;

    private Rectangle[] recLayout;
    private Rectangle recTreeView;
    private Rectangle recNoteProp;
    private Rectangle recSetting;
    private Rectangle recSkinPreview;

    private GuiDropDownList dropDownSkinType;

    private GuiTreeView treeView;
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

        recTreeView = new Rectangle(PADDING, PADDING, 120, height - 90 - PADDING * 3);
        recNoteProp = new Rectangle(PADDING, height - 90 - PADDING, recTreeView.width, 90);
        recSetting = new Rectangle(recNoteProp.width + PADDING * 2, recNoteProp.y, width - recNoteProp.width - PADDING * 3, recNoteProp.height);
        recSkinPreview = new Rectangle(recTreeView.width + PADDING * 2, PADDING, width - recTreeView.width - PADDING * 3, height - recNoteProp.height - PADDING * 3);
        recLayout = new Rectangle[] { recTreeView, recNoteProp, recSetting, recSkinPreview };

        buttonList.clear();

        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        dropDownSkinType = new GuiDropDownList(0, recSkinPreview.x + recSkinPreview.width - 80 - PADDING, recSkinPreview.y + PADDING, 80, "", this);
        ArrayList<ISkinType> skinList = str.getRegisteredSkinTypes();
        int skinCount = 0;
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (!skinType.isHidden() & skinType != SkinTypeRegistry.skinOutfit) {
                String skinLocalizedName = str.getLocalizedSkinTypeName(skinType);
                String skinRegistryName = skinType.getRegistryName();
                DropDownItemSkin item = new DropDownItemSkin(skinLocalizedName, skinRegistryName, skinType.enabled(), skinType);
                dropDownSkinType.addListItem(item);
                // if (skinType == tileEntity.getSkinType()) {
                // dropDownSkinType.setListSelectedIndex(skinCount);
                // }
                skinCount++;
            }
        }
        buttonList.add(dropDownSkinType);
        updateActiveSkinType();

        treeView = new GuiTreeView(5, 5, 100, height - 15 - 100);
        // buttonList.add(treeView);

        int sliderPosX = recSetting.x + PADDING;
        int sliderPosY = recSetting.y + PADDING + 10;

        sliderX = new GuiCustomSlider(0, sliderPosX, sliderPosY, 80, 10, "X:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderY = new GuiCustomSlider(0, sliderPosX, sliderPosY + 15, 80, 10, "Y:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderZ = new GuiCustomSlider(0, sliderPosX, sliderPosY + 30, 80, 10, "Z:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
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
            slot.xPos = width - INVENTORY_WIDTH + x * 18 - PADDING;
            slot.yPos = this.height + 1 - PADDING * 2 - slotSize - neiBump;
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = inventorySlots.inventorySlots.get(x + y * 9 + 9);
                slot.xPos = width - INVENTORY_WIDTH + x * 18 - PADDING;
                slot.yPos = this.height + 1 - INVENTORY_HEIGHT - PADDING * 2 + y * slotSize - neiBump;
            }
        }

        for (int i = getContainer().getPlayerInvStartIndex(); i < getContainer().getPlayerInvEndIndex(); i++) {
            Slot slot = getContainer().getSlot(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable) slot).setVisible(false);
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

    private void updateActiveSkinType() {
        skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(dropDownSkinType.getListSelectedItem().tag);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        for (Rectangle rec : recLayout) {
            drawRect(rec.x, rec.y, rec.x + rec.width, rec.y + rec.height, 0x44808080);
        }

        fontRenderer.drawString("Tree View:", recTreeView.x + PADDING, recTreeView.y + PADDING, 0xCCFFFFFF);
        fontRenderer.drawString("Node Options:", recNoteProp.x + PADDING, recNoteProp.y + PADDING, 0xCCFFFFFF);
        fontRenderer.drawString("Settings:", recSetting.x + PADDING, recSetting.y + PADDING, 0xCCFFFFFF);
        fontRenderer.drawString("Preview:", recSkinPreview.x + PADDING, recSkinPreview.y + PADDING, 0xCCFFFFFF);

        if (skinType != null) {
            for (int i = 0; i < skinType.getSkinParts().size(); i++) {
                ISkinPartType partType = skinType.getSkinParts().get(i);
                fontRenderer.drawString("root: " + partType.getPartName(), recTreeView.x + PADDING, recTreeView.y + PADDING + 10 + 20 * i, 0xCCFFFFFF);
                fontRenderer.drawString("sub note: " + (i + 1), recTreeView.x + PADDING + 10, recTreeView.y + PADDING + 10 + 20 * i + 10, 0xCCFFFFFF);
            }
        }

        String[] nodeOptions = { "name", "skin", "enabled", "scale", "mirror", "position", "rotation", "rotation position" };
        for (int i = 0; i < nodeOptions.length; i++) {
            fontRenderer.drawString(nodeOptions[i], recNoteProp.x + PADDING, recNoteProp.y + PADDING + 10 * i + 10, 0xCCFFFFFF);
        }

        // drawRect(5, height - 105, width - 5, height - 5, 0xCCFFFFFF);
        // fontRenderer.drawString("Index: " + indexActive, guiLeft + 55, guiTop + 127,
        // 0xCCCCCC, true);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        dropDownSkinType.drawForeground(mc, mouseX, mouseY, 0);
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

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        updateActiveSkinType();
    }

    public static class GuiTreeViewPartNote extends GuiTreeViewItem {

        private AdvancedPartNode advancedPartNode;

        public GuiTreeViewPartNote(String name) {
            super(name);
        }

        public AdvancedPartNode getAdvancedPartNode() {
            return advancedPartNode;
        }
    }
}
