package moe.plushie.armourers_workshop.client.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerMain.DropDownItemSkin;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCustomSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTextFieldCustom;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView.GuiTreeViewItem;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView.IGuiTreeViewCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView.IGuiTreeViewItem;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.AdvancedPartRenderer;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPartNode;
import moe.plushie.armourers_workshop.common.skin.advanced.IAdvancedPartParent;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAdvancedSkinBuilder extends ModGuiContainer<ContainerAdvancedSkinBuilder> implements ISlider, IDropDownListCallback, IGuiTreeViewCallback, IAdvancedPartParent {

    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);
    private static final int PADDING = 2;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INVENTORY_WIDTH = 162;

    private final TileEntityAdvancedSkinBuilder tileEntity;

    private ISkinType skinType = null;

    private Rectangle recTreeView;
    private Rectangle recNoteProp;
    private Rectangle recSetting;
    private Rectangle recSkinPreview;
    private Rectangle[] recLayout;

    // Tree View
    private GuiTreeView treeView;
    private GuiIconButton buttonAdd;
    private GuiIconButton buttonRemove;

    // Node Options
    private GuiDropDownList dropDownNoteSettings;

    // Settings
    private GuiTextFieldCustom textSetting;
    private ISkinIdentifier identifierSetting;
    private GuiCheckBox checkBoxSetting;
    private GuiCustomSlider sliderSetting;
    private GuiCustomSlider sliderSettingX;
    private GuiCustomSlider sliderSettingY;
    private GuiCustomSlider sliderSettingZ;

    // Preview
    private GuiDropDownList dropDownSkinType;

    private AdvancedPartNode builtNode = null;

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

        // Tree View
        treeView = new GuiTreeView(recTreeView.x + PADDING, recTreeView.y + PADDING, recTreeView.width - PADDING * 2, recTreeView.height - 20 - PADDING);
        treeView.setCallback(this);
        buttonList.add(treeView);

        buttonAdd = new GuiIconButton(this, 0, recTreeView.x + PADDING, recTreeView.y + recTreeView.height - PADDING - 16, 16, 16, TEXTURE_BUTTONS);
        buttonAdd.setIconLocation(208, 176, 16, 16).setDrawButtonBackground(false).setHoverText("Add Node");
        buttonList.add(buttonAdd);

        buttonRemove = new GuiIconButton(this, 0, recTreeView.x + PADDING * 2 + 16, recTreeView.y + recTreeView.height - PADDING - 16, 16, 16, TEXTURE_BUTTONS);
        buttonRemove.setIconLocation(208, 160, 16, 16).setDrawButtonBackground(false).setHoverText("Remove Node");
        buttonList.add(buttonRemove);

        // Node Options
        dropDownNoteSettings = new GuiDropDownList(0, recNoteProp.x + PADDING, recNoteProp.y + PADDING + 10, recNoteProp.width - PADDING * 2, "", this);
        updateSettingsDropDown();
        dropDownNoteSettings.setMaxDisplayCount(5);
        buttonList.add(dropDownNoteSettings);

        // Settings
        textSetting = new GuiTextFieldCustom(recSetting.x + PADDING, recSetting.y + PADDING + 10, 100, 16);
        
        checkBoxSetting = new GuiCheckBox(0, recSetting.x + PADDING, recSetting.y + PADDING + 10, "", false);
        checkBoxSetting.setTextColour(0xFFFFFFFF);
        buttonList.add(checkBoxSetting);
        int sliderPosX = recSetting.x + PADDING;
        int sliderPosY = recSetting.y + PADDING + 10;
        sliderSetting = new GuiCustomSlider(0, sliderPosX, sliderPosY, 100, 10, "Scale:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderSettingX = new GuiCustomSlider(0, sliderPosX, sliderPosY, 100, 10, "X:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderSettingY = new GuiCustomSlider(0, sliderPosX, sliderPosY + 15, 100, 10, "Y:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        sliderSettingZ = new GuiCustomSlider(0, sliderPosX, sliderPosY + 30, 100, 10, "Z:", "", -64, 64, 0, false, true, this).setFineTuneButtons(true);
        buttonList.add(sliderSetting);
        buttonList.add(sliderSettingX);
        buttonList.add(sliderSettingY);
        buttonList.add(sliderSettingZ);

        // setPlayerSlotVisible(false);
        movePlayerInventorySlots(width - INVENTORY_WIDTH - PADDING, height - INVENTORY_HEIGHT - PADDING);

        // setPlayerSlotVisible(true);

        // Preview
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
        updateActiveSkinType();
        buttonList.add(dropDownSkinType);

        updateSetting();
    }

    @Override
    public void updateScreen() {
        textSetting.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
        if (textSetting.keyTyped(c, keycode)) {
            applySetting();
        }
        super.keyTyped(c, keycode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        textSetting.mouseClicked(mouseX, mouseY, button);
        // TODO Auto-generated method stub
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    private void updateSettingsDropDown() {
        dropDownNoteSettings.clearList();
        GuiTreeView.IGuiTreeViewItem treeViewItem = treeView.getSelectedItem();
        if (treeViewItem != null && treeViewItem instanceof GuiTreeViewPartNote) {
            GuiTreeViewPartNote treeNote = (GuiTreeViewPartNote) treeViewItem;
            for (PartNodeSetting setting : PartNodeSetting.values()) {
                boolean enabled = true;
                if (treeNote.isLocked()) {
                    enabled = setting.onRoot;
                }
                dropDownNoteSettings.addListItem(setting.name(), setting.name(), enabled);
            }
        }
    }

    private void movePlayerInventorySlots(int xPos, int yPos) {
        int slotSize = 18;

        int neiBump = 18;
        if (ModAddonManager.addonNEI.isVisible()) {
            neiBump = 18;
        } else {
            neiBump = 0;
        }
        int playerInvY = yPos;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            Slot slot = inventorySlots.inventorySlots.get(x);
            slot.xPos = xPos + x * 18;
            slot.yPos = yPos - neiBump + 58;
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = inventorySlots.inventorySlots.get(x + y * 9 + 9);
                slot.xPos = xPos + x * 18;
                slot.yPos = yPos + y * slotSize - neiBump;
            }
        }
    }

    private void setPlayerSlotVisible(boolean visible) {
        for (int i = getContainer().getPlayerInvStartIndex(); i < getContainer().getPlayerInvEndIndex(); i++) {
            Slot slot = getContainer().getSlot(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable) slot).setVisible(visible);
            }
        }
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
        sliderSettingX.visible = true;
        sliderSettingY.visible = true;
        sliderSettingZ.visible = true;

        sliderSettingX.setValue(x);
        sliderSettingY.setValue(y);
        sliderSettingZ.setValue(z);

        sliderSettingX.updateSlider();
        sliderSettingY.updateSlider();
        sliderSettingZ.updateSlider();
    }

    private void updateActiveSkinType() {
        skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(dropDownSkinType.getListSelectedItem().tag);
        treeView.getItems().clear();
        if (skinType != null) {
            for (int i = 0; i < skinType.getSkinParts().size(); i++) {
                ISkinPartType partType = skinType.getSkinParts().get(i);
                String partName = SkinTypeRegistry.INSTANCE.getLocalizedSkinPartTypeName(partType);
                GuiTreeViewPartNote treeViewPartNote = new GuiTreeViewPartNote("root - " + partName);
                treeViewPartNote.setLocked(true);
                treeViewPartNote.setColour(0xFFCCCCFF);

                for (int j = 0; j < 2; j++) {
                    GuiTreeViewPartNote sub1 = new GuiTreeViewPartNote(partName + " sub " + (j + 1));
                    for (int z = 0; z < 2; z++) {
                        sub1.getSubItems().add(new GuiTreeViewPartNote("deep " + (z + 1)));
                    }
                    //treeViewPartNote.getSubItems().add(sub1);
                }
                treeView.addItem(treeViewPartNote);
            }
            GuiTreeViewPartNote treeViewPartFree = new GuiTreeViewPartNote("root - Float");
            treeViewPartFree.setLocked(true);
            treeViewPartFree.setColour(0xFFCCCCFF);
            treeViewPartFree.getAdvancedPartNode().enabled = false;
            treeView.addItem(treeViewPartFree);

            GuiTreeViewPartNote treeViewPartStatic = new GuiTreeViewPartNote("root - Static");
            treeViewPartStatic.setLocked(true);
            treeViewPartStatic.setColour(0xFFCCCCFF);
            treeViewPartStatic.getAdvancedPartNode().enabled = false;
            treeView.addItem(treeViewPartStatic);
        }
    }

    private void updateSetting() {
        PartNodeSetting nodeSetting = getActiveSetting();
        GuiTreeViewPartNote treeViewPartNote = null;
        GuiTreeView.IGuiTreeViewItem treeViewItem = treeView.getSelectedItem();
        if (treeViewItem != null && treeViewItem instanceof GuiTreeViewPartNote) {
            treeViewPartNote = (GuiTreeViewPartNote) treeViewItem;
        }

        textSetting.visible = false;
        identifierSetting = null;
        checkBoxSetting.visible = false;
        sliderSetting.visible = false;
        sliderSettingX.visible = false;
        sliderSettingY.visible = false;
        sliderSettingZ.visible = false;

        if (nodeSetting == null | treeViewPartNote == null) {
            return;
        }

        switch (nodeSetting) {
        case NAME:
            textSetting.visible = true;
            break;
        case SKIN:
            identifierSetting = treeViewPartNote.getSkinIdentifier();
            break;
        case ENABLED:
            checkBoxSetting.visible = true;
            checkBoxSetting.displayString = "Enabled?";
            checkBoxSetting.setIsChecked(treeViewPartNote.getAdvancedPartNode().enabled);
            break;
        case SCALE:
            sliderSetting.visible = true;
            sliderSetting.setValue(treeViewPartNote.getAdvancedPartNode().scale);
            sliderSetting.updateSlider();
            break;
        case MIRROR:
            checkBoxSetting.visible = true;
            checkBoxSetting.displayString = "Mirror?";
            checkBoxSetting.setIsChecked(treeViewPartNote.getAdvancedPartNode().mirror);
            break;
        case POSITION:
            updateSliders(treeViewPartNote.getAdvancedPartNode().pos);
            break;
        case ROTATION:
            updateSliders(treeViewPartNote.getAdvancedPartNode().rotationAngle);
            break;
        case ROTATION_POSITION:
            updateSliders(treeViewPartNote.getAdvancedPartNode().rotationPos);
            break;
        }
    }

    private void applySetting() {
        PartNodeSetting nodeSetting = getActiveSetting();
        GuiTreeViewPartNote treeViewPartNote = null;
        GuiTreeView.IGuiTreeViewItem treeViewItem = treeView.getSelectedItem();
        if (treeViewItem != null && treeViewItem instanceof GuiTreeViewPartNote) {
            treeViewPartNote = (GuiTreeViewPartNote) treeViewItem;
        }

        if (nodeSetting == null | treeViewPartNote == null) {
            return;
        }

        switch (nodeSetting) {
        case NAME:
            treeViewPartNote.setName(textSetting.getText());
            break;
        case SKIN:
            break;
        case ENABLED:
            treeViewPartNote.getAdvancedPartNode().enabled = checkBoxSetting.isChecked();
            break;
        case SCALE:
            treeViewPartNote.getAdvancedPartNode().scale = sliderSetting.getValueInt();
            break;
        case MIRROR:
            treeViewPartNote.getAdvancedPartNode().mirror = checkBoxSetting.isChecked();
            break;
        case POSITION:
            treeViewPartNote.getAdvancedPartNode().pos = new Vec3d(sliderSettingX.getValueInt(), sliderSettingY.getValueInt(), sliderSettingZ.getValueInt());
            break;
        case ROTATION:
            treeViewPartNote.getAdvancedPartNode().rotationAngle = new Vec3d(sliderSettingX.getValueInt(), sliderSettingY.getValueInt(), sliderSettingZ.getValueInt());
            break;
        case ROTATION_POSITION:
            treeViewPartNote.getAdvancedPartNode().rotationPos = new Vec3d(sliderSettingX.getValueInt(), sliderSettingY.getValueInt(), sliderSettingZ.getValueInt());
            break;
        }
    }

    private AdvancedPartNode convertTreeToAdvancedPartNode() {
        AdvancedPartNode partNode = new AdvancedPartNode(-1, "main root");
        convertTreeToAdvancedPartNode(partNode, treeView.getItems());
        return partNode;
    }

    private AdvancedPartNode convertTreeToAdvancedPartNode(AdvancedPartNode advancedPartNode, ArrayList<GuiTreeView.IGuiTreeViewItem> treeViewItems) {
        for (GuiTreeView.IGuiTreeViewItem treeViewItem : treeViewItems) {
            GuiTreeViewPartNote partNote = (GuiTreeViewPartNote) treeViewItem;
            AdvancedPartNode node = partNote.getAdvancedPartNode().clone();
            
            advancedPartNode.getChildren().add(node);
            convertTreeToAdvancedPartNode(node, treeViewItem.getSubItems());

        }
        return advancedPartNode;
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
        textSetting.drawButton(mc, mouseX, mouseY, partialTicks);
        fontRenderer.drawString("Tree View:", recTreeView.x + PADDING, recTreeView.y + PADDING, 0xCCFFFFFF);
        fontRenderer.drawString("Node Options:", recNoteProp.x + PADDING, recNoteProp.y + PADDING, 0xCCFFFFFF);
        fontRenderer.drawString("Settings:", recSetting.x + PADDING, recSetting.y + PADDING, 0xCCFFFFFF);
        fontRenderer.drawString("Preview:", recSkinPreview.x + PADDING, recSkinPreview.y + PADDING, 0xCCFFFFFF);

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        dropDownSkinType.drawForeground(mc, mouseX, mouseY, 0);
        dropDownNoteSettings.drawForeground(mc, mouseX, mouseY, 0);

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(recSkinPreview.x + recSkinPreview.width / 2, recSkinPreview.y + recSkinPreview.height / 2, 500);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableNormalize();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableNormalize();
        GlStateManager.enableColorMaterial();
        ModRenderHelper.enableAlphaBlend();
        GlStateManager.enableDepth();
        
        float scale = 50;
        GlStateManager.scale((-scale), scale, scale);
        
        GL11.glRotatef(-40, 1.0F, 0.0F, 0.0F);
        float rotation = (float) ((double) System.currentTimeMillis() / 10 % 360);
        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        
        mc.renderEngine.bindTexture(DefaultPlayerSkin.getDefaultSkinLegacy());
        GlStateManager.pushMatrix();
        
        //GlStateManager.scale(16F / 1F, 1F, 1F);
        ModelPlayer modelPlayer = new ModelPlayer(1, false);
        modelPlayer.bipedHead.render(1F / 16F);
        //ArmourerRenderHelper.renderBuildingGrid(skinType, 1F / 16F, true, new SkinProperties(), false);
        GlStateManager.popMatrix();
        
        
        SkinRenderData renderData = new SkinRenderData(1F / 16F, null, null, 0, true, true, true, null);
        AdvancedPartRenderer.renderAdvancedSkin(this, renderData, null, null, convertTreeToAdvancedPartNode());

        
        ModRenderHelper.enableAlphaBlend();

        
        
        GlStateManager.disablePolygonOffset();
        GlStateManager.disableDepth();
        
        //ModRenderHelper.disableAlphaBlend();
        GlStateManager.disableNormalize();
        GlStateManager.disableColorMaterial();
        
        RenderHelper.disableStandardItemLighting();
        GlStateManager.resetColor();
        
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == buttonAdd) {
            treeView.addItem(new GuiTreeViewPartNote("New Node"), treeView.getSelectedIndex());
        }
        if (button == buttonRemove) {
            treeView.removeItem(treeView.getSelectedIndex());
        }
        if (button == checkBoxSetting) {
            applySetting();
        }
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        applySetting();
    }

    @Override
    public String getName() {
        return LibBlockNames.ADVANCED_SKIN_BUILDER;
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        if (dropDownList == dropDownSkinType) {
            updateActiveSkinType();
        }
        if (dropDownList == dropDownNoteSettings) {
            updateSetting();
        }
    }

    private PartNodeSetting getActiveSetting() {
        DropDownListItem listItem = dropDownNoteSettings.getListSelectedItem();
        if (listItem != null) {
            return PartNodeSetting.valueOf(listItem.tag);
        }
        return null;
    }

    @Override
    public void onSelectionChange(GuiTreeView guiTreeView, IGuiTreeViewItem selectedItem) {
        updateSettingsDropDown();
        updateSetting();
    }

    @Override
    public SkinPart getAdvancedPart(int index) {
        if (index == 1) {
            return null;
        }
        Skin skin = null;
        ItemStack stack = getContainer().getSlot(0).getStack();
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (descriptor != null) {
            skin = ClientSkinCache.INSTANCE.getSkin(descriptor);
            if (skin != null) {
                return skin.getParts().get(0);
            }
        }
        return null;
    }

    @Override
    public AdvancedPartNode getAdvancedPartNode(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    public static class GuiTreeViewPartNote extends GuiTreeViewItem {

        private AdvancedPartNode advancedPartNode;
        private ISkinIdentifier skinIdentifier;

        public GuiTreeViewPartNote(String name) {
            super(name);
            advancedPartNode = new AdvancedPartNode(0, name);
        }

        @Override
        public void setName(String name) {
            super.setName(name);
            advancedPartNode.name = name;
        }

        public AdvancedPartNode getAdvancedPartNode() {
            return advancedPartNode;
        }

        public ISkinIdentifier getSkinIdentifier() {
            return skinIdentifier;
        }

        public void setSkinIdentifier(ISkinIdentifier skinIdentifier) {
            this.skinIdentifier = skinIdentifier;
        }
    }

    public static enum PartNodeSetting {

        NAME(false), SKIN(true), ENABLED(true), SCALE(false), MIRROR(true), POSITION(false), ROTATION(false), ROTATION_POSITION(false);

        private boolean onRoot;

        private PartNodeSetting(boolean onRoot) {
            this.onRoot = onRoot;
        }
    }
}
